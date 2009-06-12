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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Iterator;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;

/**
 * This is the internal interface which is invoked in the smartfrog console to handle 
 * debugging operarions
 * The basic oprations provided are
 * Print Attributes
 * Add a new Attribute
 * Replace the value of an exixting Attribute
 * Remove any non mandatory attribute
 */

public class simpleShell {

   
    private static Prim p; 
      
    public simpleShell(Prim pr) {
        super();
        p = pr;
        
    }
    
/**
 * This method gets the command from the user, parses it and calls methods as accordingly 
 * otherwise display an appropriate message 
 *
 */ 
public void processAttributes(){
        
        boolean flag = true;
        String mStr = null;
        String fStr = null;
        String lStr = null;
        
	System.out.println("Try help for command summary");
        
        while(flag){
        System.out.print("debugger>");
         try {
            mStr = ParseCommand();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
	
	
	int pos = mStr.indexOf(' ');
	    
        try{
        
	fStr = mStr.substring(0,pos).trim();
        
        lStr = mStr.substring(pos).trim();
        
        }catch(StringIndexOutOfBoundsException e){
		fStr = mStr;
		lStr = null;
	}
      
        String atrName = null;
        String valName = null;
	    int p = 0;
	    
	    if(lStr != null){
	    p =  lStr.indexOf(' ');
	    try{
		    if (p > 0) {
	    		atrName = lStr.substring(0, p).trim();
	   		 valName = lStr.substring(p).trim();
		    } else
	    		atrName = lStr;
			    
		
     	    }catch(StringIndexOutOfBoundsException e){
	        
	    }
        }     

        if(fStr.equals("print")){
            printAttribute();
        }else if(fStr.equals("replace")){
		if(atrName != null && valName != null){
            replaceAttribute(atrName.trim(),valName.trim());
		}else{
			System.out.println("arg missing");
		}
        }else if(fStr.equals("add")){
		if(atrName != null && valName != null){
            addAttribute(atrName.trim(),valName.trim());
		}else{
			System.out.println("arg missing");
		}
        }else if(fStr.equals("remove")){
		if(atrName != null)
            		removeAttribute(atrName.trim());
        }else if(fStr.equals("continue")){
            flag = false;
        }else if(fStr.equals("help")){
		help();
	}else{
            System.out.println("Bad command: try help");
        }
        
    }
    
 }
/**
 * This method gives the command summary
 *
 */
    public void help(){
        System.out.println("Commands summary:");
        System.out.println("print            - to print the attributes");
        System.out.println("add 'atr val'    - to add a new attribute");
        System.out.println("replace 'atr val'- to replace an existing attribute with new value");
        System.out.println("remove 'atr'     - to remove the attribute");
        System.out.println("continue             - to resume ");
        System.out.println("help             - to display command summary");
    }

/**
* 
* @return parsed String from the command line
* @throws IOException 
*/
public static String ParseCommand() throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str = null;
		str = br.readLine();
		return str;
	}

/**
 * This method prints the Atrributes valid for the current prim
 *
 */
public void printAttribute(){
        Iterator iv = null,ia = null;
        try {
            ia = p.sfAttributes();
            iv = p.sfValues();
	System.out.println("The attributes of the current prim are:" + p.sfCompleteName().toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        while(ia.hasNext()){
            
            System.out.println(ia.next().toString() + ":" + iv.next().toString());
        }
        
    }
/**
 * This method adds a new attribute to the current prim
 * @param atr - name of the attribute
 * @param val - value to be given to the attribute
 */
    public void addAttribute(Object atr, Object val){
        try {
            p.sfAddAttribute(atr, val);
        } catch (SmartFrogRuntimeException e) {
                        e.printStackTrace();
        } catch (RemoteException e) {
            
            e.printStackTrace();
        }
    }
   
/**
 * This method replace the existing value of the attribute with new value
 * @param atr - name of the existing attribute
 * @param val - value to be replaced
 */    
    public void replaceAttribute(Object atr,Object val){
       
	    Iterator it = null, iv = null;
	
	    try{
            it = p.sfAttributes();
            iv = p.sfValues();
	    }catch(RemoteException e){
		e.printStackTrace();
	    }
            Object obj = null;
            Object objv = null;
            
	   

	try {
	     	while(it.hasNext()){
                
		objv = iv.next();
                
		if(it.next().toString().equals(atr)){
                   
		   if(objv instanceof Integer){
			   
			int i = Integer.parseInt(val.toString());
                        obj = (Object) new Integer(i);
			p.sfReplaceAttribute(atr,obj);

                       }else {
				p.sfReplaceAttribute(atr, val);
		       }
                }
                
            }

                    } catch (SmartFrogRuntimeException e) {
            
            e.printStackTrace();
        } catch (RemoteException e) {
            
            e.printStackTrace();
        }
    }
/**
 * This method removes the attribute from current prim
 * 
 * @param name - attribute name to be removed
 */    
    public void removeAttribute(Object name){
        try {
            p.sfRemoveAttribute(name);
        } catch (SmartFrogRuntimeException e) {
                     e.printStackTrace();
        } catch (RemoteException e) {
            
            e.printStackTrace();
        }
    }//end removeAttribute
}//end class

