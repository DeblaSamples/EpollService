package com.cocoonshu.cobox.serivces.webservice;

import java.util.concurrent.ThreadFactory;

public class WebSocketThreadFactory implements ThreadFactory {

	private static final String ThreadName     = "WebService-Socket #";
	private              int    mThreadCounter = 0;
	
	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setName(ThreadName + (mThreadCounter++));
		return null;
	}

}
