package com.cocoonshu.cobox.serivces.webservice;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import com.cocoonshu.cobox.utils.Log;

public class ClientAcceptThread extends Thread {

	private static final String TAG = "ClientAcceptThread";
	
	private AsynchronousServerSocketChannel mServerSocketChannel      = null;
	private ClientSocketChannelProxy        mClientSocketChannelProxy = null;
	
	public ClientAcceptThread(
			AsynchronousServerSocketChannel serverSocketChannel,
			ClientSocketChannelProxy        clientSocketChannelProxy) {
		mServerSocketChannel      = serverSocketChannel;
		mClientSocketChannelProxy = clientSocketChannelProxy;
    }
	
	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				AsynchronousServerSocketChannel serverSocketChannel = mServerSocketChannel;
				if (serverSocketChannel == null) {
					Log.e(TAG, "[ClientAcceptThread] AsynchronousServerSocketChannel is null, thread abort.");
					return;
				}
				
				Future<AsynchronousSocketChannel> handler = serverSocketChannel.accept();
			}
		} catch (Throwable thr) {
			thr.printStackTrace();
		}
	}
}
