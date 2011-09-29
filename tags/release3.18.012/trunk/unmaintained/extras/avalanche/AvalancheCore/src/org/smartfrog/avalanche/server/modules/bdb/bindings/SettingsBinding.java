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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.settings.xdefault.SettingsDocument;

public class SettingsBinding extends TupleBinding {
    private static Log log = LogFactory.getLog(SettingsBinding.class);

	public Object entryToObject(TupleInput data) {
		String text = data.readString();
		SettingsDocument sdoc = null; 
		
		try{
			sdoc = SettingsDocument.Factory.parse(text);
		}catch(Exception e){
			// wont happen
			log.error(e); 
		}
		return sdoc;
	}

	public Object entryToObject(DatabaseEntry data) {
		byte[] dataBytes = data.getData();
		SettingsDocument sdoc = null; 
		
		if( null != dataBytes ){
			try{
				sdoc = SettingsDocument.Factory.parse(new String(dataBytes));
			}catch(Exception e){
				// wont happen
				log.error(e); 
			}
		}
		return sdoc;
	}

	public void objectToEntry(Object obj, TupleOutput data) {
		SettingsDocument sdoc = (SettingsDocument)obj ;
		data.writeString(sdoc.xmlText()) ;
	}

	public void objectToEntry(Object obj, DatabaseEntry data) {
		SettingsDocument sdoc = (SettingsDocument)obj ;
		data.setData(sdoc.xmlText().getBytes()) ;
	}
}
