/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP
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



package org.smartfrog.services.persistence.model;

/**
 * Commit points are places in the startup life cycle of a persistent component
 * that may be used to commit attribute changes. Updates made between commit
 * points are applied atomically at commit points and it is assumed that a
 * component can be recovered from its state at a commit point. This interface
 * defines a set of commit point names.
 */
public interface CommitPoints {
    /**
     * PRE_DEPLOY_WITH is at the start of an sfDeployWith(cxt) life cycle transition
     */
    public static String PRE_DEPLOY_WITH = "PRE_DEPLOY_WITH";

    /**
     * POST_DEPLOY_WITH is at the completion of an sfDeployWith(cxt) life cycle transition
     */
    public static String POST_DEPLOY_WITH = "POST_DEPLOY_WITH";

    /**
     * PRE_DEPLOY is at the start of an sfDeploy() life cycle transition
     */
    public static String PRE_DEPLOY = "PRE_DEPLOY";

    /**
     * POST_DEPLOY is at the completion of an sfDeploy() life cycle transition
     */
    public static String POST_DEPLOY = "POST_DEPLOY";

    /**
     * PRE_START is at the start of an sfStart() life cycle transition
     */
    public static String PRE_START = "PRE_START";

    /**
     * POST_START is at the completion of an sfStart() life cycle transition
     */
    public static String POST_START = "POST_START";

    /**
     * PRE_RECOVER is at the start of an sfRecover() life cycle transition
     */
    public static String PRE_RECOVER = "PRE_RECOVER";

    /**
     * POST_RECOVER is at the completion of an sfRecover() life cycle transition
     */
    public static String POST_RECOVER = "POST_RECOVER";

    /**
     * PRE_TERMINATE is at the start of an sfTerminate() life cycle transition
     */
    public static String PRE_TERMINATE = "PRE_TERMINATE";

    /**
     * POST_TERMINATE is at the completion of an sfTerminate() life cycle transition
     */
    public static String POST_TERMINATE = "POST_TERMINATE";
}
