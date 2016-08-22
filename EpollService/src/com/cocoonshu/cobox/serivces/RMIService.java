package com.cocoonshu.cobox.serivces;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RMIService extends UnicastRemoteObject implements IRMIService {

	private static final long        serialVersionUID = 1003663922698814641L;
	private              IRMIService mServiceProxy    = null;

	public static String bindString(String address) {
		return "rmi://" + address + ":" + RMIPort + "/" + RMIServiceName;
	}
	
	public RMIService() throws RemoteException {
		super();
	}

	public RMIService(int port) throws RemoteException {
		super(port);
	}
	
	public RMIService(int port,
			RMIClientSocketFactory clientSocketFactory,
			RMIServerSocketFactory serverSocketFactory) throws RemoteException {
		super(port, clientSocketFactory, serverSocketFactory);
	}
	
	public void setServiceProxy(IRMIService serviceProxy) {
		mServiceProxy = serviceProxy;
	}

	public void start() throws RemoteException, AlreadyBoundException, MalformedURLException, AlreadyBoundException {
		LocateRegistry.createRegistry(RMIService.RMIPort);
		Naming.bind(RMIService.bindString("localhost"), this);
	}

	public void stop() throws RemoteException, MalformedURLException {
		Naming.rebind(RMIService.bindString("localhost"), this);
	}

	@Override
	public Service startService(Class<? extends Service> serviceClass) throws RemoteException{
		return mServiceProxy != null ? mServiceProxy.startService(serviceClass) : null;
	}

	@Override
	public Service stopService(Class<? extends Service> serviceClass) throws RemoteException {
		return mServiceProxy != null ? mServiceProxy.stopService(serviceClass) : null;
	}

	@Override
	public Class<? extends Service> getClassObjectFromName(String name) throws RemoteException {
		return mServiceProxy != null ? mServiceProxy.getClassObjectFromName(name) : null;
	}

	@Override
	public List<Class<? extends Service>> listServicesList() throws RemoteException {
		return mServiceProxy != null ? mServiceProxy.listServicesList() : null;
	}
	
}
