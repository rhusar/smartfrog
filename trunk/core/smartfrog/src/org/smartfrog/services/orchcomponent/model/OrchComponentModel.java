package org.smartfrog.services.orchcomponent.model;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.services.dependencies.statemodel.state.Composite;
import org.smartfrog.services.dependencies.statemodel.state.Model;
import org.smartfrog.services.orchcomponent.desiredstate.DesiredEvent;
import org.smartfrog.services.orchcomponent.desiredstate.DesiredEventRecord;
import org.smartfrog.services.orchcomponent.desiredstate.DesiredEventRegistration;
import org.smartfrog.services.orchcomponent.policy.IntegrityConstraintHandler;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

public class OrchComponentModel extends Model {

	   DesiredEventRegistration der;
	  
	   public OrchComponentModel() throws RemoteException {
	   }

	   private Vector<Prim> toRun= null;
	   
	   public void addToRun(Prim p){
		   System.out.println("AddPrims.");
		   if (toRun==null) toRun = new Vector<Prim>();
		   
		   try {
			   System.out.println("Adding:::"+((Composite)p).getName());
			   toRun.add(p);
		   } catch (Exception e){System.out.println("A:In error..."+e);}
	   }
	   
	   public void runPrims(){
		   if (toRun==null) return;
		   System.out.println("RunPrims."+toRun.size());
		   try {
			   for (Prim p : toRun){
				   if (p==null) System.out.println("WTF!!!");
				   System.out.println("Replacing *run* attribute in: "+((Composite)p).getName());
				   p.sfReplaceAttribute("run", true);
				   System.out.println("Out of that...");
			   }
			   toRun = null;
		   } catch (Exception e){System.out.println("B:In error..."+e);}
	   }
	   
	   public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
		  //System.out.println("&&&&& IN ORCHCOMPONENTMODEL DEPLOY &&&&&");
	      super.sfDeploy();
	      initModel();
	   }
	   
	   private void initModel(){	
		   
		   //System.out.println("+++Registering policies...");
 		   
		   //Register integrity constraints...
		   ComponentDescription constraints = null;
		   try { constraints = (ComponentDescription) sfResolveHere(OrchConstants.ORCH_CONSTRAINTS);} 
		   catch (Exception e){/*System.out.println("eeeeIC"+e);*//*Do nothing*/}
		   if (constraints==null) return;
		   
		   //Get der eventreg
		   try { der = (DesiredEventRegistration) sfResolve(new Reference(ReferencePart.attrib(OrchConstants.ORCH_EVENTREG)));} 
		   catch (Exception e){/*System.out.println("eeeeDER"+e);*//*Do nothing*/}
		   if (der==null) return;  /*Do something...*/
		   
		   if (constraints!=null){
			   Enumeration cons_enum= constraints.sfContext().keys();
			   while (cons_enum.hasMoreElements()){
				   
				   //get individual constraint
				   ComponentDescription constraint = null;
				   try {constraint = (ComponentDescription) constraints.sfContext().get(cons_enum.nextElement());}catch (Exception cce){}
				   if (constraint==null) continue; 
				   
				   //System.out.println("My name..."+constraint.sfParent().sfAttributeKeyFor(constraint));
				   
				   //get its context
				   String context=null;
				   try { context = (String) constraint.sfContext().get(OrchConstants.ORCH_CONTEXT);}catch (Exception cce){}
				   if (context==null) continue;
				   
				   Vector<Reference> policy_refs = new Vector<Reference>();
				   
				   //get its enforcement
				   Reference policy=null;
				   try { policy = (Reference) constraint.sfContext().get(OrchConstants.ORCH_POLICY);}catch (ClassCastException cce){}
				   if (policy!=null) {
					   //System.out.println("Adding a policy reference");
					   policy_refs.add(policy);
				   }
					
				   ComponentDescription policies=null;
				   try { policies = (ComponentDescription) constraint.sfContext().get(OrchConstants.ORCH_POLICIES);}catch (Exception cce){}
				   if (policies!=null){
					   //System.out.println("Policies not null");
					   Enumeration penum = policies.sfContext().keys();
					   while (penum.hasMoreElements()){
						   try { policy= (Reference) policies.sfContext().get(penum.nextElement());}
						   catch(ClassCastException cce){ /*System.out.println("Oops");*/ continue;}
						   //System.out.println("Adding a policy reference");
						   policy_refs.add(policy);
					   }
				   }
				   
				   //System.out.println("+++Registering some...");
				   DesiredEventRecord derec = new DesiredEventRecord(new DesiredEvent(context), constraint, new IntegrityConstraintHandler(policy_refs));
				   der.registerForInsertionEvents(derec);   
			   }
		   }
	   }
}
