package com.cocoonshu.cobox.linkservice;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

import com.cocoonshu.cobox.serivces.Service;
import com.cocoonshu.cobox.utils.Log;


public class WebService extends Service implements Serializable {

	private static final String                          TAG              = "WebService";
	private static final long                            serialVersionUID = 54604139032979549L;
	private static final int                             ListenPort       = 80;
	private              AsynchronousServerSocketChannel mServerChannel   = null;
	
	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		try {
			mServerChannel = AsynchronousServerSocketChannel.open()
					         .bind(new InetSocketAddress(ListenPort));
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Create asynchronous server socket channel failed, web service cannot launch", e);
			mServerChannel = null;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestory() {
		// TODO Auto-generated method stub
		
	}

}
