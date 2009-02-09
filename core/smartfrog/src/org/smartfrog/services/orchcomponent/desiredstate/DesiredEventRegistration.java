package org.smartfrog.services.orchcomponent.desiredstate;

import java.util.Vector;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public interface DesiredEventRegistration {
	public void registerForInsertionEvents(DesiredEventRecord record);
	public void deRegisterForEvents(DesiredEventHandler handler);
}
