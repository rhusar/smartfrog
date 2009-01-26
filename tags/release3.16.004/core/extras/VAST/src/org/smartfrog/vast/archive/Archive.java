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

package org.smartfrog.vast.archive;

import java.io.IOException;

interface Archive {
	/**
	 * The size of the buffer for writing entries.
	 */
	public int BUFFER_SIZE = 2048;

	/**
	 * Creates an archive at the given location.
	 */
	public void create() throws IOException;

	/**
	 * Closes the archive.
	 */
	public void close() throws IOException;

	/**
	 * Puts a new entry into the archive.
	 * @param inPath Path to a file or folder.
	 * @param inRelPath Relative path to the file within the archive.
	 * @throws IOException
	 */
	public void putNextEntry(String inPath, String inRelPath) throws IOException;

	/**
	 * Extracts an archive.
	 * @param inDestination The destination folder.
	 * @throws IOException
	 */
	public void extract(String inDestination) throws IOException;
}
