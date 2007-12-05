/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Nov 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.prereqs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.gt4.build.GT4Constants;
import org.smartfrog.avalanche.client.sf.exec.ant.AntUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CheckPrereqs extends CheckCommand implements PrereqConstants {
	private Hashtable tools = null; 
	private static Log log = LogFactory.getLog(CheckCommand.class);
	private String ver = new String();
	private String pathSep = System.getProperty("path.separator");
				
	/**
	 * 
	 */
	public CheckPrereqs() {
		super();
		tools = new Hashtable();
		tools.put("java", new Integer(JAVA));
		tools.put("ant", new Integer(ANT));
		tools.put("cc", new Integer(CC));
		tools.put("tar", new Integer(TAR));
		tools.put("sed", new Integer(SED));
		tools.put("make", new Integer(MAKE));
		tools.put("sudo", new Integer(SUDO));
		tools.put("zlib", new Integer(ZLIB));
	//	tools.put("postgres", new Integer(POSTGRES));
		tools.put("perl", new Integer(PERL));		
	}
	
	public void checkCmd(String cmd, String reqVersion, String vendor) 
				throws PrereqException, IOException, InterruptedException {
		checkCmd(null, cmd, reqVersion, vendor);		
	}
	
	public void checkCmd(String cmd, String reqVersion) 
		throws PrereqException, IOException, InterruptedException {
		checkCmd(null, cmd, reqVersion, null);
	}
	
	public void checkCmd(String cmd) 
		throws PrereqException, IOException, InterruptedException {
		checkCmd(null, cmd, null, null);
	}
	
	/**
	 * Checks if the command cmd is in path, if it does not exist in path, 
	 * checks it in dir/bin if dir is defined. If cmd is found, then checks
	 * if version of this cmd is >= reqVersion. 
	 * 
	 * @param dir
	 * @param cmd
	 * @param reqVersion
	 * @param vendor
	 * @return
	 * @throws IOException
	 */
	public void checkCmd(String dir, String cmd, String reqVersion, String vendor)
				throws PrereqException {	
		int limit;
		
		if (null == cmd) {
			log.error("Pls provide the command to be checked!");
			throw new PrereqException("Pls provide the command " + 
					" to be checked!");
		}
		
		String errStr = null;
		String cmdOut = null;
		
		int cmdCode = ((Integer)(tools.get(cmd))).intValue();
		boolean found  = true;
		int exitVal = 0;
		switch (cmdCode) {
			case JAVA:
				/*try {
					if ((exitVal = cmdInPath(cmd, javaVerOpt)) != 0) {
						found  = false;
					}
					String javaBin = System.getProperty("java.home");
					int idx = javaBin.lastIndexOf(File.separatorChar);
					String javaPath = "JAVA_HOME=" + javaBin.substring(0,idx);
					javaBin = javaBin.substring(0,idx) + File.separatorChar + "bin";
					System.out.println("Java Bin : " + javaBin);
					
					GT4Constants.setPath(javaBin);					
				} catch (InterruptedException ie) {
					log.error(ie);
					throw new PrereqException(ie);
				} catch (IOException ioe) {
					try {
						if (dir.length() != 0) {
							if ((exitVal = cmdInDir(dir, cmd, javaVerOpt)) != 0) {
								found = false;
							}
							else {
								found = true;
								String javaBin = dir + File.separatorChar + "bin";
								GT4Constants.setPath(javaBin);
							}
						}
					} catch (IOException e) {
						log.error(e);
						throw new PrereqException(e);
					} catch (InterruptedException iee) {
						log.error(iee);
						throw new PrereqException(iee);
					}
				}*/
				try {
					if (dir.length() != 0) {
						if ((exitVal = cmdInDir(dir, cmd, javaVerOpt)) != 0) {
							found = false;
						}
						else {
							found = true;
							String javaBin = dir + File.separatorChar + "bin";
							String javaHome = "JAVA_HOME="+dir;
							GT4Constants.setEnvp(javaHome);
							GT4Constants.setPath(javaBin);
						}
					}
					else {
						log.error("Java path is not specified");
						throw new PrereqException("Java path is not specified");
					}
				} catch (IOException e) {
					log.error(e);
					throw new PrereqException(e);
				} catch (InterruptedException iee) {
					log.error(iee);
					throw new PrereqException(iee);
				}
				
				if (!found) {
					log.error(cmd + " not found on the machine");
					throw new PrereqException(cmd + " not found on " + 
							"the machine");
				}				
				
				try {
					if (exitVal != 0) {
						if (null != cmdError) {
							errStr = cmdError.readLine();
						}
					}
				} catch (IOException ioe) {
					log.error(ioe);
					throw new PrereqException(ioe);
				}
				
				if ((null != errStr) && (!errStr.startsWith("java version"))) {
					log.error("Error in executing " + cmd + " " + javaVerOpt);
					log.error(errStr);
					throw new PrereqException(errStr);				
				}
				
				if (null == reqVersion) {
					return;
				}
				
				String javaVersion = System.getProperty("java.version");
								
				if (!checkVersion(reqVersion,javaVersion)) {
					log.error("Java version needs to be >= " + reqVersion);
					throw new PrereqException("Java version needs to be >= " + 
							reqVersion);
				}
				log.info("Java Version is proper");
				break;
				
			case ANT:
				/*try {
					if ((exitVal = cmdInPath(cmd, antVerOpt)) != 0) {
						found  = false;
					}
				} catch (InterruptedException ie) {
					log.error(ie);
					throw new PrereqException(ie);
				} catch (IOException ioe) {
					try {
						if (dir.length() != 0) {
							if ((exitVal = cmdInDir(dir, cmd, antVerOpt)) != 0) {
								found = false;
							}
							else {
								found = true;
								String antBin = dir + File.separatorChar + "bin";
								//path = path + pathSep +	antBin;
								GT4Constants.setPath(antBin);
							}
						}						
					} catch (IOException ex) {
						log.error(ex);
						throw new PrereqException(ex);
					} catch (InterruptedException iee) {
						log.error(iee);
						throw new PrereqException(iee);
					}
				}*/
				
				try {
					if (dir.length() != 0) {
						if ((exitVal = cmdInDir(dir, cmd, antVerOpt)) != 0) {
							found = false;
						}
						else {
							found = true;
							String antBin = dir + File.separatorChar + "bin";
							//path = path + pathSep +	antBin;
							GT4Constants.setPath(antBin);
						}
					}
					else {
						log.error("Ant path is not specified");
						throw new PrereqException("Ant path is not specified");
					}
				} catch (IOException ex) {
					log.error(ex);
					throw new PrereqException(ex);
				} catch (InterruptedException iee) {
					log.error(iee);
					throw new PrereqException(iee);
				}
				
				if (!found) {
					log.error(cmd + " not found on the machine");
					throw new PrereqException(cmd + " not found " + 
							"on the machine");					
				}

				try {
					if (exitVal != 0) {
						errStr = "Error in executing " + cmd + " " + antVerOpt;
						String line = null;
						while ((line = cmdError.readLine()) != null) {
							errStr = errStr + line;
						}
						log.error(errStr);
						throw new PrereqException(errStr);
					}
				} catch (IOException ioe) {
					log.error(ioe);
					throw new PrereqException(ioe);
				}
				
				if (null == reqVersion) {
					return;
				}
				
				String antVersion = AntUtils.getAntVersion();
				
				if (!checkVersion(reqVersion, antVersion)) {
					log.error("Ant version needs to be >= " + reqVersion);
					throw new PrereqException("Ant version needs to be >= " + 
							reqVersion);
				}			
				log.info("Ant Version is proper"); 
				break;
				
			case CC:
				try {
					if ((exitVal = cmdInPath(cmd, ccVerOpt)) != 0) {
						errStr = "Error in executing " + cmd + " " + ccVerOpt;
						String line = null;
						while ((line = cmdError.readLine()) != null) {
							errStr = errStr + line;
						}
						log.error(errStr);
						throw new PrereqException(errStr);
					}
					
					if (cmdOutput != null) {
						cmdOut = cmdOutput.readLine();
					}
				} catch (IOException ioe) {
					log.error(ioe);
					throw new PrereqException(ioe);
				} catch (InterruptedException ie) {
					log.error(ie);
					throw new PrereqException(ie);
				}
				
				if (null == cmdOut) {
					log.error("No output for the command " + cmd + " --version");
					throw new PrereqException("No output for the command " + 
							cmd + " --version");
				}
				
				String[] cStr = cmdOut.split("\\s");
				String ccVer;
				if (null != cStr[2]) {
					ccVer = cStr[2];									 
				}
				else {
					log.error("Error in reading output of " + cmd + " " + ccVerOpt);
					throw new PrereqException("Error in reading output of " + 
							cmd + " " + ccVerOpt);					
				}
				
				cmdOut = cmdOut.replaceAll("\\s", "").toUpperCase();
				if (cmdOut.indexOf("gcc".toUpperCase()) != -1) {			
					// This is a gcc compiler. then check if version is 
					// 3.2 Globus does not support version gcc 3.2
					if (ccVer == "3.2") {
						log.error("GCC version 3.2 is not supported");
						throw new PrereqException("GCC version 3.2 is not supported");
					}
				}
				
				log.info("cc version is proper");
				break;
				
			case TAR:
			case SED:
			case MAKE:
				try {
					if ((exitVal = cmdInPath(cmd, gnuVerOpt)) != 0) {
						errStr = "Error in executing " + cmd + " " + gnuVerOpt;
						String line = null;
						while ((line = cmdError.readLine()) != null) {
							errStr = errStr + line;
						}
						log.error(errStr);
						throw new PrereqException(errStr);
					}
					cmdOut = cmdOutput.readLine();
				} catch (IOException ioe) {
					log.error(ioe);
					throw new PrereqException(ioe);
				} catch (InterruptedException ie) {
					log.error(ie);
					throw new PrereqException(ie);
				}
				
				if (cmdOut.length() == 0) {
					log.error("No output for the command " + cmd + " --version");
					throw new PrereqException("No output for the command " +
							cmd + " --version");
				}			
				
				if (!gnuVendor(vendor, cmdOut)) {
					log.error("Needs " + vendor + " " + cmd);
					throw new PrereqException("Needs " + vendor + " " + cmd);
				}
				log.info(cmd + " is in path");
				break;
				
			case SUDO:
				try {
					if ((exitVal = cmdInPath(cmd, sudoVerOpt)) != 0) {
						log.error(cmd + " not found in path");
						throw new PrereqException(cmd + " not found in path");
					}
					
					if (exitVal != 0) {
						errStr = "Error in executing " + cmd + " " + sudoVerOpt;
						String line = null;
						while ((line = cmdError.readLine()) != null) {
							errStr = errStr + line;
						}
						log.error(errStr);
						throw new PrereqException(errStr);
					}
					
					if (null != cmdOutput) {
						cmdOut = cmdOutput.readLine();
					}
				} catch (IOException ioe) {
					log.error(ioe);
					throw new PrereqException(ioe);
				} catch (InterruptedException ie) {
					log.error(ie);
					throw new PrereqException(ie);
				}
				
				if (null == cmdOut) {
					log.error("No output for the command " + cmd + " " + sudoVerOpt);
					throw new PrereqException("No output for the command " + 
							cmd + " " + sudoVerOpt);
				}
				log.info(cmd + " is in path");
				break;
				
			case POSTGRES:
				cmd = "pg_ctl";
				/*try {
					if ((exitVal = cmdInPath(cmd, pgresVerOpt)) != 0) {
						found  = false;						
					}
				} catch (IOException ioe) {
					try {
						if (dir.length() != 0) {
							if ((exitVal = cmdInDir(dir, cmd, pgresVerOpt)) != 0) {
								found = false;
							}
							else {
								found = true;
								String pgresBin = dir + File.separatorChar + "bin";
								//path = path + pathSep +	pgresBin;
								GT4Constants.setPath(pgresBin);
							}
						}
					} catch (IOException e) {
						log.error(cmd + "not found");
						throw new PrereqException(e);
					} catch (InterruptedException iee) {
						log.error(iee);
						throw new PrereqException(iee);
					}
				} catch (InterruptedException ie) {
					log.error(ie);
					throw new PrereqException(ie);
				}*/
				
				try {
					if (dir.length() != 0) {
						if ((exitVal = cmdInDir(dir, cmd, pgresVerOpt)) != 0) {
							found = false;
						}
						else {
							found = true;
							String pgresBin = dir + File.separatorChar + "bin";
							//path = path + pathSep +	pgresBin;
							GT4Constants.setPath(pgresBin);
						}
					}
					else {
						log.error("Postgres path is not specified");
						throw new PrereqException("Postgres path is not specified");
					}
				} catch (IOException e) {
					log.error(cmd + "not found");
					throw new PrereqException(e);
				} catch (InterruptedException iee) {
					log.error(iee);
					throw new PrereqException(iee);
				}
				
				log.info("Postgres check complete");
				if (!found) {
					log.error("postgres not found on the machine");
					throw new PrereqException("postgres not found on the machine");
				}
					
				try {
					if (exitVal != 0) {
						errStr = "Error in executing " + cmd + " " + pgresVerOpt;
						String line = null;
						while ((line = cmdError.readLine()) != null) {
							errStr = errStr + line;
						}
						log.error(errStr);
						throw new PrereqException(errStr);
					}
				} catch (IOException ioe) {
					log.error(ioe);
					throw new PrereqException(ioe);
				}
				
				if (reqVersion == null) {
					return;
				}
				
				try {
					if (null != cmdOutput) {
						cmdOut = cmdOutput.readLine();
					}
				} catch (IOException ioe) {
					log.error(ioe);
					throw new PrereqException(ioe);
				}
				
				String[] arrStr = cmdOut.split("\\s");
				String pgresVer;
				if (arrStr.length == 3) {
					pgresVer = arrStr[2];									 
				}
				else {
					log.error("Error in reading output of " + cmd + " " + pgresVerOpt);
					throw new PrereqException("Error in reading output of " + 
							cmd + " " + pgresVerOpt);
				}
				
				if (!checkVersion(reqVersion, pgresVer)) {
					log.error("Postgres version needs to be >= " + reqVersion);
					throw new PrereqException("Postgres version needs to be >= " + 
							reqVersion);
				}			
				log.info("Postgres Version is proper"); 
				break;
				
			case PERL:
				/*try {
					if ((exitVal = cmdInPath(cmd, perlVerOpt)) != 0) {
						found  = false;						
					}
				} catch (IOException ioe) {
					try {
						if (dir.length() != 0) {
							if ((exitVal = cmdInDir(dir, cmd, perlVerOpt)) != 0) {
								found = false;
							}
							else {
								found = true;
								String perlBin = dir + File.separatorChar + "bin";
								//path = path + pathSep +	perlBin;
								GT4Constants.setPath(perlBin);
							}
						}
					} catch (IOException e) {
						log.error(e);
						throw new PrereqException(e);						
					} catch (InterruptedException iee) {
						log.error(iee);
						throw new PrereqException(iee);
					}					
				} catch (InterruptedException ie) {
					log.error(ie);
					throw new PrereqException(ie);
				}*/
				
				try {
					if (dir.length() != 0) {
						if ((exitVal = cmdInDir(dir, cmd, perlVerOpt)) != 0) {
							found = false;
						}
						else {
							found = true;
							String perlBin = dir + File.separatorChar + "bin";
							//path = path + pathSep +	perlBin;
							GT4Constants.setPath(perlBin);
						}
					}
					else {
						log.error("Perl path is not specified");
						throw new PrereqException("Perl path is not specified");
					}
				} catch (IOException e) {
					log.error(e);
					throw new PrereqException(e);						
				} catch (InterruptedException iee) {
					log.error(iee);
					throw new PrereqException(iee);
				}
				
				log.info("Perl check complete");
				if (!found) {
					log.error(cmd + " not found on the machine");
					throw new PrereqException(cmd + " not found on the machine");					
				}
								
				try {
					if (exitVal != 0) {
						errStr = "Error in executing " + cmd + " " + perlVerOpt;
						String line = null;
						while ((line = cmdError.readLine()) != null) {
							errStr = errStr + line;
						}
						log.error(errStr);
						throw new PrereqException(errStr);
					}
				} catch (IOException ioe) {
					log.error(ioe);
					throw new PrereqException(ioe);
				}
								
				if (reqVersion == null) {
					return;
				}
				
				try {
					if (null != cmdOutput) {
						cmdOut = cmdOutput.readLine();
						cmdOut = cmdOutput.readLine();
					}
				} catch (IOException ioe) {
					log.error(ioe);
					throw new PrereqException(ioe);
				}
				
				String[] pArrStr = cmdOut.split("\\s");
				String perlVer = pArrStr[3];
				perlVer = perlVer.trim();
				perlVer = perlVer.substring(1);
								
				if (null == perlVer) {
					log.error("Error in reading output of " + cmd + " " + perlVerOpt);
					throw new PrereqException("Error in reading output of " + 
							cmd + " " + perlVerOpt);
				}
				
				if (!checkVersion(reqVersion, perlVer)) {
					log.error("Perl version needs to be >= " + reqVersion);
					throw new PrereqException("Perl version needs to be >= " + 
							reqVersion);
				}		
				log.info("Perl Version is proper"); 
				break;
				
			default:			
				throw new PrereqException("Checking of " + cmd + 
						" is not yet included");							
		}				
		
		try {
			cmdError.close();
			cmdOutput.close();
		} catch (IOException ioe) {
			log.error(ioe);
			throw new PrereqException(ioe);
		}		
	}
	
	private ArrayList getArrList(String version[]) {
		ArrayList al = new ArrayList();
		int n;
		
		for (int i=0; i<version.length; i++) {
			String v[] = version[i].trim().split("_");				
			for (int j=0; j<v.length; j++) {
				n = Integer.parseInt(v[j].trim());						
				al.add(new Integer(n));
			}					
		}
		
		if (al.isEmpty()) {
			return null;
		}
		
		/*Iterator iter = al.iterator();
		while (iter.hasNext()) {
			int num = ((Integer)iter.next()).intValue();					
			log.info("Elements: " + num + "\n");
		}*/
		
		return al;
	}
	
	public boolean checkVersion(String reqVersion, String currVersion) {
		String ver[]=null;
		String reqVer[] = null;
		ArrayList verArrList, reqVerArrList;
		int limit;
		
		ver = (currVersion.trim()).split("\\.");
		reqVer = reqVersion.split("\\.");
		verArrList = getArrList(ver);
		reqVerArrList = getArrList(reqVer);
		
		if (verArrList.size() < reqVerArrList.size()) {
			limit = verArrList.size();
		}
		else {
			limit = reqVerArrList.size();
		}
			
		for (int i=0; i<limit; i++) {
			int verNum = ((Integer)verArrList.get(i)).intValue();
			int reqVerNum = ((Integer)reqVerArrList.get(i)).intValue();
			
			if (verNum < reqVerNum) {
				return false;
			}
		}		
		return true;
	}	
		
	public boolean gnuVendor(String vendor, String cmdOut) {
		cmdOut = cmdOut.replaceAll("\\s", "").toUpperCase();
		if (cmdOut.indexOf(vendor.toUpperCase()) == -1) {			
			return false;
		}
		return true;
	}
	
	public static void main(String args[]) {
		CheckPrereqs chk = new CheckPrereqs();
		
		try {
			chk.checkCmd("java", "1.4.2_03", null);
			chk.checkCmd("ant", "1.6.1", null);
			chk.checkCmd("tar", null, "GNU");
			chk.checkCmd("sed", null, "GNU");
			chk.checkCmd("cc", null);
			chk.checkCmd("make", null, "GNU");
			chk.checkCmd("sudo", null, null);
			
			//chk.checkCmd("postgres", "7.1");
			chk.checkCmd("perl", "5.8");
		} catch(IOException ioe) {
			log.error("Exception : " + ioe);
		} catch (PrereqException pe) {
			log.error(pe);
		} catch (InterruptedException ie) {
			log.error(ie);
		}
	}

}
