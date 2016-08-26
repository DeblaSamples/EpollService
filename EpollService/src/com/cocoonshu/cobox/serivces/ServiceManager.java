package com.cocoonshu.cobox.serivces;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.cocoonshu.cobox.serivces.webservice.WebService;
import com.cocoonshu.cobox.service.linkservice.LinkService;
import com.cocoonshu.cobox.utils.Log;

public class ServiceManager implements IRMIService {

	private static final String TAG = "ServiceManager";
	
	private List<Class<? extends Service>> mRegisteredServices = null;
	private Stack<Service>                 mRunningServices    = null;
	private RMIService                     mRMIService         = null;
	
	public ServiceManager() {
		mRegisteredServices = new LinkedList<Class<? extends Service>>();
		mRunningServices    = new Stack<Service>();
		Runtime.getRuntime().addShutdownHook(new Thread() {

			private ServiceManager mServiceManager = ServiceManager.this;
			
			@Override
			public void run() {
				destoryRMIServer();
				mServiceManager.terminate();
				Log.i(TAG, "Terminated...\n");
			}
			
		});
	}
	
	public void createRMIServer() {
		String rmiUrl = RMIService.bindString("localhost");
		try {
			mRMIService = new RMIService();
			mRMIService.setServiceProxy(this);
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
	
	public ServiceManager registerServices(Class<? extends Service> service) {
		if (!mRegisteredServices.contains(service)) {
			mRegisteredServices.add(service);
		}
		return this;
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
	
	protected void terminate() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Service startService(Class<? extends Service> serviceClass) {
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

	@Override
	public Service stopService(Class<? extends Service> serviceClass) {
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

	@Override
	public Class<? extends Service> getClassObjectFromName(String name) {
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

	@Override
	public List<Class<? extends Service>> listServicesList() {
		return mRegisteredServices;
	}
	
	public static void main(String[] args) {
		ServiceManager serviceManager = new ServiceManager();
		serviceManager.createRMIServer();
		serviceManager.registerServices(LinkService.class)
                      .registerServices(WebService.class);
	}
}
