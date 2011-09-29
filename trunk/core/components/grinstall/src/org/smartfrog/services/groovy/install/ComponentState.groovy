package org.smartfrog.services.groovy.install

/**
 * Software component states. The states are ordered:
 *  UNKNOWN        // Component has been created in model, but state not yet determined.
 *                 // A placeholder to allow a script to probe actual system to determine state.
 *  REMOVED        // Component has been completely removed from system, or has not yet started installation.
 *  INSTALLED      // Component has been installed, but not yet configured.
 *  PRECONFIGURED  // Component has been configured, but not yet running.
 *                 // This is the first stage of configuration - preconfiguration.
 *  STARTED        // Component is running, but not yet available for use -
 *                 // still needs to be postconfigured and subcomponents made ready.
 *  POSTCONFIGURED // Component has been postconfigured. This is the second stage of configuration.
 *  READY          // Component is ready for use.
 */
enum ComponentState {
    UNKNOWN, REMOVED, INSTALLED, PRECONFIGURED, STARTED, POSTCONFIGURED, READY
}
