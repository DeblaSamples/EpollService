package com.cocoonshu.cobox.log;

public class Log {

	public static final void i(String tag, String msg) {
		System.out.println("[i][" + tag + "] " + msg);
	}
	
	public static final void w(String tag, String msg) {
		System.out.println("[w][" + tag + "] " + msg);
	}
	
	public static final void e(String tag, String msg) {
		System.err.println("[e][" + tag + "] " + msg);
	}
}
