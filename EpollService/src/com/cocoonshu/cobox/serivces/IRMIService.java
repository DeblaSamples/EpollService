package com.cocoonshu.cobox.serivces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRMIService extends Remote {

	public static final int    RMIPort        = 3529;
	public static final String RMIServiceName = "EpollServiceManagerRMI";

	public Service                        startService(Class<? extends Service> serviceClass) throws RemoteException;
	public Service                        stopService(Class<? extends Service> serviceClass) throws RemoteException;
	public Class<? extends Service>       getClassObjectFromName(String name) throws RemoteException;
	public List<Class<? extends Service>> listServicesList() throws RemoteException;
}
