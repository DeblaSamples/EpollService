package com.cocoonshu.cobox.serivces.webservice;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class ClientSocketChannelProxy {

	private ThreadPoolExecutor mTaskExecutor  = null;
	private ThreadFactory      mThreadFactory = null;
	
	public ClientSocketChannelProxy(ThreadFactory threadFactory) {
		mTaskExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool(mThreadFactory);
	}
	
}
