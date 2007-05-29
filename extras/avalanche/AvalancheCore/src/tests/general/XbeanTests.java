/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package tests.general;

import org.smartfrog.avalanche.core.module.ModuleDocument;
import org.smartfrog.avalanche.core.module.ModuleType;

public class XbeanTests {

	
	public static void main(String []args) throws Exception{
		ModuleDocument mdoc = ModuleDocument.Factory.newInstance();
		ModuleType module = mdoc.addNewModule(); 
		module.setId("TestModuleId");
		module.setDescription("Test module");
		module.setVendor("Test Vendor");
		
		String text = mdoc.xmlText();
		System.out.println(text);
		
		ModuleDocument mdoc1 = ModuleDocument.Factory.parse(text);
		ModuleType module1 = mdoc1.getModule();
		
		System.out.println(mdoc1.xmlText());
	}
}
