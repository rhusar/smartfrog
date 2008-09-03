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

public class VirtualMachineConfig extends PhysicalMachineConfig {
    public static String ATTR_AFFINITY = "Affinity";
    public static String ATTR_NAME = "Name";
    public static String ATTR_SOURCE_IMAGE = "SourceImage";
    public static String ATTR_GUEST_USER = "GuestUser";
    public static String ATTR_GUEST_PASS = "GuestPass";
    public static String ATTR_DISPLAY_NAME = "DisplayName";
    public static String ATTR_TOOLS_TIMEOUT = "toolsTimeout";
	public static String ATTR_VAST_NETWORK_IP = "VastNetworkIP";
	public static String ATTR_VAST_NETWORK_MASK = "VastNetworkMask";
	public static String ATTR_HOST_NETWORK_MASK = "HostMask";
	public static String ATTR_SUT_PACKAGE = "SUTPackage";
	public static String ATTR_VAST_CONTROLLER = "VastController";

	// affinity to a physical machine
	private String Affinity;

    // name of the master image from which this
    // virtual machine should be created
    private String SourceImage;

    // user account for the guest os
    private String GuestUser;

    // password for the user account
    private String GuestPass;

    // the display name for the vm
    private String DisplayName;

	// timeout in seconds for the guest os to boot up
	private Integer ToolsTimeout;

	// network address for the vast
	// test controller
	private String VastNetworkIP;

	// network mask for the vast network
	private String VastNetworkMask;

	// nework mask for the SUT network
	private String HostMask;

	// name of the sut package
	private String SUTPackage;

	// is this the vast controller vm?
	private boolean VastController;

	// how many times has it been tried to set up the network using the helper?
	private int NetworkSetupHelperTries;

	public int getNetworkSetupHelperTries() {
		return NetworkSetupHelperTries;
	}

	public void setNetworkSetupHelperTries(int networkSetupHelperTries) {
		NetworkSetupHelperTries = networkSetupHelperTries;
	}

	public String getSUTPackage() {
		return SUTPackage;
	}

	public void setSUTPackage(String SUTPackage) {
		this.SUTPackage = SUTPackage;
	}

	public boolean isVastController() {
		return VastController;
	}

	public void setVastController(boolean vastController) {
		VastController = vastController;
	}

	public String getAffinity() {
        return Affinity;
    }

    public void setAffinity(String affinity) {
        Affinity = affinity;
    }

    public String getSourceImage() {
        return SourceImage;
    }

    public void setSourceImage(String sourceImage) {
        SourceImage = sourceImage;
    }

    public String getGuestPass() {
        return GuestPass;
    }

    public void setGuestPass(String guestPass) {
        GuestPass = guestPass;
    }

    public String getGuestUser() {
        return GuestUser;
    }

    public void setGuestUser(String guestUser) {
        GuestUser = guestUser;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public Integer getToolsTimeout() {
        return ToolsTimeout;
    }

    public void setToolsTimeout(Integer toolsTimeout) {
        ToolsTimeout = toolsTimeout;
    }

	public String getVastNetworkIP() {
		return VastNetworkIP;
	}

	public void setVastNetworkIP(String vastNetworkIP) {
		VastNetworkIP = vastNetworkIP;
	}

	public String getHostMask() {
		return HostMask;
	}

	public void setHostMask(String hostMask) {
		HostMask = hostMask;
	}

	public String getVastNetworkMask() {
		return VastNetworkMask;
	}

	public void setVastNetworkMask(String vastNetworkMask) {
		VastNetworkMask = vastNetworkMask;
	}
}
