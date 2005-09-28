package org.smartfrog.services.persistence.recoverablecomponent;

import java.io.Serializable;

public interface RComponentProxyLocator extends Serializable {

	public RComponent getRComponentStub() throws ProxyLocatorException;
	
	public boolean isDead() throws ProxyLocatorException;
	
}
