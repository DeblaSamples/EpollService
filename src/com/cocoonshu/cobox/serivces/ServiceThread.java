package com.cocoonshu.cobox.serivces;

public class ServiceThread extends Thread {

	private static final int ThreadState_Initialized = 0;
	private static final int ThreadState_Launching   = 1;
	private static final int ThreadState_Launched    = 2;
	private static final int ThreadState_Terminating = 3;
	private static final int ThreadState_Terminated  = 4;
	
	public interface OnStateChangedListener {
		void onThreadLaunching();
		void onThreadLaunched();
		void onThreadRun();
		void onThreadTerminating();
		void onThreadTerminated();
	}
	
	private volatile int           mThreadState            = ThreadState_Initialized;
	private Object                 mLaunchingLock          = new Object();
	private Object                 mTerminatingLock        = new Object();
	private OnStateChangedListener mOnStateChangedListener = null;
	
	public void setOnStateChangedListener(OnStateChangedListener listener) {
		mOnStateChangedListener = listener;
	}
	
	public void launch() {
		if (mThreadState == ThreadState_Initialized) {
			this.start();
			mThreadState = ThreadState_Launching;
			fireThreadLaunchingEvent();
		}
	}
	
	public void terminate() {
		
	}
	
	public void waitForLaunched() {
		if (mThreadState == ThreadState_Launching) {
			synchronized (mLaunchingLock) {
				try {
					mLaunchingLock.wait();
				} catch (InterruptedException exp) {
					// if service has launching completed, this
					// lock will be notified
				}
			}
		}
	}
	
	public void waitForTerminated() {
		if (mThreadState == ThreadState_Terminating) {
			synchronized (mTerminatingLock) {
				try {
					mTerminatingLock.wait();
				} catch (InterruptedException exp) {
					// if service has launching completed, this
					// lock will be notified
				}
			}
		}
	}
	
	@Override
	public void run() {
		synchronized (mLaunchingLock) {
			mLaunchingLock.notifyAll();
		}
		mThreadState = ThreadState_Launched;
		fireThreadLaunchedEvent();
		
		try {
			fireThreadRunEvent();
		} catch (InterruptedException exp) {
			
		}
	}
	
	protected OnStateChangedListener getOnStateChangedListener() {
		return mOnStateChangedListener;
	}
	
	private void fireThreadLaunchingEvent() {
		if (mOnStateChangedListener != null) {
			mOnStateChangedListener.onThreadLaunching();
		}
	}
	
	private void fireThreadLaunchedEvent() {
		if (mOnStateChangedListener != null) {
			mOnStateChangedListener.onThreadLaunched();
		}
	}
	
	private void fireThreadRunEvent() {
		if (mOnStateChangedListener != null) {
			mOnStateChangedListener.onThreadRun();
		}
	}
	
	private void fireThreadTerminatingEvent() {
		if (mOnStateChangedListener != null) {
			mOnStateChangedListener.onThreadTerminating();
		}
	}
	
	private void fireThreadTerminatedEvent() {
		if (mOnStateChangedListener != null) {
			mOnStateChangedListener.onThreadTerminated();
		}
	}
}
