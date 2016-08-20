package com.cocoonshu.cobox.serivces;

import java.rmi.Remote;

public interface IRMIService extends Remote {

	public static final int    RMIPort        = 3529;
	public static final String RMIServiceName = "EpollServiceManagerRMI";

}
