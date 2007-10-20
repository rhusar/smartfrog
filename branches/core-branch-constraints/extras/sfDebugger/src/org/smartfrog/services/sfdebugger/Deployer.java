/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

This library was developed along with Manjunatha H S and Vedavyas H Raichur 
from Sri JayChamrajendra College of Engineering, Mysore, India. 
The work was part of the final semester Project work.

*/

package org.smartfrog.services.sfdebugger;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Stack;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.reference.Reference;



/**
  * This is to add an attribute corresponding to the user set life cycle methos for a particular
  * component in the component description
  */

public class Deployer implements CDVisitor{
        
       /**The static component description to which the new attributes to be added*/
        private static ComponentDescription cd ;
    	
       /**The static HashMap to hold the setBreakpoints HashMap*/
	private static HashMap breakHash = new HashMap();
        

       /**
	 * Constructs the Deployer with the Component description tree
	 *
	 * @param tree Component description be deployed
 	 *
	 */ 
 
	public Deployer(ComponentDescription tree){
            		
		cd = tree;
        }


	/**
	  * Deploys a particular description on a given host
	  * calls a method to add new atrributes to the description
	  *
	  * @param hm HashMap which contains the set Breakpoints and corresponding set life cycle
          * methods
  	  * @param hostName the host where the application has to be run
	  *
	  */ 
		 
        public void DeployCompDesc(HashMap hm,String hostName) {   
        
       
            	breakHash = hm;
	
			System.out.println(breakHash.toString());
		try {
                
		// Init SF System
                org.smartfrog.SFSystem.initSystem();
                
		// Get an DEPLOYED component. In this case the sfDaemon in local host
                Compound cp = SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(hostName.trim()),3800);
                
		//check for the root first
		if(breakHash.containsKey("Root")){
            	     putHook(cd,breakHash.get("Root"));
            	 }
	
		//check for other nodes of the tree
                cd.visit(this,true);
                
		 
                // Deploy the component description
	
		Prim p = cp.sfCreateNewApp("App",cd, null);
                
                System.out.println(" We just deployed: "+ p.sfCompleteName());
                
       } catch (SFGeneralSecurityException ex) {
                ex.printStackTrace();
            } catch (SmartFrogException ex) {
                ex.printStackTrace();
            } catch (Exception ex){
                ex.printStackTrace();
            }
    }//end DeployCompDesc
        

      /**
        * adds a special atrribute corresponding to the set life cycle method
	*
	* @param temp component description node to which the attribute is to be added
	* @param metName method name which corresponds to the user set life cycle methods
	*
	*/
      public void putHook(ComponentDescription temp,Object metName){
            
	    try{
	     //If the set life cycle methos is deploy add new attribute
	     if(metName.toString().indexOf('d') != -1){
           	System.out.println("Setting Deploy Hooks for " + temp.sfCompleteName().toString()); 
                temp.sfReplaceAttribute("DeployHook","true");
             }
	     if(metName.toString().indexOf('s') != -1){
		//If it is Start
           	System.out.println("Setting Start Hooks for " + temp.sfCompleteName().toString()); 
                 temp.sfReplaceAttribute("StartHook","true");
             }
	     if(metName.toString().indexOf('t') != -1){
		//If it is Terminate
           	 System.out.println("Setting Terminate Hooks for  " + temp.sfCompleteName().toString()); 
                 temp.sfReplaceAttribute("TerminateHook","true");
             }
	     }catch(SmartFrogRuntimeException e){
                e.printStackTrace();
            }//end catch
            
        }// end putHook
        
                 
        /* (non-Javadoc)
         * @see org.smartfrog.sfcore.componentdescription.CDVisitor#actOn(org.smartfrog.sfcore.
         	componentdescription.ComponentDescription)
         */

	/**
	  * called once for every node visited by the visitor method
	  * action taken is addition of new attribute by calling putHook
	  *
	  * @param node one of the component in the component description
	  */

        public void actOn(ComponentDescription node, Stack stack) throws Exception {
        		
        	    //get the reference of the node 
		    Reference ref = node.sfCompleteName();
		
		    //check if it is in the set breakpoints hashMap, if yes add attribute
		    	int index = ref.toString().indexOf(' ');
			String name=ref.toString().substring(index +1);
                     //if(breakHash.containsKey(ref.toString()))	
                     if(breakHash.containsKey(name))	{
                       	      //putHook(node,breakHash.get(ref.toString()));
                       	      putHook(node,breakHash.get(name));
		     }
        }//end act on
	
 }//end class

