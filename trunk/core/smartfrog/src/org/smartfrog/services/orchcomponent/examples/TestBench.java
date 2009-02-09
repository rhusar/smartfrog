package org.smartfrog.services.orchcomponent.examples;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.services.orchcomponent.desiredstate.DesiredEvent;
import org.smartfrog.services.orchcomponent.desiredstate.DesiredEventHandler;
import org.smartfrog.services.orchcomponent.desiredstate.DesiredEventRecord;
import org.smartfrog.services.orchcomponent.desiredstate.DesiredEventRegistration;
import org.smartfrog.services.orchcomponent.desiredstate.DesiredEvent.DesiredEventPart;
import org.smartfrog.services.orchcomponent.model.OrchComponentModel;
import org.smartfrog.services.orchcomponent.model.OrchConstants;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

public class TestBench extends PrimImpl implements Prim, DesiredEventRegistration {
	
	private Vector<DesiredEventRecord> ders = new Vector<DesiredEventRecord>();
	private OrchComponentModel model;
	
	public TestBench() throws RemoteException{}
	
	public synchronized void sfStart() throws RemoteException, SmartFrogException {
		   //System.out.println("&&&&& IN TEST BENCH START &&&&&");  
		   super.sfStart();
		   
		   try { model = (OrchComponentModel) sfResolve(new Reference(ReferencePart.here(OrchConstants.ORCH_ROOT)));} 
		   catch (Exception sfre){/*Elaborate*/ /*System.out.println("OOPS"+sfre);*/ return;}
		   
		   //System.out.println("&&&&&  MODEL  &&&&"+model.sfContext());  
		   
	       new Thread(new TestBenchThread()).start();
	   }
	
	public void registerForInsertionEvents(DesiredEventRecord record){
		this.ders.add(record);
	}
	
	public void deRegisterForEvents(DesiredEventHandler handler){}

	private void matchAndHandleEvent(DesiredEvent de, ComponentDescription cd){
		for (DesiredEventRecord der : ders){
			Context matching = matchEvent(de, der.getDesiredEvent());
			if (matching!=null){
				ComponentDescription base = der.getCD();
				Enumeration en = matching.keys();
				while (en.hasMoreElements()) {
					Object mkey = en.nextElement();
					base.sfContext().put(mkey, matching.get(mkey));
				}
				//System.out.println("***Handling event..."+base);
				der.getHandler().handleDesiredEvent(base);
			}
		}
	}
	
	private Context matchEvent(DesiredEvent in, DesiredEvent ref){
		Context match= new ContextImpl();
		
		Vector<DesiredEventPart> inParts = in.getParts();
		Vector<DesiredEventPart> refParts = (Vector<DesiredEventPart>) ref.getParts().clone();
		
		//This is not quite the right logic for the final integration.
		//Here we assume that insertions are registered as being made *at* the point of interest,
		//although as the VM insertion instance exemplifies they can happen higher...
		
		for (DesiredEventPart inPart : inParts){
			DesiredEventPart refPart = null;
			try{refPart = refParts.remove(0);}catch(Exception e){}
			if (refPart==null) return match; //got it!
			String refKey = refPart.getKey();
			String inKey = inPart.getKey();
			
			//System.out.println("matchEvent: refKey, inKey:"+refKey+":"+inKey);
			
			if (refPart.isVariable()){
				if (refPart.isPrefix()){
					if (inKey.startsWith(refPart.getKey())){
						match.put(refKey,inKey); 						
					} else return null;  //no match...
				} else match.put(refKey,inKey); 
			} else if (!refKey.equals(inKey)) return null; //no match... 	
		}
		
		if (refParts.size()>0)  return null;  //no match against reference...
		
		//System.out.println("matchEvent: we have a match");
		
		return match;
	}
	
	private void updateModel(){
		model.runPrims();
		model.runNotifier();
	}
	
