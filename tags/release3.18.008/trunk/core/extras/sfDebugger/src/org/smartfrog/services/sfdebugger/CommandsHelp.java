/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

This library was developed along with Manjunatha H S and Vedavyas H Raichur 
from Sri JayChamrajendra College of Engineering, Mysore, India. 
The work was part of the final semester Project work.

*/

package org.smartfrog.services.sfdebugger;




public class CommandsHelp implements Command {
	  public void execute() {
	    System.out.println("Commands Summary:");
	    System.out.println("debug `filename.sf`           - to debug a Smartfrog Descripion file");
	    System.out.println("break `Breakpoint no:-[d|s|t]`- to set a Breakpoint for particular components life cycle method");
	    System.out.println("                                (-d) Deploy (-s) Start (-t) Terminate");
	    System.out.println("run  'hostname'                - to run the application");
	    System.out.println("exit                           - to quit ");
	   System.out.println("help                            - to show commands summary");
	    }
}
