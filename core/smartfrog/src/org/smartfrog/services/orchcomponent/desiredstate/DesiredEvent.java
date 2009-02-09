package org.smartfrog.services.orchcomponent.desiredstate;

import java.util.Vector;

public class DesiredEvent {
	
	private Vector<DesiredEventPart> parts = new Vector<DesiredEventPart>();
	
	public DesiredEvent(String context){
		initDesiredEvent(context);
	}
	
	private void initDesiredEvent(String context){
		while(true){
			
			//Get next part...
			String rem=null;
			int idx = context.indexOf(":");
			
			if (idx!=-1){
				rem = context.substring(idx+1);
				context = context.substring(0,idx);
			} 
			
			DesiredEventPart dep = new DesiredEventPart();
			parts.add(dep);
			
			//Variable?
			idx = context.indexOf("?");
			if (idx==0){
				context = context.substring(1);
				dep.variable=true;
			}
			
			//Prefix
			idx = context.indexOf("*");
			if (idx!=-1){
				context = context.substring(0, idx);
				dep.prefix=true;
			}
			
			//Key is remainder of context part...
			dep.key = context;
			
			//Any more?
			if (rem!=null) context=rem;
			else break; //from while...
		}
		
	}
	
	public Vector<DesiredEventPart> getParts(){ return this.parts; }
	
	public static class DesiredEventPart {
		private String key;
		private boolean prefix=false;
		private boolean variable=false;
		
		public String getKey(){return key;}
		public boolean isPrefix(){return prefix;}
		public boolean isVariable(){return variable;}
	}
}