	public class TestBenchThread implements Runnable {
		public void run(){
			//Recall: the managed component is *only* interested in insertions...
			//Any injection event however leads to model.runIdle()...
			
			//Narrative 1
			
			//System.out.println("In TestBenchThread");
			
			//1 -- Insertion of VM record with status "off" 
			String insertionPt = "desired:vms";
			
			//The following would just happen in reality...
			ComponentDescription vmcd = new SFComponentDescriptionImpl();
			ComponentDescription fcd = new SFComponentDescriptionImpl();
			vmcd.sfContext().put("status", "off");
			vmcd.sfContext().put("vm_id", "vm_0");
			vmcd.sfContext().put("fc_id", "fc_0");
			vmcd.setParent(fcd);
			fcd.sfContext().put("vm_0", vmcd);
			ComponentDescription vmscd = null;
			try { vmscd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			fcd.setParent(vmscd);
			vmscd.sfContext().put("fc_0", fcd);
			insertionPt += ":fc_0:vm_0";  //see comment above in "matchEvent"...
					
			DesiredEvent de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, vmcd);
			
			//FC_0, VM_1
			insertionPt = "desired:vms:fc_0";
			
			//The following would just happen in reality...
		    fcd = null; 
			try { fcd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			vmcd = new SFComponentDescriptionImpl();
			vmcd.sfContext().put("status", "on");
			vmcd.sfContext().put("vm_id", "vm_1");
			vmcd.sfContext().put("fc_id", "fc_0");
			vmcd.setParent(fcd);
			fcd.sfContext().put("vm_1", vmcd);
			
			insertionPt += ":vm_1";  //see comment above in "matchEvent"...
					
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, vmcd);
			
			insertionPt = "desired:vms";
			
			//FC_1, VM_2
			vmcd = new SFComponentDescriptionImpl();
			fcd = new SFComponentDescriptionImpl();
			vmcd.sfContext().put("status", "on");
			vmcd.sfContext().put("vm_id", "vm_2");
			vmcd.sfContext().put("fc_id", "fc_1");
			vmcd.setParent(fcd);
			fcd.sfContext().put("vm_2", vmcd);
			vmscd = null;
			try { vmscd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			fcd.setParent(vmscd);
			vmscd.sfContext().put("fc_1", fcd);
			insertionPt += ":fc_1:vm_2";  //see comment above in "matchEvent"...
					
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, vmcd);
			
			updateModel();
			
			
			//check that there is a component deployed
			/*
			try {
			Prim orch = (Prim) model.sfContext().get("orchestration");
			Prim vms = (Prim) orch.sfContext().get("vms");
			Prim fc = (Prim) vms.sfContext().get("fc_0");
			Prim ovm = (Prim) fc.sfContext().get("vm_0");
			Prim vm = (Prim) ovm.sfContext().get("vm");
			String vmid = (String) vm.sfContext().get("vm_id");
			
			System.out.println("VM deployed with id:"+vmid);
			} catch(Exception e){System.out.println(e);return;}
			*/
			
			//2 -- Insertion of Conn record with vm_id and vol_id
			insertionPt = "desired:connections";
			
			//The following would just happen in reality...
			ComponentDescription conncd = new SFComponentDescriptionImpl();
			fcd = new SFComponentDescriptionImpl();
			conncd.sfContext().put("vol_id", "vol_0");
			conncd.sfContext().put("vm_id", "vm_0");
			conncd.sfContext().put("conn_id", "conn_0");
			conncd.sfContext().put("sm_id", "smfoo");
			conncd.sfContext().put("fc_id", "fc_0");
			conncd.setParent(fcd);
			fcd.sfContext().put("conn_0", conncd);
			ComponentDescription connscd = null;
			try { connscd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			fcd.setParent(connscd);
			connscd.sfContext().put("fc_0", fcd);
			insertionPt += ":fc_0:conn_0";  //see comment above in "matchEvent"...
					
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, conncd);
			
			insertionPt = "desired:connections:fc_0";
			
			conncd = new SFComponentDescriptionImpl();
			fcd = null; 
			try { fcd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			
			conncd.sfContext().put("vol_id", "vol_0");
			conncd.sfContext().put("vm_id", "vm_0");
			conncd.sfContext().put("conn_id", "conn_1");
			conncd.sfContext().put("sm_id", "smfoo");
			conncd.sfContext().put("fc_id", "fc_0");
			conncd.setParent(fcd);
			fcd.sfContext().put("conn_1", conncd);
			insertionPt += ":conn_1";  //see comment above in "matchEvent"...
					
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, conncd);
			
			//check
			/*
			try {
				Prim orch = (Prim) model.sfContext().get("orchestration");
				Prim conns = (Prim) orch.sfContext().get("connections");
				Prim fc = (Prim) conns.sfContext().get("fc_0");
				Prim oconn = (Prim) fc.sfContext().get("conn_0");
				Prim conn = (Prim) oconn.sfContext().get("conn");
				String connid = (String) conn.sfContext().get("conn_id");
				
				System.out.println("Connection deployed with id:"+connid);
				} catch(Exception e){System.out.println(e);return;}
			*/
			
			//3 -- The adaptive injection which is to be managed at within eventreg (ie the real version of this)
			//will cause an event to fire on volume record insertion into desired... Here, we simulate it...
			insertionPt = "desired:volumes";
			
			//The following would just happen in reality...
			ComponentDescription volcd = new SFComponentDescriptionImpl();
			volcd.sfContext().put("vol_id", "vol_0");
			
			ComponentDescription volscd = null;
			try { volscd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			volcd.setParent(volscd);
			volscd.sfContext().put("vol_0", volcd);
			insertionPt += ":vol_0";  //see comment above in "matchEvent"...
					
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, volcd);
			
			updateModel();
			
			//check
			/*try {
				Prim orch = (Prim) model.sfContext().get("orchestration");
				Prim vols = (Prim) orch.sfContext().get("volumes");
				Prim ovol = (Prim) vols.sfContext().get("vol_0");
				Prim vol = (Prim) ovol.sfContext().get("vol");
				String volid = (String) vol.sfContext().get("vol_id");
				
				System.out.println("Volume deployed with id:"+volid);
				} catch(Exception e){System.out.println(e);return;}
			*/
			
			insertionPt = "desired:connectionGrants";
			//The following would just happen in reality...
			conncd = new SFComponentDescriptionImpl();
			fcd = new SFComponentDescriptionImpl();
			conncd.sfContext().put("status", "grant");
			conncd.sfContext().put("vr_id", "vrid123");
			conncd.setParent(fcd);
			fcd.sfContext().put("conn_0", conncd);
			connscd = null;
			try { connscd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			fcd.setParent(connscd);
			connscd.sfContext().put("fc_0", fcd);
			
			insertionPt += ":fc_0:conn_0:status";  //see comment above in "matchEvent"...		
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, conncd);
			
			insertionPt += ":fc_0:conn_1:status";  //see comment above in "matchEvent"...		
			conncd = new SFComponentDescriptionImpl();
			conncd.sfContext().put("status", "grant");
			conncd.sfContext().put("vr_id", "vrid123");
			conncd.setParent(fcd);
			fcd.sfContext().put("conn_1", conncd);
			
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, conncd);
			
