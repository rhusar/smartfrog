/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.vast.architecture.archive;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class TarArchive extends BaseArchive {
	TarOutputStream out;

	public void create(String inPath) throws IOException {
		out = new TarOutputStream(new FileOutputStream(inPath));
	}

	public void putNextEntry(String inPath, String inRelPath) throws IOException {
		File file = new File(inPath);

		// create an entry in the tar file
		TarEntry entry = new TarEntry(inRelPath);
		entry.setSize(file.length());
		if (file.canExecute())
			entry.setMode(755);
		out.putNextEntry(entry);

		if (file.isFile()) {
			// copy the file's content
			FileInputStream reader = new FileInputStream(file);
			byte buffer[] = new byte[BUFFER_SIZE];
			int in;
			while ((in = reader.read(buffer, 0, BUFFER_SIZE)) > 0) {
				out.write(buffer, 0, in);
			}
			reader.close();
		}

		// close entry
		out.closeEntry();
	}

	public void close() throws IOException {
		out.close();
	}
}
