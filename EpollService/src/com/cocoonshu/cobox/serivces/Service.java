package com.cocoonshu.cobox.serivces;

public abstract class Service {

	public static enum ServiceState {
		Initialized,
		Launching,
		Servering,
		Terminating,
		Terminated
	}
	
	private ServiceState  mServiceState  = null;
	private ServiceThread mServiceThread = null;
	
	protected abstract void onCreate();
	protected abstract void onStart();
	protected abstract void onStop();
	protected abstract void onDestory();
	
	public Service() {
		mServiceState = ServiceState.Initialized;
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
			mServiceThread = new ServiceThread();
			mServiceThread.launch();
			mServiceState = ServiceState.Launching;
		}
	}
	
	public void terminate() {
		if (mServiceState == ServiceState.Launching
				|| mServiceState == ServiceState.Servering) {
			// Notify service to terminated without waiting it done
			if (mServiceThread != null) {
				mServiceThread.terminate();
			}
			mServiceState = ServiceState.Terminating;
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
