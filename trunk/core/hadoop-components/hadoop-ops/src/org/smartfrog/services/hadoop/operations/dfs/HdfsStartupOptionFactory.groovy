package org.smartfrog.services.hadoop.operations.dfs

import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption

/**
 *   static public enum StartupOption{*   FORMAT  ("-format"),
 REGULAR ("-regular"),
 UPGRADE ("-upgrade"),
 ROLLBACK("-rollback"),
 FINALIZE("-finalize"),
 IMPORT  ("-importCheckpoint");

 private String name = null;
 private StartupOption(String arg) {this.name = arg;}public String getName() {return name;}}*/
class HdfsStartupOptionFactory {

    /**
     * Create a startup option from a string value.
     * @param option the option as a command-line string
     * @return the action, or null for no match
     */
    public static StartupOption createStartupOption(String option) {
        switch (option) {
            case "-regular":
                return StartupOption.REGULAR;
            case "-format":
                return StartupOption.FORMAT;
            case "-rollback":
                return StartupOption.ROLLBACK;
            case "-finalize":
                return StartupOption.FINALIZE;
            case "-importCheckpoint":
                return StartupOption.IMPORT;
            default:
                return null;
        }
    }
}
