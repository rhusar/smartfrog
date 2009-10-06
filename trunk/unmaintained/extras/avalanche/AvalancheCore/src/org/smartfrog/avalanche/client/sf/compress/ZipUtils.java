/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Sep 4, 2005
 *
 */
package org.smartfrog.avalanche.client.sf.compress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author sanjay, Sep 4, 2005
 * Suppors - Extracting from Gzip, Zip, Tar BZip2 compresssion methods.
 * Need to add similar compression methods. 
 * check - "org.smartfrog/avalanche/client/sf/compress/uncompress.sf" for related SF Descriptors
 * TODO
 */
public class ZipUtils {
	private static Log log = LogFactory.getLog(ZipUtils.class);

	private static int chunkSize = 8192;

	/**
	 *  
	 */
	public ZipUtils() {
		super();
	}
	
	public static boolean unTarProc(File tarFile, File outputDir) 
		throws IOException {
		String tarFileName = tarFile.getName();

		if (!tarFile.exists()) {
			log.error("The file " + tarFileName + " does not exist");
			return false;
		}
		if (!tarFile.isFile()) {
			log.error("The file " + tarFileName + " is not a file");
			return false;
		}
		if (!outputDir.exists()) {
			log.error("The directory " + outputDir + " does not exist");
			return false;
		}
		if (!outputDir.isDirectory()) {
			log.error("The given path " + outputDir + " is not a directory");
			return false;
		}
		if (!outputDir.canWrite()) {
			log.error("Cannot write to the directory " + outputDir);
			return false;
		}
		
		Runtime run = Runtime.getRuntime();
		String cmd = "tar xf " + tarFile.getAbsolutePath();
		Process p = run.exec(cmd, null, outputDir);
		int exitVal = 0;
		
		try {
			exitVal = p.waitFor();
		} catch (InterruptedException ie) {
			log.error("Error in un-tarring file " + tarFile.getAbsolutePath());
			return false;
		}
		if (exitVal != 0) {
			log.error("Error in un-tarring file " + tarFile.getAbsolutePath());
			return false;
		}
		else
			return true;		
	}
	
	public static boolean unTar(File tarFile, File outputDir)
			throws IOException {
		String tarFileName = new String(tarFile.getName());

		if (!tarFile.exists()) {
			log.error("The file " + tarFileName + " does not exist");
			return false;
		}
		if (!tarFile.isFile()) {
			log.error("The file " + tarFileName + " is not a file");
			return false;
		}
		if (!outputDir.exists()) {
			log.error("The directory " + outputDir + " does not exist");
			return false;
		}
		if (!outputDir.isDirectory()) {
			log.error("The given path " + outputDir + " is not a directory");
			return false;
		}
		if (!outputDir.canWrite()) {
			log.error("Cannot write to the directory " + outputDir);
			return false;
		}

        TarInputStream tarInstream=null;
        try {
            InputStream instream = getInputStream(tarFile);
            tarInstream = new TarInputStream(instream);

            TarEntry tarEntry = tarInstream.getNextEntry();
			while (tarEntry != null) {
				//create a file with the same name as the tarEntry
				File destPath = new File(outputDir,tarEntry.getName());
				log.info(tarEntry.getName());
                log.info("Mode : " + tarEntry.getMode());
                log.info("Size : " + tarEntry.getSize());
				
				if (tarEntry.isDirectory()) {
					destPath.mkdir();
				} else {
					String pathStr = destPath.getPath();
					int idx = pathStr.lastIndexOf(File.separatorChar);
					if (idx > 0) {
						File destDir = new File(pathStr.substring(0, idx));
						destDir.mkdirs();
					}
					FileOutputStream fileOutStream=null;
                    try {
                        fileOutStream = new FileOutputStream(
                                destPath);
                        tarInstream.copyEntryContents(fileOutStream);
                    } finally {
                        FileSystem.close(fileOutStream);
                    }

					// TODO: find a better method grant execute permissions
					System.out.println("Length of " + tarEntry.getName() + " : " + destPath.length());
					
					log.info("*********************");					
					if( destPath.getName().endsWith(".sh")){
						Runtime.getRuntime().exec("chmod +x " + destPath.getAbsolutePath());
					}
					if (destPath.getName().endsWith("configure")) {
						Runtime.getRuntime().exec("chmod +x " + destPath.getAbsolutePath());
					}
					if (destPath.getName().endsWith("config")) {
						Runtime.getRuntime().exec("chmod +x " + destPath.getAbsolutePath());
					}
					if (destPath.getName().endsWith("Configure")) {
						Runtime.getRuntime().exec("chmod +x " + destPath.getAbsolutePath());
					}					
				}
				tarEntry = tarInstream.getNextEntry();
			}
		} catch (FileNotFoundException fnf) {
			log.error("Exception while un-tarring the file " + tarFileName,fnf);
			throw new IOException("Error: File Not Found, " + fnf.getMessage());
		} finally {
            FileSystem.close(tarInstream);
        }
		return true;
	}

	private static InputStream getInputStream(File tarFile)
			throws FileNotFoundException, IOException {
		return (new FileInputStream(tarFile));
	}
	