			updateModel();
			
			insertionPt = "desired:volumeRealizations";
			//The following would just happen in reality...
			ComponentDescription vrcd = new SFComponentDescriptionImpl();
			vrcd.sfContext().put("vol_id", "vol_0");
			ComponentDescription vrs = null;
			try { vrs = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			vrcd.setParent(vrs);
			vrs.sfContext().put("vrid123", vrcd);
			insertionPt += ":vrid123";
			
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, vrcd);
			updateModel();
			
			insertionPt = "desired:localDev";
			//The following would just happen in reality...
			vrcd = new SFComponentDescriptionImpl();
			vrcd.sfContext().put("vol_id", "vol_0");
			vrs = null;
			try { vrs = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			vrcd.setParent(vrs);
			vrs.sfContext().put("vrid123", vrcd);
			insertionPt += ":vrid123";
			
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, vrcd);
			
			insertionPt = "desired:vms:fc_0:vm_0";
			//The following would just happen in reality...
			ComponentDescription vmcdOn = null;
			try { vmcdOn = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){/*System.out.println("ERROR NOW"+e);*//**/}
			vmcdOn.sfContext().put("status", "on");
			updateModel();
			
			ComponentDescription vmstate = null;
			while (true){
				insertionPt = "orchestration:vms:fc_0:vm_0:vm:ostate";
				try { 
					vmstate = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); 
					if (vmstate.sfContext().get("status").equals("started")) break; //from while...
					else Thread.sleep(3000);
				
				}catch (Exception e){}
			}
			
			
			//Narrative 2
			
