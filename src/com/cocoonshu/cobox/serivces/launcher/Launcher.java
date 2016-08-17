package com.cocoonshu.cobox.serivces.launcher;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.cocoonshu.cobox.linkservice.LinkService;
import com.cocoonshu.cobox.linkservice.WebService;
import com.cocoonshu.cobox.serivces.Service;

public class Launcher {
	
	public static final int ERR_EXCEPTED_ARGUMENT = 0x0001;
	public static final int ERR_EXCEPTED_OPTION   = 0x0002;
	public static final int ERR_EXCEPTED_SERVICE  = 0x0003;
	
	private List<Class<? extends Service>> mRegisteredServices = null;
	private Stack<Service>                 mRunningServices    = null;
	
	public static enum Command {
		START_SERVICE("-start", 1),
		STOP_SERVICE ("-stop",  2),
		LIST_SERVICES("-list",  3),
		HELP         ("-help",  0);
		
		private String mName = null;
		private int    mId   = 0;
		
		private Command(String name, int id) {
			mName = name;
			mId   = id;
		}
		
		@Override
		public String toString() {
			return mName;
		}
		
		public int getId() {
			return mId;
		}
	}
	
	public Launcher() {
		mRegisteredServices = new LinkedList<Class<? extends Service>>();
		mRunningServices    = new Stack<Service>();
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread() {

			@Override
			protected void onShutdown() {
				System.out.println("Terminated...\n");
			}
			
		});
	}
	
	public Launcher registerServices(Class<? extends Service> service) {
		if (!mRegisteredServices.contains(service)) {
			mRegisteredServices.add(service);
		}
		return this;
	}
	
	public void execCommand(String[] args) {
		if (args == null || args.length == 0) {
			printHelpInformation();
			return;
		}
		
		int                            argsCount         = args.length;
		List<Class<? extends Service>> startServiceNames = new LinkedList<>();
		List<Class<? extends Service>> stopServiceNames  = new LinkedList<>();
		Command                        currentCommand    = null;
		
		for (int i = 0; i < argsCount; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase(Command.HELP.toString())) {
				currentCommand = Command.HELP;
				printHelpInformation();
			} else if (arg.equalsIgnoreCase(Command.START_SERVICE.toString())) {
				currentCommand = Command.START_SERVICE;
			} else if (arg.equalsIgnoreCase(Command.STOP_SERVICE.toString())) {
				currentCommand = Command.STOP_SERVICE;
			} else if (arg.equalsIgnoreCase(Command.LIST_SERVICES.toString())) {
				currentCommand = Command.STOP_SERVICE;
				listServicesList();
			} else {
				if (currentCommand != null) {
					if (currentCommand == Command.START_SERVICE) {
						Class argClass = getClassObjectFromName(arg);
						if (argClass != null && !startServiceNames.contains(argClass)) {
							startServiceNames.add(argClass);
						} else {
							printError(ERR_EXCEPTED_SERVICE, arg);
							return;
						}
					} else if (currentCommand == Command.STOP_SERVICE) {
						Class argClass = getClassObjectFromName(arg);
						if (argClass != null && !stopServiceNames.contains(argClass)) {
							stopServiceNames.add(argClass);
						} else {
							printError(ERR_EXCEPTED_SERVICE, arg);
							return;
						}
					}
				} else {
					if (arg.startsWith("-")) {
						printError(ERR_EXCEPTED_OPTION, arg);
						return;
					} else {
						printError(ERR_EXCEPTED_ARGUMENT, arg);
						return;
					}
				}
			}
		}
		
		// Execute command
		if (stopServiceNames.size() > 0) {
			System.out.println();
			System.out.println("Stop Services:");
			for (Class clazz : stopServiceNames) {
				// Stopping services
				System.out.print("    " + clazz.getSimpleName() + " ... ");
				Service service = stopService(clazz); 
				if (service != null && service.isLaunched()) {
					System.out.println("successed");
				} else {
					System.out.println("failed");
				}
			}
		}
		if (startServiceNames.size() > 0) {
			System.out.println();
			System.out.println("Start Services:");
			for (Class clazz : startServiceNames) {
				// Starting services
				System.out.print("    " + clazz.getSimpleName() + " ... ");
				Service service = startService(clazz); 
				if (service != null && !service.isServering()) {
					System.out.println("successed");
				} else {
					System.out.println("failed");
				}
			}
		}
	}

	private Class getClassObjectFromName(String name) {
		if (name != null) {
			int loopSize = mRegisteredServices.size();
			for (int i = 0; i < loopSize; i++) {
				if (name.equalsIgnoreCase(mRegisteredServices.get(i).getSimpleName())) {
					return mRegisteredServices.get(i);
				}
			}
		}
		return null;
	}

	private Service startService(Class<? extends Service> serviceClass) {
		Service service = null;
		if (!hasSameRunningServiceClazz(serviceClass)) {
			try {
				service = serviceClass.newInstance();
				synchronized (mRunningServices) {
					mRunningServices.push(service);
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if (service != null) {
			service.launch();
		}
		return service;
	}
	
	private Service stopService(Class<? extends Service> serviceClass) {
		Service service = null;
		if (hasSameRunningServiceClazz(serviceClass)) {
			synchronized (mRunningServices) {
				Stack<Service> runningServices = mRunningServices;
				for (Service runningService : runningServices) {
					if (runningService.getClass().equals(serviceClass)) {
						service = runningService;
						runningServices.remove(runningService);
					}
				}
			}
		}
		if (service != null) {
			service.terminate();
		}
		return service;
	}
	
	private boolean hasSameRunningServiceClazz(Class<? extends Service> serviceClass) {
		synchronized (mRunningServices) {
			Stack<Service> runningServices = mRunningServices;
			for (Service service : runningServices) {
				if (service.getClass().equals(serviceClass)) {
					return true;
				}
			}
			return false;
		}
	}
	
	private void printError(int error, String arg) {
		switch (error) {
		case ERR_EXCEPTED_OPTION:
			System.out.println("Error: Excepted option: " + arg);
			break;
			
		case ERR_EXCEPTED_SERVICE:
			System.out.println("Error: Excepted service: " + arg);
			break;
			
		case ERR_EXCEPTED_ARGUMENT:
			System.out.println("Error: Excepted argument: " + arg);
			break;

		default:
			System.out.println("Error: Unknown error");
			break;
		}
	}

	private void listServicesList() {
		System.out.println();
		System.out.println("Registered services:");
		for (int i = 0; i < mRegisteredServices.size(); i++) {
			System.out.println("    " + mRegisteredServices.get(i).getSimpleName());
		}
	}

	private void printHelpInformation() {
		System.out.println();
		System.out.println("Usage: launcher OPTION [Arguments]");
		System.out.println(" -list                              list services");
		System.out.println(" -start [service1, [service2] ...]  start services");
		System.out.println(" -stop  [service1, [service2] ...]  stop services");
		System.out.println(" -help                              help information");
	}

	private void mainLoop() {
		// TODO Update the states of the each service thread,
		//      and display information on Console
		
		while (true) {
			
		}
	}
	
	/**
	 * Main entry of Application
	 * @param args
	 */
	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		launcher.registerServices(LinkService.class)
		        .registerServices(WebService.class);
		launcher.execCommand(args);
		launcher.mainLoop();
	}

}
