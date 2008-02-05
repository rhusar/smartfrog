package org.smartfrog.services.filesystem;

import java.rmi.Remote;


public interface Chmod extends Remote {
    /**
     * The target file or directory which access permissions should be changed.
     */
    static final String ATTR_TARGET = "file";

    /**
     * The permissions for the owner.
     */
    static final String ATTR_USER_PERMISSIONS = "userPermissions";
    /**
     * The permissions for the group members.
     */
    static final String ATTR_GROUP_PERMISSIONS = "groupPermissions";
    /**
     * The permissions for the other users.
     */
    static final String ATTR_OTHER_PERMISSIONS = "otherPermissions";

    /**
     * Octal notation of the permissions.
     */
    static final String ATTR_OCTAL_CODE = "octalCode";

    /**
     * If true permissions will be set recursively.
     */
    static final String ATTR_RECURSIVELY = "recursively";
}
