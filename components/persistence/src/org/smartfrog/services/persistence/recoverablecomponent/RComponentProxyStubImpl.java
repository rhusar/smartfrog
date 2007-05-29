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


/*
 * This class implements all the methods that are called locally at the stub side
 * These calls (such as "equal") shall not be forwarded to the RPrim object
 */
public class RComponentProxyStubImpl implements RComponentProxyStub {

    protected RComponentProxyLocator locator;


    /**
     * Constructor
     * @param locator RComponentProxyLocator
     */
    public RComponentProxyStubImpl(RComponentProxyLocator locator) {
        this.locator = locator;
    }


    /**
     * Return this - why??!!
     * @return RComponentProxyStub
     */
    public RComponentProxyStub getProxyStub() {
        return this;
    }


    /**
     * The object is dead if the locator says so - remember an object that
     * is not dead may still be inaccessible due to failure.
     *
     * @return true if the target object has terminated
     */
    public boolean isDead() {
        try {
            return locator.isDead();
        } catch (ProxyLocatorException exc) {}
        return false;
    }


    public boolean equals(Object obj) {

        /**
         * remember obj is supposed to be a Proxy object
         * therefore, it implements RComponentProxyStub
         */
        if (!(obj instanceof RComponentProxyStub)) {
            return false;
        }

        /**
         * now we should get the stub object
         * out of the proxy object.
         * remember this call goes through the InvocationHandler so it
         */
        RComponentProxyStub RPPStub = ((RComponentProxyStub) obj).getProxyStub();

        if (!(RPPStub instanceof RComponentProxyStubImpl)) {
            return false;
        }

        RComponentProxyStubImpl RPPSI = (RComponentProxyStubImpl) RPPStub;

        return locator.equals(RPPSI.locator);
    }

}
