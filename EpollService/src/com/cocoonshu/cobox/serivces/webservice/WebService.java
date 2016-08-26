package com.cocoonshu.cobox.serivces.webservice;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.cocoonshu.cobox.serivces.Service;
import com.cocoonshu.cobox.utils.Log;


public class WebService extends Service implements Serializable,
                                                   CompletionHandler<AsynchronousSocketChannel, Object> {

	private static final String                          TAG              = "WebService";
	private static final long                            serialVersionUID = 54604139032979549L;
	private static final int                             ListenPort       = 80;
	private              AsynchronousServerSocketChannel mServerChannel   = null;
	
	@Override
	protected void onCreate() {
		Log.i(TAG, "[onCreate]");
		
		// TODO Auto-generated method stub
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "[onStart]");
		
		// TODO Auto-generated method stub
		newEpollServerSocket();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "[onStop]");
		
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestory() {
		Log.i(TAG, "[onDestory]");
		
		// TODO Auto-generated method stub
		
	}

	///////////////////////////////////////////////////////////////////////
	
	private void newEpollServerSocket() {
		try {
			mServerChannel = AsynchronousServerSocketChannel.open()
					         .bind(new InetSocketAddress(ListenPort));
			Log.i(TAG, "AsynchronousServerSocket has created and bind to port:" + ListenPort);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Create asynchronous server socket channel failed, web service cannot launch", e);
			mServerChannel = null;
		}
	}

	private void acceptAsynchronousSocket() {
		
	}
	
	@Override
	public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void failed(Throwable exception, Object attachment) {
		// TODO Auto-generated method stub
		
	}
}
