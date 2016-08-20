package com.cocoonshu.cobox.serivces.launcher;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.cocoonshu.cobox.linkservice.LinkService;
import com.cocoonshu.cobox.linkservice.WebService;
import com.cocoonshu.cobox.serivces.Service;
import com.cocoonshu.cobox.utils.Log;

public class Launcher {
	
	public static final String TAG                   = "Launcher";
	public static final int    ERR_EXCEPTED_ARGUMENT = 0x0001;
	public static final int    ERR_EXCEPTED_OPTION   = 0x0002;
	public static final int    ERR_EXCEPTED_SERVICE  = 0x0003;
	
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
		
		Log.setConsoleOutput(true);
		Log.setLogFileOutput(true);
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread() {

			@Override
			protected void onShutdown() {
				Log.i(TAG, "Terminated...\n");
				Log.setConsoleOutput(false);
				Log.setLogFileOutput(false);
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
			Log.println();
			Log.i(TAG, "Stop Services:");
			for (Class clazz : stopServiceNames) {
				// Stopping services
				Log.i(TAG, "    " + clazz.getSimpleName() + " ... ", true);
				Service service = stopService(clazz); 
				if (service != null && service.isLaunched()) {
					Log.println("successed");
				} else {
					Log.println("failed");
				}
			}
		}
		if (startServiceNames.size() > 0) {
			Log.println();
			Log.i(TAG, "Start Services:");
			for (Class clazz : startServiceNames) {
				// Starting services
				Log.i(TAG, "    " + clazz.getSimpleName() + " ... ", true);
				Service service = startService(clazz); 
				if (service != null && !service.isServering()) {
					Log.println("successed");
				} else {
					Log.println("failed");
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

	/**
	 * FIXME This method should call the Service Manager Process to start service
	 * @param serviceClass
	 * @return
	 */
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
	
	/**
	 * FIXME This method should call the Service Manager Process to stop service
	 * @param serviceClass
	 * @return
	 */
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
			Log.e(TAG, "Error: Excepted option: " + arg);
			break;
			
		case ERR_EXCEPTED_SERVICE:
			Log.e(TAG, "Error: Excepted service: " + arg);
			break;
			
		case ERR_EXCEPTED_ARGUMENT:
			Log.e(TAG, "Error: Excepted argument: " + arg);
			break;

		default:
			Log.e(TAG, "Error: Unknown error");
			break;
		}
	}

	private void listServicesList() {
		Log.println();
		Log.i(TAG, "Registered services:");
		for (int i = 0; i < mRegisteredServices.size(); i++) {
			Log.i(TAG, "    " + mRegisteredServices.get(i).getSimpleName());
		}
	}

	private void printHelpInformation() {
		Log.println();
		Log.i(TAG, "Usage: launcher OPTION [Arguments]");
		Log.i(TAG, " -list                              list services");
		Log.i(TAG, " -start [service1, [service2] ...]  start services");
		Log.i(TAG, " -stop  [service1, [service2] ...]  stop services");
		Log.i(TAG, " -help                              help information");
	}

	private void mainLoop() {
		// TODO Update the states of the each service thread,
		//      and display information on Console
		
		// STEP1 Launch Service Manager Process if the process isn't launch
		// STEP2 Connect to the print stream of the Service Manager Process 
		// STEP3 Output print stream from Service Manager Process in loop
		
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
