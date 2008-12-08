/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.rpm.dirload;

import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Created 08-Dec-2008 16:47:21
 */

public class DirLoaderImpl extends FileUsingComponentImpl implements DirLoader {

  private HashMap<File,DeployedDir> directories = newMap();

  private HashMap<File, DeployedDir> newMap() {
    return new HashMap<File, DeployedDir>();
  }

  public DirLoaderImpl() throws RemoteException {
  }


  @Override
  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
  }

  @Override
  public void sfStart() throws SmartFrogException, RemoteException {
    super.sfStart();
    //bind to our filesystem
    bind(true,null);
  }

  private void scan() {
    HashMap<File, DeployedDir> dir2 = newMap();
    File[] files = getFile().listFiles(new DirFilter());
    //look through the list for arrivals and departures; queue arrivals for deployment.
    //alternatively: queue everything for ordered deployment and let the deployer decide.
  }


  public class DirFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
      return false;
    }
  }
}
