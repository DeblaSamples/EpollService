package com.cocoonshu.cobox.serivces;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import com.cocoonshu.cobox.utils.Log;

public class ServiceManager {

	private static final String TAG = "ServiceManager";
	private RMIService mRMIService = null;
	
	public ServiceManager() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				destoryRMIServer();
				Log.i(TAG, "Terminated...\n");
			}
			
		});
	}
	
	public void createRMIServer() {
		String rmiUrl = RMIService.bindString("localhost");
		try {
			mRMIService = new RMIService();
			mRMIService.start();
		} catch (RemoteException e) {
			Log.e(TAG, "Cannot bind RMI service", e);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Cannot bind RMI service with wrong RMI url: " + rmiUrl, e);
		} catch (AlreadyBoundException e) {
			Log.e(TAG, "RMI service has already bound", e);
		}

		Log.i(TAG, "RMI service bound, RMI: " + rmiUrl);
	}
	
	public void destoryRMIServer() {
		String rmiUrl = RMIService.bindString("localhost");
		if (mRMIService != null) {
			try {
				mRMIService.stop();
			} catch (RemoteException e) {
				Log.e(TAG, "Cannot unbind RMI service", e);
			} catch (MalformedURLException e) {
				Log.e(TAG, "Cannot unbind RMI service with wrong RMI url: " + rmiUrl, e);
			}
			Log.i(TAG, "RMI service unbound, RMI: " + rmiUrl);
		}
	}
	
	public static void main(String[] args) {
		ServiceManager serviceManager = new ServiceManager();
		serviceManager.createRMIServer();
	}
	
}
