/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */


package org.smartfrog.services.persistence.rebind;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smartfrog.services.persistence.storage.StorageException;



/**
 * <p>
 * References to recoverable components can rebind themselves automatically
 * when the target component is recreated after some failure. These references
 * are implemented as dynamic proxies that use the RebindingStub
 * as their invocation handler.
 * </p>
 * <p>
 * The invocation handler uses three objects: the "direct" object; a "binder" 
 * object; and a "local intercepter" object; The direct object is the
 * target recoverable component and the invocation handler holds an RMI
 * reference to it. In the event that the RMI reference becomes invalid, say
 * due to a failure of the target, a new one is obtained from the binder object.
 * This step is how the RebindingStub rebinds to a new incarnation of the recoverable
 * component. The last object, the local intercepter, intercepts some methods that
 * can be performed locally instead of invoking the target recoverable component.
 * These include methods that are required to work even with the recoverable component
 * is absent - e.g. the method "isDead" which determines that we can not get to it.
 * </p>
 */
public class RebindingStub implements InvocationHandler, Serializable {

    static final long serialVersionUID = 0L;

    /**
     * DirectObject is a reference (stub) for the actual object
     */
    private Object directObject;

    /**
     * Binder does the rebindnig
     */
    private Binder binder;
    
    /**
     * localIntercept is an object that implements local methods - it does not
     * forward the method invocations to the actual object. These are
     * methods that may need to work in the absence of the actual object.
     */
    private Rebind localIntercept;
    
    /**
     * keeps track of stub status - if the stub is dead or 
     * deliberately closed all remote methods will be unavailable
     * to the user.
     */
    private volatile boolean closed;
    
    /**
     * fail fast timeout
     */
    private long failFastTimeout;
    
    /**
     * These methods will not invoke a rebind attempt if they fail
     */
    private Set<Method> failFastMethods;
    
    /**
     * These methods will not invoke a rebind attempt if they fail
     */
    private Set<Method> finalisingMethods;
    
    /**
     * Constructor - sets the direct object to be the obj parameter,
     * constructs a proxy locator and constructs an object that will handle
     * invocations that must be done locally (i.e. anything that may need
     * to be done in the absence of the actual object).
     *
     * @param obj
     * @throws RemoteException
     */
    public RebindingStub(Object obj) throws RemoteException {
        setDirectObject(obj);
        binder = ((Rebind)obj).getBinder();
        setFailFastTimeout( ((Rebind)obj).getFailFastTimeout() );
        failFastMethods = Collections.synchronizedSet( new HashSet<Method>() );
        finalisingMethods = Collections.synchronizedSet( new HashSet<Method>() );
        localIntercept = new RemoteRebindImpl(binder, this);
        closed = false;
        for( Method method : obj.getClass().getMethods() ) {
        	try {
        		if( method.isAnnotationPresent(FailFast.class) ) {
        			failFastMethods.add( getInterfaceMethod(method.getName(), method.getParameterTypes()) );
        		} 
        		if( method.isAnnotationPresent(Finalizing.class) ) {
        			finalisingMethods.add( getInterfaceMethod(method.getName(), method.getParameterTypes()) );
        		}
        	} catch(NoSuchMethodException e) {
        		e.printStackTrace();
        	}
        }
    }
    
