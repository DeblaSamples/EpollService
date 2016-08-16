package com.cocoonshu.cobox.serivces;

public abstract class Service {

	protected abstract void onCreate();
	protected abstract void onDestory();
	
	public void launch() {
		
	}
	
	public void terminate() {
		
	}
	
	public boolean isLaunched() {
		return false;
	}
	
	public boolean isServering() {
		return false;
	}
	
}
