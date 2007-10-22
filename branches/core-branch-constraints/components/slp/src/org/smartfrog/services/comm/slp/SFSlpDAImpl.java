/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp;

import org.smartfrog.services.comm.slp.agents.DirectoryAgent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.Properties;

/** SmartFrog component for the Directory Agent. */
public class SFSlpDAImpl extends PrimImpl implements Prim, SFSlpDA {
    private DirectoryAgent da;
    private LogSF slpLog = null;

    public SFSlpDAImpl() throws RemoteException {

    }

    /** Creates an instance of the SLP DA. */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        // get properties
        Properties properties = new Properties();
        String s = (String) sfResolve("slp_config_interface");
        if (!s.equals("")) properties.setProperty("net.slp.interface", s);
        properties.setProperty("net.slp.useScopes", sfResolve("slp_config_scope_list").toString());
        properties.setProperty("net.slp.mtu", sfResolve("slp_config_mtu").toString());
        properties.setProperty("net.slp.port", sfResolve("slp_config_port").toString());
        properties.setProperty("net.slp.multicastAddress", sfResolve("slp_config_mc_addr").toString());
        properties.setProperty("net.slp.debug", sfResolve("slp_config_debug").toString());
        properties.setProperty("net.slp.logErrors", sfResolve("slp_config_log_errors").toString());
        properties.setProperty("net.slp.logMsg", sfResolve("slp_config_log_msg").toString());
        properties.setProperty("net.slp.logfile", sfResolve("slp_config_logfile").toString());
        properties.setProperty("net.slp.sflog", sfResolve("slp_config_sflog").toString());

        // create DA.
        da = new DirectoryAgent(properties);
        // logging.
        if (properties.getProperty("net.slp.sflog").equalsIgnoreCase("true")) {
            slpLog = sfGetLog(properties.getProperty("net.slp.logfile"));
            da.setSFLog(slpLog);
        }
    }

    /** Terminates the Directory Agent. */
    public void sfTerminateWith(TerminationRecord tr) {
        //System.out.println("Killing DA");
        da.killDA();
        da = null;
        super.sfTerminateWith(tr);
    }
}

