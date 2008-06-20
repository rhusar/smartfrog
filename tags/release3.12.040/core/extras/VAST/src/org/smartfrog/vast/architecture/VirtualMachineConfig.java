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
    public static String ATTR_GUEST_OS_READY_INTERVAL = "GuestOSReadyInterval";

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

    private Integer GuestReadyInterval;

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

    public Integer getGuestReadyInterval() {
        return GuestReadyInterval;
    }

    public void setGuestReadyInterval(Integer guestReadyInterval) {
        GuestReadyInterval = guestReadyInterval;
    }
}
