package org.smartfrog.services.persistence.recoverablecomponent;

import java.lang.reflect.*;
import java.io.*;
import java.rmi.*;
import java.rmi.server.*;

import org.smartfrog.services.persistence.storage.*;

public class RComponentProxyInvocationHandler implements InvocationHandler, Serializable {

	static final long serialVersionUID = 0L;
	Object DirectObject;
	RComponentProxyLocator proxylocator;
	RComponentProxyStubImpl RPPStub;
	
	public RComponentProxyInvocationHandler(RComponent component){
		DirectObject = component;
		
		try{
			proxylocator = component.getProxyLocator();
		} catch (RemoteException exc){
			throw new RuntimeException("Proxy was being remotely created!",exc);
		}
		
		RPPStub = new RComponentProxyStubImpl(proxylocator); 
	}
	
    static public Object sfGetProxy(RComponent obj) {
    	
    	Class[] objinterfvector = obj.getClass().getInterfaces();
    	Class[] interfvector = new Class[objinterfvector.length+1];

    	interfvector[0] = RComponentProxyStub.class;
    	for (int i =0; i< objinterfvector.length; i++){
    		interfvector[i+1] = objinterfvector[i];
    	}
    	
    	return java.lang.reflect.Proxy.newProxyInstance(
    	       obj.getClass().getClassLoader(),
    	       interfvector,
    	       new RComponentProxyInvocationHandler(obj));
    }		

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		Object result = null;
		boolean finished=true;
		
		//System.out.println("\nINVOKE WAS CALLED with"+ method);
		
		try{
			result = method.invoke(RPPStub,args);
		} catch (InvocationTargetException exc) {
			throw exc.getTargetException();
		} catch (IllegalArgumentException exc2){
			finished = false;
		}

		if( finished ) return result;
		
		try{
				//System.out.println("Directly calling"+ DirectObject+"\n");
				result = method.invoke(DirectObject,args);
		} catch (InvocationTargetException exc){
		    Throwable e = exc.getTargetException();
		    while (e instanceof RemoteException){

		    	//System.out.println("Apparently the object was not there:"+e);
		    	try{
		    		Thread.sleep(RComponent.StubWait);
		    	} catch (InterruptedException exc2){}

		    	//System.out.println("Verifying "+proxylocator); 
		    	try{
		    		if(proxylocator.isDead())
		    			throw new RemoteException("Component already terminated.");
		    		DirectObject = proxylocator.getRComponentStub();

		    		e = null;
		    		//System.out.println("\nNew object is " + DirectObject+"\nTrying againd "+method);
		    		result = method.invoke(DirectObject,args);
		    	} catch (ProxyLocatorException exc2) { // problems getting the new stub - retry later
		    		// notice that e has not been assigned null if this exception was thrown
		    	} catch (InvocationTargetException exc2){ // problems during the invocation
		    		e = exc2.getTargetException();
		    	} 

		    	if (e == null){ // correct execution
		    		return result;
		    	}

		    	// in any case, if sfPing and not dead, returns OK 
		    	if (method.getName().equals("sfPing")){
		    		return null;
		    	}
		    }
		    throw e;
		}
		return result;
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
    	throws IOException{
	
		if (DirectObject instanceof RemoteStub){
			out.writeObject(DirectObject);
		}else{
			out.writeObject(RemoteObject.toStub((Remote) DirectObject));
		}
		out.writeObject(proxylocator);
	}
	
	private void readObject(java.io.ObjectInputStream in)
    	throws IOException, ClassNotFoundException{
		
		DirectObject = in.readObject();
		proxylocator = (RComponentProxyLocator) in.readObject();
		
		RPPStub = new RComponentProxyStubImpl(proxylocator);
	}
}
