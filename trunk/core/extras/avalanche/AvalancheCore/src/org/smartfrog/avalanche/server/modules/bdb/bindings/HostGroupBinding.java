/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.modules.bdb.bindings;

import org.smartfrog.avalanche.core.hostGroup.HostGroupDocument;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

public class HostGroupBinding extends TupleBinding {

	public Object entryToObject(TupleInput data) {
		String text = data.readString();
		HostGroupDocument hgdoc = null; 
		try{
			hgdoc = HostGroupDocument.Factory.parse(text);
		}catch(Exception e){
			// wont happen
			e.printStackTrace();
		}
		return hgdoc;
	}

	public Object entryToObject(DatabaseEntry data) {
		byte[] dataBytes = data.getData();
		HostGroupDocument hgdoc = null; 
		
		if( null != dataBytes){
			try{
				hgdoc = HostGroupDocument.Factory.parse(new String(dataBytes));
			}catch(Exception e){
				// wont happen
				e.printStackTrace();
			}
		}
		return hgdoc;
	}

	public void objectToEntry(Object obj, TupleOutput data) {
		HostGroupDocument hgdoc = (HostGroupDocument)obj; 
		data.writeString(hgdoc.xmlText());
	}
	public void objectToEntry(Object obj, DatabaseEntry data) {
		HostGroupDocument hgdoc = (HostGroupDocument)obj; 
		data.setData(hgdoc.xmlText().getBytes());
	}
}
