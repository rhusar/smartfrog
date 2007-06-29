/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package tests.org.smartfrog.avalanche.client.sf.compress;

import junit.framework.TestCase;
import org.smartfrog.avalanche.client.sf.compress.ZipUtils;

import java.io.File;

public class ZipUtilsTest extends TestCase {

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.compress.ZipUtils.unTar(File, File)'
	 */
	public void testUnTar() throws Exception{
		File tarFile = new File("/tmp/abc.tar");
		File outputDir = new File("/Users/sanjaydahiya/dev/temp");
		
		ZipUtils.unTar(tarFile, outputDir) ;
	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.compress.ZipUtils.bunzip(File, File)'
	 */
	public void testBunzip() {

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.compress.ZipUtils.gunzip(File, File)'
	 */
	public void testGunzip() throws Exception {
		ZipUtils.gunzip(new File("/tmp/abc.tar.gz"), new File("/Users/sanjaydahiya/dev/temp/xyz.tar"));

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.client.sf.compress.ZipUtils.unzip(File, File)'
	 */
	public void testUnzip() {

	}

}
