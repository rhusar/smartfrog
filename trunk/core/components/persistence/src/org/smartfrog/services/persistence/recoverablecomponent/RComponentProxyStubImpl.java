
/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.recoverablecomponent;

import org.smartfrog.services.persistence.storage.*;

/*
 * This class implements all the methods that are called locally at the stub side
 * These calls (such as "equal") shall not be forwarded to the RPrim object
 */
public class RComponentProxyStubImpl implements RComponentProxyStub {

	protected RComponentProxyLocator ab;
	
	public RComponentProxyStubImpl(RComponentProxyLocator ref){
		ab = ref;
	}
	
	//TODO: in the future all these methods should be defined final in RPrim
	//and have an empy implementation in RPrimImpl.
	public RComponentProxyStub getProxyStub(){
		return this;
	}
	
	public boolean isDead(){
		try{
			return ab.isDead();
		}catch(ProxyLocatorException exc){}
		return false;
	}
	
	public boolean equals (Object obj){

		//remember obj is supposed to be a Proxy object
		//therefore, it implements RPrimProxyStub
		if(!(obj instanceof RComponentProxyStub))
			return false;
		
		//now we should get the RPrimProxyStub object
		//out of the proxy object 
		//remember this call goes through the InvocationHandler
		RComponentProxyStub RPPStub = ((RComponentProxyStub) obj).getProxyStub();

		if(!(RPPStub instanceof RComponentProxyStubImpl))
			return false;

		RComponentProxyStubImpl RPPSI = (RComponentProxyStubImpl) RPPStub;
		
		return ab.equals(RPPSI.ab);
	}
	
}
