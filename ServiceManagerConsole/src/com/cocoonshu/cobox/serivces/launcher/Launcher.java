package com.cocoonshu.cobox.serivces.launcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.cocoonshu.cobox.linkservice.LinkService;
import com.cocoonshu.cobox.linkservice.WebService;
import com.cocoonshu.cobox.serivces.IRMIService;
import com.cocoonshu.cobox.serivces.RMIService;
import com.cocoonshu.cobox.serivces.Service;
import com.cocoonshu.cobox.utils.Log;

public class Launcher {
	
	public static final String TAG                   = "Launcher";
	public static final int    ERR_EXCEPTED_ARGUMENT = 0x0001;
	public static final int    ERR_EXCEPTED_OPTION   = 0x0002;
	public static final int    ERR_EXCEPTED_SERVICE  = 0x0003;
	
	private IRMIService mRMIClient = null;
	
	public static enum Command {
		START_SERVICE("-start", 1),
		STOP_SERVICE ("-stop",  2),
		LIST_SERVICES("-list",  3),
		HELP         ("-help",  0),
		QUIT         ("quit",  -1),
		EXIT         ("exit",  -1);
		
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
		Log.setConsoleOutput(true);
		Log.setLogFileOutput(true);
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread() {

			@Override
			protected void onShutdown() {
				destoryRMIClient();
				Log.i(TAG, "Terminated...\n");
				Log.setConsoleOutput(false);
				Log.setLogFileOutput(false);
			}
			
		});
	}
	
	public void createRMIClient() {
		String rmiUrl = RMIService.bindString("localhost");
		try {
			mRMIClient = (IRMIService) Naming.lookup(RMIService.bindString("localhost"));
		} catch (ConnectException e) {
			Log.e(TAG, "RMI service isn't resigter: " + rmiUrl, e);
			System.exit(-1);
		} catch (NotBoundException e) {
			Log.e(TAG, "RMI service isn't resigter: " + rmiUrl, e);
			System.exit(-1);
		} catch (RemoteException e) {
			Log.e(TAG, "Cannot bind RMI service", e);
			System.exit(-1);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Cannot bind RMI service with wrong RMI url: " + rmiUrl, e);
			System.exit(-1);
		}

		Log.i(TAG, "RMI client bound, RMI: " + rmiUrl);
	}
	
	public void destoryRMIClient() {
		String rmiUrl = RMIService.bindString("localhost");
		if (mRMIClient != null) {
			mRMIClient = null;
		}
	}
	
	public void execCommand(String[] args) throws RemoteException {
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
						Class argClass = mRMIClient.getClassObjectFromName(arg);
						if (argClass != null && !startServiceNames.contains(argClass)) {
							startServiceNames.add(argClass);
						} else {
							printError(ERR_EXCEPTED_SERVICE, arg);
							return;
						}
					} else if (currentCommand == Command.STOP_SERVICE) {
						Class argClass = mRMIClient.getClassObjectFromName(arg);
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
		if (mRMIClient != null) {
			// Stop services
			if (stopServiceNames.size() > 0) {
				Log.println();
				Log.i(TAG, "Stop Services:");
				for (Class clazz : stopServiceNames) {
					// Stopping services
					Log.i(TAG, "    " + clazz.getSimpleName() + " ... ", true);
					Service service = mRMIClient.stopService(clazz); 
					if (service != null && service.isLaunched()) {
						Log.println("successed");
					} else {
						Log.println("failed");
					}
				}
			}
			// Start services
			if (startServiceNames.size() > 0) {
				Log.println();
				Log.i(TAG, "Start Services:");
				for (Class clazz : startServiceNames) {
					// Starting services
					Log.i(TAG, "    " + clazz.getSimpleName() + " ... ", true);
					Service service = mRMIClient.startService(clazz); 
					if (service != null) {
						if (!service.isServering()) {
							Log.println("successed");
						} else {
							Log.println("already launched");
						}
					} else {
						Log.println("failed");
					}
				}
			}
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

	private void listServicesList() throws RemoteException {
		Log.println();
		Log.i(TAG, "Registered services:");
		
		if (mRMIClient != null) {
			List<Class<? extends Service>> registerServices = mRMIClient.listServicesList();
			for (int i = 0; i < registerServices.size(); i++) {
				Log.i(TAG, "    " + registerServices.get(i).getSimpleName());
			}
		} else {
			Log.i(TAG, "    (RMI client hasn't bound yet)");
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

		int    inputCount  = 0;
		byte[] inputBuffer = new byte[1024];
		while (true) {
			try {
				inputCount = System.in.read(inputBuffer);
				if (inputCount > 0) {
					String command = new String(inputBuffer).trim();
					if (command.equalsIgnoreCase(Command.QUIT.toString())
					 || command.equalsIgnoreCase(Command.EXIT.toString())) {
						System.exit(0);
					}
				}
				
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Main entry of Application
	 * @param args
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException {
		Launcher launcher = new Launcher();
		launcher.createRMIClient();
		launcher.execCommand(args);
		launcher.mainLoop();
	}

}
