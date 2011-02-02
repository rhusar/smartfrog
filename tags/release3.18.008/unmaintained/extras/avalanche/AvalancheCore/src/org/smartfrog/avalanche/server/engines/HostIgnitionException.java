/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jul 28, 2005
 *
 */
package org.smartfrog.avalanche.server.engines;

/**
 * @author sanjay, Jul 28, 2005
 *
 * TODO 
 */
public class HostIgnitionException extends Exception {

	/**
	 * 
	 */
	public HostIgnitionException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public HostIgnitionException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public HostIgnitionException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public HostIgnitionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
