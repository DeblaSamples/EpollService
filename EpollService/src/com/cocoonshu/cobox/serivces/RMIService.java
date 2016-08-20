package com.cocoonshu.cobox.serivces;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class RMIService extends UnicastRemoteObject implements IRMIService {

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

	public void start() throws RemoteException, AlreadyBoundException, MalformedURLException, AlreadyBoundException {
		LocateRegistry.createRegistry(RMIService.RMIPort);
		Naming.bind(RMIService.bindString("localhost"), this);
	}

	public void stop() throws RemoteException, MalformedURLException {
		Naming.rebind(RMIService.bindString("localhost"), this);
	}
	
}
