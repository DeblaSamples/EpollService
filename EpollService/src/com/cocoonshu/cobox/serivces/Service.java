package com.cocoonshu.cobox.serivces;

import com.cocoonshu.cobox.serivces.ServiceThread.OnStateChangedListener;

public abstract class Service {

	public static enum ServiceState {
		Initialized,
		Launching,
		Servering,
		Terminating,
		Terminated
	}
	
	private ServiceState           mServiceState           = null;
	private ServiceThread          mServiceThread          = null;
	private OnStateChangedListener mOnStateChangedListener = null;
	
	protected abstract void onCreate();
	protected abstract void onStart();
	protected abstract void onStop();
	protected abstract void onDestory();
	
	public Service() {
		mServiceState = ServiceState.Initialized;
		setupServiceThreadListener();
	}
	
	private void setupServiceThreadListener() {
		mOnStateChangedListener = new OnStateChangedListener() {
			
			@Override
			public void onThreadLaunching() {
				mServiceState = ServiceState.Launching;
				onCreate();
			}
			
			@Override
			public void onThreadLaunched() {
				mServiceState = ServiceState.Servering;
				onStart();
			}
			
			@Override
			public void onThreadRun() {
				mServiceState = ServiceState.Servering;
			}
			
			@Override
			public void onThreadTerminating() {
				mServiceState = ServiceState.Terminating;
				onStop();
			}
			
			@Override
			public void onThreadTerminated() {
				mServiceState = ServiceState.Terminated;
				onDestory();
			}
			
		};
	}
	
	public void launch() {
		if (mServiceState == ServiceState.Initialized
				|| mServiceState == ServiceState.Terminating
				|| mServiceState == ServiceState.Terminated) {
			// Wait for service terminated before re-launching an new Service
			// to release socket IP address and port in case in conflicting
			if (mServiceThread != null) {
				mServiceThread.terminate();
				mServiceThread.waitForTerminated();
			}
			mServiceThread = new ServiceThread(mOnStateChangedListener);
			mServiceThread.launch();
		}
	}
	
	public void terminate() {
		if (mServiceState == ServiceState.Launching
				|| mServiceState == ServiceState.Servering) {
			// Notify service to terminated without waiting it done
			if (mServiceThread != null) {
				mServiceThread.terminate();
			}
		}
	}
	
	public boolean isLaunched() {
		return mServiceState != null
				&& (mServiceState == ServiceState.Launching
				    || mServiceState == ServiceState.Servering);
	}
	
	public boolean isServering() {
		return mServiceState != null
				&& mServiceState == ServiceState.Servering;
	}
	
}
