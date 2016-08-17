package com.cocoonshu.cobox.serivces;

public abstract class Service {

	public static enum ServiceState {
		Initialized,
		Launching,
		Servering,
		Terminating,
		Terminated
	}
	
	private ServiceState mServiceState  = null;
	private Thread       mServiceThread = null;
	
	protected abstract void onCreate();
	protected abstract void onDestory();
	
	public Service() {
		mServiceState = ServiceState.Initialized;
	}
	
	public void launch() {
		// TODO
	}
	
	public void terminate() {
		// TODO
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
