/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Nov 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.prereqs;


/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PrereqConstants {
	public final int JAVA = 1;
	public final int ANT = 2;
	public final int CC = 3;
	public final int TAR = 4;
	public final int SED = 5;
	public final int MAKE = 6;
	public final int SUDO = 7;
	public final int ZLIB = 8;
	public final int POSTGRES = 9;
	
	public final String javaVerOpt = "-version";
	public final String antVerOpt = "-version";
	public final String ccVerOpt = ""; // ?? not the same for all
									   // distributions of cc
	public final String gnuVerOpt = "--version";
	public final String sudoVerOpt = "-V";
	public final String pgresVerOpt = "--version";
	
}