			/*insertionPt = "desired:vms:fc_0:vm_0";
			//The following would just happen in reality...
			vmcdOn = null;
			try { vmcdOn = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){}
			vmcdOn.sfContext().put("status", "off");
			updateModel();
			
			vmstate = null;
			while (true){
				insertionPt = "orchestration:vms:fc_0:vm_0:vm:ostate";
				try { 
					vmstate = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); 
					if (vmstate.sfContext().get("status").equals("stopped")) break; //from while...
					else Thread.sleep(3000);
				
				}catch (Exception e){}
			}
			
			insertionPt = "desired:vms:fc_0:vm_0";
			//The following would just happen in reality...
			vmcdOn = null;
			try { vmcdOn = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){}
			vmcdOn.sfContext().put("status", "on");
			updateModel();
			
			vmstate = null;
			while (true){
				insertionPt = "orchestration:vms:fc_0:vm_0:vm:ostate";
				try { 
					vmstate = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); 
					if (vmstate.sfContext().get("status").equals("started")) break; //from while...
					else Thread.sleep(3000);
				
				}catch (Exception e){}
			}*/
			
			//Narrative 4 (switched)
			/*insertionPt = "desired:connectionGrants:fc_0:conn_0";
			
			ComponentDescription cgrant = null;
			try { cgrant = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){}
			cgrant.sfContext().put("status", "failed");
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, cgrant);
			updateModel();
			
			ComponentDescription connstate = null;
			while (true){
				insertionPt = "orchestration:connections:fc_0:conn_0:conn:ostate";
				try { 
					connstate = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); 
					if (connstate.sfContext().get("status").equals("requested")) break; //from while...
					else Thread.sleep(3000);
				}catch (Exception e){}
			}
			
			insertionPt = "desired:connectionGrants:fc_0:conn_0";
			
			cgrant = null;
			try { cgrant = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){}
			cgrant.sfContext().put("status", "grant");
			de = new DesiredEvent(insertionPt);
			matchAndHandleEvent(de, cgrant);
			updateModel();
			
			vmstate = null;
			while (true){
				insertionPt = "orchestration:vms:fc_0:vm_0:vm:ostate";
				try { 
					vmstate = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); 
					if (vmstate.sfContext().get("status").equals("started")) break; //from while...
					else Thread.sleep(3000);
				
				}catch (Exception e){}
			}*/
			
			//Narrative 3
			/*insertionPt = "desired:connections:fc_0";
			
			fcd = null;
			try { fcd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){}
			fcd.sfContext().remove("conn_0");
			updateModel();*/
			
			//Narrative 5
			/*insertionPt = "observed:vmStatus:fc_0:vm_0";
			
			vmcd = null;
			try { vmcd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){}
			vmcd.sfContext().put("power", "failed");
			updateModel();
			
			try { Thread.sleep(10000); }catch (Exception e){}
			
			vmcd.sfContext().remove("power");
			updateModel();
			*/
			
			//Narrative 5b
			/*insertionPt = "desired:vms:fc_0";
			
			fcd = null;
			try { fcd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){}
			fcd.sfContext().remove("vm_0");
			updateModel();
			*/
			
			//Narrative 6
			/*insertionPt = "desired:volumes";
			
			fcd = null;
			try { fcd = (ComponentDescription) model.sfResolve(Reference.fromString(insertionPt)); }
			catch (Exception e){}
			fcd.sfContext().remove("vol_0");
			updateModel();*/
		}
	}
	
}
