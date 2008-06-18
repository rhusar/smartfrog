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
package org.smartfrog.vast.architecture;

import java.util.ArrayList;

public class PhysicalMachineConfig {
    public static String ATTR_HOST_ADDRESS = "HostAddress";
    public static String ATTR_ACCESS_MODES = "AccessModes";
    public static String ATTR_TRANSFER_MODES = "TransferModes";
    public static String ATTR_MODE_TYPE = "Type";
    public static String ATTR_MODE_USERNAME = "Username";
    public static String ATTR_MODE_PASSWORD = "Password";
    public static String ATTR_MODE_IS_DEFAULT = "IsDefault";
    public static String ATTR_ARGUMENTS = "Arguments";
    public static String ATTR_ARG_NAME = "Name";
    public static String ATTR_ARG_VALUE = "Value";
    public static String ATTR_ARCHITECTURE = "Architecture";
    public static String ATTR_PLATFORM = "Platform";
    public static String ATTR_OS = "OS";

    // one of the following has to be specified
    private String HostAddress;

    // list of access modes
    private ArrayList<ConnectionMode> listAccessModes = new ArrayList<ConnectionMode>();

    // list of transfer modes
    private ArrayList<ConnectionMode> listTransferModes = new ArrayList<ConnectionMode>();

    // list of arguments
    private ArrayList<Argument> listArguments = new ArrayList<Argument>();

    // java home directory on the machine
    private String JavaHome;

    // avalanche home directory on the machine
    private String AvalancheHome;

    // x86 or IA64?
    private String Architecture;

    // platform (intel etc)
    private String Platform;

    // operating system (windows, linux)
    private String OS;

    // is the machine up and running?
    private boolean Running;

    public String getArchitecture() {
        return Architecture;
    }

    public void setArchitecture(String architecture) {
        Architecture = architecture;
    }

    public String getAvalancheHome() {
        return AvalancheHome;
    }

    public void setAvalancheHome(String avalancheHome) {
        AvalancheHome = avalancheHome;
    }

    public String getJavaHome() {
        return JavaHome;
    }

    public void setJavaHome(String javaHome) {
        JavaHome = javaHome;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }

    public ArrayList<ConnectionMode> getListAccessModes() {
        return listAccessModes;
    }

    public void setListAccessModes(ArrayList<ConnectionMode> listAccessModes) {
        this.listAccessModes = listAccessModes;
    }

    public ArrayList<ConnectionMode> getListTransferModes() {
        return listTransferModes;
    }

    public void setListTransferModes(ArrayList<ConnectionMode> listTransferModes) {
        this.listTransferModes = listTransferModes;
    }

    public ArrayList<Argument> getListArguments() {
        return listArguments;
    }

    public void setListArguments(ArrayList<Argument> listArguments) {
        this.listArguments = listArguments;
    }

    public boolean isRunning() {
        return Running;
    }

    public void setRunning(boolean running) {
        Running = running;
    }

    public String getHostAddress() {
        return HostAddress;
    }

    public void setHostAddress(String hostAddress) {
        HostAddress = hostAddress;
    }
}
