package com.cocoonshu.cobox.serivces.launcher;

public abstract class ShutdownHookThread extends Thread {
	
	protected abstract void onShutdown();
	
	@Override
	public void run() {
		super.run();
		try {
			onShutdown();
		} catch (Throwable thr) {
			thr.printStackTrace();
		}
	}
	
}