	public static boolean bunzip(File bzipFile, File outputDir) throws IOException{

		CBZip2InputStream bzipInstream=null;
        BufferedOutputStream output=null;

        if (!bzipFile.exists()) {
			log.error("The file " + bzipFile.getAbsolutePath() + " does not exist");
			return false;
		}
		if (!bzipFile.isFile()) {
			log.error("The file " + bzipFile.getAbsolutePath() + " is not a file");
			return false;
		}
		if (!outputDir.exists()) {
			log.error("The directory " + outputDir + " does not exist");
			return false;
		}
		if (!outputDir.isDirectory()) {
			log.error("The given path " + outputDir + " is not a directory");
			return false;
		}
		if (!outputDir.canWrite()) {
			log.error("Cannot write to the directory " + outputDir
					+ ".Permission denied");
			return false;
		}
        String bzFileName;
        FileOutputStream out;
        try {
            bzFileName = bzipFile.getName();
            String file = bzipFile.getPath();
            FileInputStream in = new FileInputStream(file);
            BufferedInputStream src = new BufferedInputStream(in);
            bzipInstream = new CBZip2InputStream(src);

            byte[] buffer = new byte[chunkSize];
            int len = 0;

            String outputFileName = null;
            if (bzFileName.endsWith(".gz")) {
                outputFileName = bzFileName.substring(0, bzFileName.length() - 3);
            } else {
                outputFileName = bzFileName + ".tmp";
            }
            File outputFile = new File(outputDir, outputFileName);

            out = new FileOutputStream(outputFile);
            output = new BufferedOutputStream(out, chunkSize);

            /*
            * Read from gzip stream which will uncompress and write to output
                * stream
                */
            while ((len = bzipInstream.read(buffer, 0, chunkSize)) != -1) {
                output.write(buffer, 0, len);
            }

            output.flush();
        } finally {
            FileSystem.close(output);
            FileSystem.close(bzipInstream);
        }

		log.info("Uncompress completed for file - " + bzFileName);
		return true;
	}

	public static boolean gunzip(File gzipFile, File outputFile)
			throws IOException {
		GZIPInputStream gzipInstream;

		String gzFileName = gzipFile.getName();
		if (!gzipFile.exists()) {
			log.error("The file " + gzFileName + " does not exist");
			return false;
		}
		if (!gzipFile.isFile()) {
			log.error("The file " + gzFileName + " is not a file");
			return false;
		}
		if (outputFile.exists()) {
			// overwrite
		}
		if (outputFile.isDirectory()) {
			log.error("The given path " + outputFile + " is a directory");
			return false;
		}
		
		if (gzipFile.getAbsolutePath().equals(outputFile.getAbsolutePath())) {
			log.error("Source and destination paths for un-zipping the file are same.");
			return false;
		}

		String file = gzipFile.getPath();
		FileInputStream in = new FileInputStream(file);
		BufferedInputStream src = new BufferedInputStream(in);
		gzipInstream = new GZIPInputStream(src);

		byte[] buffer = new byte[chunkSize];
		int len = 0;
		
		FileOutputStream out = new FileOutputStream(outputFile);
		BufferedOutputStream output = new BufferedOutputStream(out, chunkSize);

		/*
		 * Read from gzip stream which will uncompress and write to output
		 * stream
		 */
		while ((len = gzipInstream.read(buffer, 0, chunkSize)) != -1) {
			output.write(buffer, 0, len);
		}

		output.flush();
		out.close();

		gzipInstream.close();

		log.info("Uncompress completed for file - " + gzFileName);
		return true;
	}

	/**
	 * Unzips a .zip file and places the contents in outputDir
	 * 
	 * @param zipFile
	 * @param outputDir
	 * @return 
	 * @throws IOException
	 */
	public static boolean unzip(File zipFile, File outputDir)
			throws IOException {
		ZipInputStream zipInstream;

		String zipFileName = zipFile.getName();
		if (!zipFile.exists()) {
			log.error("The file " + zipFileName + " does not exist");
			return false;
		}
		if (!zipFile.isFile()) {
			log.error("The file " + zipFileName + " is not a file");
			return false;
		}
		if (!outputDir.exists()) {
			log.error("The directory " + outputDir.getPath()
					+ " does not exist");
			return false;
		}
		if (!outputDir.isDirectory()) {
			log.error("The given path " + outputDir.getPath()
					+ " is not a directory");
			return false;
		}
		if (!outputDir.canWrite()) {
			log.error("Cannot write to the directory " + outputDir.getPath());
			return false;
		}

		FileInputStream in = new FileInputStream(zipFile);
		BufferedInputStream src = new BufferedInputStream(in);
		zipInstream = new ZipInputStream(src);

		byte[] buffer = new byte[chunkSize];
		int len = 0;

		// Loop through the entries in the ZIP archive and read
		// each compressed file.
		do {
			// Need to read the ZipEntry for each file in the archive
			ZipEntry zipEntry = zipInstream.getNextEntry();
			if (zipEntry == null)
				break;

			// Use the ZipEntry name as that of the compressed file.
			File outputFile = new File(outputDir, zipEntry.getName());

			FileOutputStream out = new FileOutputStream(outputFile);
			BufferedOutputStream destination = new BufferedOutputStream(out,
					chunkSize);

			while ((len = zipInstream.read(buffer, 0, chunkSize)) != -1)
				destination.write(buffer, 0, len);

			destination.flush();
			out.close();
		} while (true);

		zipInstream.close();

		log.info("Unzipped the file " + zipFileName);
		return true;
	}
}