    /**
     * mark the given method by adding it to the given set. 
     * 
     * If the method does not exist as a public method of the target object 
     * a NoSuchMethodException will be thrown. the method chosen will follow
     * the matching rules for Class.getMethod(String, Class<?>[]).
     *  
     * @param name the name of the method to mark
     * @param parameterTypes the parameters of the method to mark
     * @return the method
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public Method getInterfaceMethod(String name, Class<?>[] parameterTypes) throws SecurityException, NoSuchMethodException {
    	
    	/**
    	 * Look for the method in the remote interfaces and record it.
    	 */
    	Class<?>[] interfaces = getDirectObject().getClass().getInterfaces();
    	for(Class<?> interf : interfaces) {
    		try {
				return interf.getMethod(name, parameterTypes);
			} catch (NoSuchMethodException e) {
				continue;
			}
    	}
    	throw new NoSuchMethodException("Method " + name + " not found in interfaces");
    }
    
    
    

    /**
     * returns the direct object - used to avoid accidentally building a chain
     * of RebindingStubs when one is returned instead of a direct object stub.
     * Also used to ensure that access to directObject is thread safe. All access
     * goes through here - invocations on direct object should be performed
     * through a local variable. e.g.:
     * 
     * @return the real RMI reference
     */
    public synchronized Object getDirectObject() {
        return directObject;
    }
    
    /**
     * Setter for directObject - only used in this class.
     * 
     * @param obj the RMI refernce
     */
    private synchronized void setDirectObject(Object obj) {
    	directObject = obj;
    }
    
    /**
     * set fail fast timeout
     * @param timeout the timeout
     */
    public synchronized void setFailFastTimeout(long timeout) {
    	failFastTimeout = timeout;
    }
    
    /**
     * Get fail fast timeout
     * @return the timeout
     */
    public synchronized long getFailFastTimeout() {
    	return failFastTimeout;
    }
    
    /**
     * close the stub
     */
    public void close() {
    	closed = true;
    }

    /**
     * Obtains the proxy for the given component with an instance of this
     * class as the invocation handler for the proxy.
     * 
     * @param obj the component
     * @return the proxy object
     * @throws IllegalArgumentException
     * @throws RemoteException
     */
    static public Object getProxy(Object obj) throws IllegalArgumentException, RemoteException  {

        Class[] objinterfvector = obj.getClass().getInterfaces();
        Class[] interfvector = new Class[objinterfvector.length + 1];

        interfvector[0] = RemoteRebind.class;
        for (int i = 0; i < objinterfvector.length; i++) {
            interfvector[i + 1] = objinterfvector[i];
        }

        return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                interfvector,
                new RebindingStub(obj));
    }

    
    /**
     * Perform an invocation. The method attempts the invocation on the local
     * invocation object first to see if it can do it. This is a kind of
     * Intercepter for invocations that can be done here. If that doesn't
     * work it tries to invoke the actual object instead. If that doesn't
     * work due to remote invocation exception it goes into a loop trying
     * to rebind to the actual object and do the invocation again. If the
     * rebind returns that the object is dead then the loop terminates.
     *
     *
     * @param proxy Object
     * @param method Method
     * @param args Object[]
     * @return the result of the invocation
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
    	Object result = null;
    	Throwable exception = null;
    	
        /**
         * Attempt the invocation against the local over-ride methods
         */
        try {
            return method.invoke(localIntercept, args);
        } catch (InvocationTargetException exc) {
            throw exc.getTargetException();
        } catch (IllegalArgumentException exc2) {
            // ignore - it means the method is not intercepted
        }

        /**
         * If the stub is closed return right away
         */
        if( closed ) {
        	throw new RemoteException("This rebinding stub has closed");
        }
        
        /**
         * Attempt the invocation against the real remote object
         */
        try {
        	if( failFastMethods.contains(method) ) {
        		result = new TimedCall(proxy, method, args).invoke( getFailFastTimeout() );
        	} else {
        		result = invoke0(proxy, method, args);
        	}
        } catch( Throwable e ) {
        	exception = e;
        }
        
        /**
         * If finalizing close the stub
         */
        if( finalisingMethods.contains(method) ) {
        	System.out.println("Call is finalizing");
        	close();
        }
        
        /**
         * throw exceptions, return results.
         */
        if( exception == null ) {
        	return result;
        } else {
        	throw exception;
        } 
    }
    
    /**
     * TimedCall is a thread that performs an invocation against
     * the real remote object in a daemon thread. No return values 
     * or exceptions are passed back by this thread. The thread is a 
     * daemon, so the JVM can terminate without waiting for it
     * to complete. The invoke(long timeout) method starts the
     * daemon thread and times the invocation. If the invocation
     * takes too long or it returns a RemoteException the stub is
     * closed and a RemoteException is throw.
     */
    private class TimedCall extends Thread {
    	Object proxy;
    	Method method;
    	Object[] args;
    	volatile boolean used = false;
    	boolean done;
    	Object result;
    	Throwable exception;
    	public TimedCall(Object proxy, Method method, Object[] args) {
    		setDaemon(true);
    		this.proxy = proxy;
    		this.method = method;
    		this.args = args;
    	}
    	public void run() {
    		Object obj = null; 
    		Throwable thr = null;
    		try {
				obj = method.invoke(getDirectObject(), args);
			} catch (IllegalArgumentException e) {
				thr = e;
			} catch (IllegalAccessException e) {
				thr = e;
			} catch (InvocationTargetException e) {
				thr = e.getTargetException();
			}  
			synchronized(this) {
				done = true;
				result = obj;
				exception = thr;
				notify();
			}
    	}
    	
    	/**
    	 * This method will start the timed invocation and return its result
    	 * or throw its exception. If the invocation takes too long this method 
    	 * will timeout, close the stub and throw a RemoteException. If the 
    	 * invocation throws a RemoteException it will also close the stub.
    	 * 
    	 * @param timeout - the max time in milliseconds allowed for the invocation
    	 * @return the result of the invocation
    	 * @throws Throwable
    	 */
    	public synchronized Object invoke(long timeout) throws Throwable {
    		start();
    		try { wait(timeout); } 
    		catch (InterruptedException e) {  }
    		if( done && exception == null ) {
    			// completed successfully => return result (normal invocation behaviour)
    			return result;
    		} else if( done && exception instanceof RemoteException ) {
    			// completed with RemoteException => close stub and throw the exception
    			close();
    			throw exception;
    		} else if( done ) {
    			// completed with other exception => throw to user (normal invocation behaviour) 
    			throw exception;
    		} else {
    			// if not completed the call timed out => close stub and throw RemoteException
    			close();
    			throw new RemoteException("Invocation timed out");
    		}
    	}
    }
        
    
    /**
     * perform the invocation against the real remote object. 
     * If the invocation attempt fails with a RemoteException the method will
     * go into a loop, waiting, then trying to rebind the stub, then doing the invocation
     * until it works or the binder considers the stub to be dead. 
     *  
     * @param proxy the RMI reference
     * @param method the method 
     * @param args the arguments
     * @return Object returned by the remote method
     * @throws Throwable thrown by the remote method or RMI transport
     */
    private Object invoke0(Object proxy, Method method, Object[] args) throws Throwable {
    	
        /**
         * Attempt the invocation against the actual object
         * for the first time
         */
        try {

        	return method.invoke(getDirectObject(), args);
        
        } catch (InvocationTargetException exc) {
            
        	Throwable invocationException = exc.getTargetException();
        	
            /**
             * While the invocation is failing due to a RemoteException 
             * try to rebind and invoke again.
             */
            while (invocationException instanceof RemoteException) {

                /**
                 * Delay the next attempt
                 */
                binder.retryDelay();

                try {
                	
                	/**
                	 * if the stub has closed as we go give up
                	 */
                	if( closed ) {
                		throw new RemoteException("This rebinding stub has closed");
                	}
                	
                    /**
                     * Check we are not dead
                     */
                    if (binder.isDead()) {
                    	close();
                        throw new RemoteException("Lost access to interface or Component already terminated.");
                    }

                    /**
                     * rebind
                     */
                    setDirectObject( binder.getStub() );

                    
                    /**
                     * Try to set the session state
                     */
                    try {
                        ((Rebind)getDirectObject()).setSessionState(localIntercept.getSessionState());
                    } catch(RemoteException e) {
                        throw new InvocationTargetException(e);
                    }
                    
                    /**
                     * Clear the invocation and try the method again
                     */
                    invocationException = null;
                    return method.invoke(getDirectObject(), args);

                } catch (BindException e) {
                    /**
                     * problems getting the new stub - retry later
                     * notice that invocationException has not been 
                     * assigned null if this exception was thrown
                     */
                } catch (InvocationTargetException e) {
                    /**
                     * problems during the invocation
                     */
                    invocationException = e.getTargetException();
                } 

                /**
                 * in any case, if sfPing and not dead, returns OK
                 */
                if (method.getName().equals("sfPing")) {
                    return null;
                }
            }
            throw invocationException;
        } 
    }
    


    /**
     * Serialisation - always writes out the direct object
     * and the binder only.
     *
     * @param out ObjectOutputStream
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {

    	/**
    	 * object and binder
    	 */
		out.writeObject(RemoteObject.toStub((Remote) getDirectObject()));
		out.writeObject(binder);
		out.writeLong(getFailFastTimeout());
		
		/**
		 * fail fast methods
		 */
		out.writeInt( failFastMethods.size() );
		for( Method method : failFastMethods ) {
			out.writeObject( method.getName() );
			out.writeObject( method.getParameterTypes() );
		}
		
		/**
		 * finalizing methods
		 */
		out.writeInt( finalisingMethods.size() );
		for( Method method : finalisingMethods ) {
			out.writeObject( method.getName() );
			out.writeObject( method.getParameterTypes() );
		}
    }


    /**
     * Serialisation - reads the direct object and the binder
     * and recreates a local invocation object.
     * 
     * @param in ObjectInputStream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

    	/**
    	 * object and binder
    	 */
    	setDirectObject(in.readObject());
    	binder = (Binder) in.readObject();
    	setFailFastTimeout( in.readLong() );
    	
    	/**
    	 * fail fast methods
    	 */
    	failFastMethods = Collections.synchronizedSet( new HashSet<Method>() );
    	int numberOfFailFastMethods = in.readInt();
    	for( int i = 0; i < numberOfFailFastMethods; i++ ) {
    		String name = (String)in.readObject();
    		Class<?>[] parameterTypes = (Class<?>[])in.readObject();
    		try {
				failFastMethods.add( getInterfaceMethod(name, parameterTypes) );
			} catch (SecurityException e) {
				throw new RuntimeException("SecurityException thrown while unmarshalling fail fast method " + name , e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("NoSuchMethodException thrown while unmarshalling fail fast method " + name, e);
			}
    	}
    	
    	/**
    	 * finalizing methods
    	 */
    	finalisingMethods = Collections.synchronizedSet( new HashSet<Method>() );
    	int numberOfFinalizingMethods = in.readInt();
    	for( int i = 0; i < numberOfFinalizingMethods; i++ ) {
    		String name = (String)in.readObject();
    		Class<?>[] parameterTypes = (Class<?>[])in.readObject();
    		try {
    			finalisingMethods.add( getInterfaceMethod(name, parameterTypes) );
			} catch (SecurityException e) {
				throw new RuntimeException("SecurityException thrown while unmarshalling finalizing method " + name , e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("NoSuchMethodException thrown while unmarshalling finalizing method " + name, e);
			}
    	}
    	localIntercept = new RemoteRebindImpl(binder, this);
    	closed = false;
    }
}
