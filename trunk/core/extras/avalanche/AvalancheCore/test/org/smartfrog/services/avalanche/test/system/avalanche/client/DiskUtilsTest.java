/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.services.avalanche.test.system.avalanche.client;

import junit.framework.TestCase;

import java.io.File;

public class DiskUtilsTest extends TestCase {

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.disk.DiskUtils.fCopy(FileInputStream, File)'
	 */
	public void testFCopyFileInputStreamFile() {

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.disk.DiskUtils.fCopy(InputStream, File)'
	 */
	public void testFCopyInputStreamFile() {

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.disk.DiskUtils.fCopy(InputStream, OutputStream)'
	 */
	public void testFCopyInputStreamOutputStream() {

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.disk.DiskUtils.globalBufferCopy(InputStream, OutputStream)'
	 */
	public void testGlobalBufferCopy() {

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.disk.DiskUtils.copy(InputStream, OutputStream, byte[])'
	 */
	public void testCopy() {

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.disk.DiskUtils.readFile(File)'
	 */
	public void testReadFile() {

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.disk.DiskUtils.forceDelete(String)'
	 */
	public void testForceDeleteString() throws Exception{
		/*
		String path = "/Users/sanjaydahiya/dev/temp/test" ;
		DiskUtils.forceDelete(path);
		if( (new File(path)).exists()){
			fail();
		}
		*/
		String []filePaths = new String[]{"/usr", "/usr/", "/usr/sanjay", "/usr/bin/" ,"/usr/loca/jakarta-tomcat"} ;
		
		for( int i=0;i<filePaths.length;i++){
			String filePath = (new File(filePaths[i])).getAbsolutePath();
			
			if( filePath.equals("") || 
					filePath.lastIndexOf(File.separatorChar) == filePath.indexOf(File.separatorChar) ){
				System.out.println(filePath + " canDelete - false" );
			}else{
				System.out.println(filePath + " canDelete - true" );
			}
		}
	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.disk.DiskUtils.forceDelete(File)'
	 */
	public void testForceDeleteFile() {

	}

}
