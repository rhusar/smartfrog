/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.modules.bdb.bindings;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;
import org.smartfrog.avalanche.core.module.ModuleDocument;

public class ModuleBinding extends TupleBinding {
	

	public Object entryToObject(TupleInput data) {
		ModuleDocument mdoc = null ;
		String text = data.readString();
		if( null != text ){
			try{
				 mdoc = ModuleDocument.Factory.parse(text);
			}catch(Exception e){
				// wont happen unless db goes bad.
				e.printStackTrace();
			}
		}
		return mdoc;
	}

	public Object entryToObject(DatabaseEntry data) {
		ModuleDocument mdoc = null ;
		byte[] dataBytes = data.getData();
		if( null != dataBytes ){
			System.out.println("Get Data : " + new String(dataBytes));
			try{
				 mdoc = ModuleDocument.Factory.parse(new String(dataBytes));
			}catch(Exception e){
				// wont happen unless db goes bad.
				e.printStackTrace();
			}
		}
		return mdoc;
	}
	
	public void objectToEntry(Object obj, TupleOutput data) {
		ModuleDocument mdoc = (ModuleDocument) obj ;
		data.writeString(mdoc.xmlText());
	}
	
	public void objectToEntry(Object obj, DatabaseEntry data) {
		ModuleDocument mdoc = (ModuleDocument) obj ;
		System.out.println(mdoc.xmlText());
		data.setData(mdoc.xmlText().getBytes());
	}
}
