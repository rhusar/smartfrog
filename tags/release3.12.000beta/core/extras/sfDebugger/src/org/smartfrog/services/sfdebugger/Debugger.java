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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.NoSuchElementException;

/**
 * 
 * This is the interface which declares the method execute to be used for all shell functions
 * 
 * 
 * 
 */
/*interface Command {
	
	public void execute();

}*/

/**
 * This is the basic debugging shell which provides a simple interface to the user to debug any
 * component description. It allows the user to set a breakpoint of his interest. These
 * breakpoints are the static set of breakpoints (which are internally the component nodes of the
 * component description tree). The user must also select a life cycle method at which the
 * debugger has to stop. The user can also run his application on any of the hosts from this shell
 */
  
public class Debugger implements Command{
	
	
	/** static Vector to hold the whole set of breakpoints*/
	static Vector BreakpointsVec = new Vector();
	
	/** static HashMap to hold the group of breakpoints and corresponding life cycle method
    which the user has set. Breakpoints are keys and life cycle method indexes are values*/
	static HashMap setBreakpoints = new HashMap();
	
	/** String to hold the life cycle methods. Contains d,s,t for deploy,start ant terminate 
    respectively*/
	static String lcStr = " "; 
	
	/** bp to get the breakpoints Vector*/
	Breakpoints bp = new Breakpoints();
	
	/** boolean to check the exit choice */
	boolean done = false;
	
	/** this is to display the commands summary of the shell*/
	CommandsHelp commandsHelp = new CommandsHelp();
	
	/** 
	  * Implements the execute method of interface Command.
	  * This is the main loop of the shell
	  */
	public void execute() {
		printWelcomeMessage();
		while(!done){
			printPromt();
			done = processCommand();
		}
	}
	/**
	  *Method which prints the Welcome message on the screen on startup of the shell
	  */ 
	
	private void printWelcomeMessage(){
		System.out.println("Welcome to the Smartfrog Debugger");
		System.out.println("type help for commands summary");
	}
	
	/**
	  *Method to print the prompt on each line of the shell
	  */
	
	private void printPromt(){
		System.out.print("SmartFrog Debugger>");
	}
	
	/**
	  * Main method which invokes the debugger
 	  */
	
	public static void main(String[] args) {
		Debugger shell = new Debugger();
		shell.execute();
		System.exit(0);
	}
	
	/**
	  * method which process the user given command and does the work accordingly
	  * command:debug - gets the breakpoints vector
	  * command:break - set the breakpoint at specific point with the life cycle method 
	  * command:run   - run the application on a particular host
	  * command:help  - display commands summary
	  *
	  * @return boolean value to stop/continue the debugging 
	  */
	private boolean processCommand(){
		  
	boolean quit = false;
          String Input;
          StringTokenizer st;
          String command;
          
          int id;
          
          Input = parseCommand();
	 
          st = new StringTokenizer(Input);
  	  int numTokens = st.countTokens();
  	  try{	  
	  command = st.nextToken();
	}catch(NoSuchElementException e){
		return quit;
	}
  		  
  		  if(command.equals("debug")){
			

			BreakpointsVec.clear();
			setBreakpoints.clear();

  		  	if(numTokens != 2){
				System.out.println("Usage: debug url  ");
				return quit;
			}
			//String url = st.nextToken();
			
			//System.out.println("The url is [" + url + "]");
			//FileReader fr = null;
			/*try{
			       fr = new FileReader(url);
                } catch (FileNotFoundException e1) {
                      System.out.println("File not found: ");
                }*/
            
            //if(fr != null){
			   // try{      
            	//BreakpointsVec = bp.getBreakpoints(url);
			
			    //ShowVector sv = new ShowVector(BreakpointsVec);
			      //     sv.execute();
			    //}catch(Exception e){
			    //	System.out.println("Exception: " + e);
			    //}
            //}
			    
			    String url = st.nextToken();
				
				//System.out.println(url);

			//	String newUrl = "/opt/Smartfrog/src/" + url.trim();
	/*	String newUrl = url.trim();
				
				FileReader fr = null;
					try{
				       		fr = new FileReader(newUrl);
	                		   } catch (FileNotFoundException e1) {
	                      			System.out.println("File not found: ");
	                		   }
	            
	            		 if(fr != null){
				          
				*/	
					
	            			BreakpointsVec = bp.getBreakpoints(url.trim());
					//System.out.println("Old values are" + BreakpointsVec.toString());
				    	ShowVector sv = new ShowVector(BreakpointsVec);
				           
						sv.execute();
	            		 //}
		  }else if(command.equals("break")){
			
		  	//String str = null;
		  	
		  	if(numTokens < 2 || numTokens > 5){
				System.out.println("Usage: break <ID>");
				return quit;
			}
			
			if((id = parseint(st)) == -1)
				return quit;
			
			
			if(id <= BreakpointsVec.size()){
				
			
				boolean illegal = false;
			    
				
				
			while(st.hasMoreTokens()){
				String temp = st.nextToken();
				
				if(temp.equals("-d")){
				
				    lcStr = lcStr + "d";
				}else if(temp.equals("-s")){
					
					lcStr = lcStr + "s";
				}else if(temp.equals("-t")){
			
					lcStr = lcStr + "t";
				}else{
					illegal = true;
				}
				
			}//end while
			
			if(illegal){
				
			System.out.println("Illegal option: [-d|-s|-t]");
				return quit;
			}
			
			String bStr = BreakpointsVec.get(id-1).toString();
			    int index = bStr.indexOf(' ');
			    
			    setBreakpoints.put(bStr.substring(index+1),lcStr.trim());
			    
	System.out.println("The Breakpoint is set at component:" + setBreakpoints.toString());
			    lcStr = " ";
			    
			   }else{
				System.out.println("The Index is out of Bound");
			   }
			
		}
		
		
		else if(command.equals("run")){
			String hostname;
			
			if(numTokens != 2){
				System.out.println("Usage: run [hostname]  ");
				return quit;
			}
			
			if(numTokens < 2)
				hostname = "localhost";
			else
				hostname = st.nextToken();
			//System.out.println("Application run on: " + hostname);
			
			Deployer dep = new Deployer(bp.getCompDesc());
			dep.DeployCompDesc(setBreakpoints,hostname);
			
			
		}
		else if(command.equals("help")){
		      
			commandsHelp.execute();
			if(numTokens > 1){
				System.out.println("Bad Command:");
				
			}
			
		    
		}
		else if(command.equals("exit")){
			  
			  System.out.println("Thanks for using the Debugger.");
			  System.out.println("Have a nice day! Bye");
			  quit = true;
		}
		else if(command.equals("list")){
			 ShowVector sv = new ShowVector(BreakpointsVec);
	           sv.execute();
		}
		else{
			System.out.println("Invalid command:" + command);
		}
  		  return quit;
  		  
	}
	
	/**
	  * this is to parse a string to get the integer value 
	  *
	  * @param st StringTokenizer for the string to be parsed
	  *
	  * @return integer value of next Token of st
	  *
	  */
	
	private static int parseint(StringTokenizer st){
		int retval = 0;
		try{
			retval = Integer.parseInt(st.nextToken());
		}catch(NumberFormatException ex){
			System.out.println("The argument must be an integer value");
			retval = -1;
		}
		return retval;
	}
	
	/** 
	  * method which gets the input commands from the user
	  *
	  * @return command string which the user has keyed in
	  *
	  */
	private String parseCommand(){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str = null;
		try{
		str = br.readLine();
		}catch(IOException e){
			System.out.println("Input not Found");
		}
		return str;
	}
	
}


