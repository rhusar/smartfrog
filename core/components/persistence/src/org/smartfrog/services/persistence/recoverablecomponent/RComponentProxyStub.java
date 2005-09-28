package org.smartfrog.services.persistence.recoverablecomponent;

/*
 * This interface defines all methods that must be called locally at the stub side
 * It may be also used to identify whether an object is a Dynamic Proxy implementing
 * an RPrim Stub.
 */
public interface RComponentProxyStub {

	public RComponentProxyStub getProxyStub();
	
	public boolean isDead();
	
}
