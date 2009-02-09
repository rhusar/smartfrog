package org.smartfrog.services.orchcomponent.desiredstate;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public class DesiredEventRecord {
	private DesiredEvent eventInfo; 
	private ComponentDescription base; 
	private DesiredEventHandler handler;
	
	public DesiredEventRecord(DesiredEvent eventInfo, ComponentDescription base, DesiredEventHandler handler){
		this.eventInfo=eventInfo; this.base=base; this.handler=handler;
	}
	public DesiredEvent getDesiredEvent(){
		return this.eventInfo;
	}
	public ComponentDescription getCD(){
		return this.base;
	}
	public DesiredEventHandler getHandler(){
		return this.handler;
	}
}
