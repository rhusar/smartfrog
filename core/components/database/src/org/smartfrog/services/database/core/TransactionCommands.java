package org.smartfrog.services.database.core;


/**
 autocommit extends Boolean {
 description "should every command commit after execution";
 }
 commands extends OptionalVector {
 description "a string list of commands to execute";
 };
 */
public interface TransactionCommands extends JdbcOperation {

    /**
     * "should every command commit after execution"
     * {@value}
     */
    String ATTR_AUTOCOMMIT="autocommit";

    /**
     * a string list of commands to execute
     * {@value}
     */
    String ATTR_COMMANDS="commands";
}
