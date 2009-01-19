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

*/


package org.smartfrog.services.filesystem.replacevar;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.util.Vector;
import java.util.Enumeration;

import java.rmi.RemoteException;

/**
 *  Component to replace text attributes in a text file
 *
 */
public class SFReplaceFileVar extends PrimImpl implements Prim {

    // Attributes
    /**
     *  File name @value
     */
    protected String fileName = ".";
    /**
     *  File name for result
     */
    protected String newFileName = null;

    /**
     *  Should terminate default @value
     */
    protected boolean shouldTerminate = true;
    /**
     *  Should detach default @value
     */
    protected boolean shouldDetach = false;

    /**
     *  Data parser
     */
    protected DataParser dataParser = null;
    /**
     *  Variable parser used for replacing
     */
    protected ParserVar parserVar = null;

    // Indicates if the process should terminate when the spanned process finishes
    static final String varShouldTerminate = "shouldTerminate";
    static final String varShouldDetach = "shouldDetach";
    // processWorkingDirectory
    static final String varSFfileName = "fileName";
    static final String varSFnewFileName = "newFileName";
    static final String varVariables = "var";
    //prefix to any variable to be included in dataParse
    static final String appendVariables = "append";
    //prefix to any variable to be included in dataAppend


    /**
     *  Constructor for the SFRunCommand object
     *
     *@exception  RemoteException  Description of Exception
     */
    public SFReplaceFileVar() throws RemoteException {
        super();
    }

    /**
     *  Sets the shouldTerminate attribute of the SFRunCommand object
     *
     *@param  shouldTerminate  The new shouldTerminate value
     */
    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
        if (dataParser != null) {
            dataParser.setShouldTerminate(this.shouldTerminate);
        }
    }


    /**
     *  Sets the shouldDetach attribute of the SFRunCommand object
     *
     *@param  shouldDetach  The new shouldDetach value
     */
    public void setShouldDetach(boolean shouldDetach) {
        this.shouldDetach = shouldDetach;
        if (dataParser != null) {
            dataParser.setShouldDetach(this.shouldDetach);
        }
    }


    /**
     *  Deploy Method
     *
     *@exception  Exception  Description of Exception
     */
    public void sfDeploy() throws RemoteException, SmartFrogException {
        if (sfLog().isInfoEnabled()) sfLog().info("Deploying...");
        super.sfDeploy();
        readSFAttributes();
        if (sfLog().isInfoEnabled()) sfLog().info("SFdeployed");

    }


    /**
     *  Start Method
     *
     *@exception  Exception  Description of Exception
     */
    public void sfStart() throws RemoteException, SmartFrogException {
        //@ToDo: it should check if the file exist an produce an appropiate message
        if (sfLog().isInfoEnabled()) sfLog().info("Starting...");
        super.sfStart();

        //Appending lines to file

        if ((dataParser.isValid()) && ((dataParser.getDataReplace() != null) || (dataParser.getDataAppend() != null))) {
            parserVar = new ParserVar(this, dataParser);
        }
        if (parserVar != null) {
            parserVar.start();
        } else {
            if (sfLog().isWarnEnabled()) sfLog().warn("sfStart: No data to parse.");
        }
        if (sfLog().isInfoEnabled()) sfLog().info("SFstarted");
    }


    /**
     *  Terminate Method
     *
     *@param  r  Description of Parameter
     */
    public void sfTerminateWith(TerminationRecord r) {
        if (sfLog().isInfoEnabled()) sfLog().info("SFterminatedWith " + r.toString());
        this.kill();
        super.sfTerminateWith(r);
    }


    /**
     *  To kill any remaining threads
     */
    public void kill() {
        if (parserVar != null) {
            try {
               // parserVar.destroy(); //Not necessary any more.
            } catch (Exception ex) {
//           ex.printStackTrace();
            }
        }
    }

    /**
     *  Read configuration attributes
     */
    private void readSFAttributes() {
        try {

            try {
                fileName = (String) sfResolve(varSFfileName);
            } catch (SmartFrogResolutionException e) {
                this.dataParser.setValid(false);
                if (sfLog().isErrorEnabled()) sfLog().error(varSFfileName + " not found.",e);
            }

            if (fileName != null) {
                //data for parsing
                this.dataParser = new DataParser(fileName, readVarData(appendVariables), readVarData(varVariables));
            }

            try {
                newFileName = (String) sfResolve(varSFnewFileName);
                if ((newFileName != null) && (!(newFileName.equals("")))) {
                    if (dataParser != null) {
                        dataParser.setNewFileName(newFileName);
                    }
                }
            } catch (SmartFrogResolutionException e) {
                if (sfLog().isErrorEnabled()) sfLog().error(varSFnewFileName + " not found.",e);
            }

            try {
                Object shouldTerminateObj = sfResolve(varShouldTerminate);
                if (shouldTerminateObj instanceof Boolean) {
                    this.setShouldTerminate(((Boolean) shouldTerminateObj).booleanValue());
                }
            } catch (SmartFrogResolutionException e) {
                if (sfLog().isErrorEnabled()) sfLog().error(varShouldTerminate + " not found.",e);
            }
            try {
                Object shouldDetachObj = sfResolve(varShouldDetach);
                if (shouldDetachObj instanceof Boolean) {
                    this.setShouldDetach(((Boolean) shouldDetachObj).booleanValue());
                }
            } catch (SmartFrogResolutionException e) {
                if (sfLog().isErrorEnabled()) sfLog().error(varShouldDetach + " not found.",e);
            }

        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) sfLog().error("Error reading SF attributes: " + e.getMessage(),e);
        }
    }


    /**
     *  Read all Replace Var Attributes in Vectors
     *
     *@param  typeAttrib  Description of the Parameter
     *@return             Description of the Return Value
     */
    private Vector readVarData(String typeAttrib) {
        Object key = null;
        Object value = null;
        Vector auxVec = null;
        Vector data = new Vector();
//      System.out.println("reading Attributes..." + typeAttrib);
        for (Enumeration e = sfContext().keys(); e.hasMoreElements(); ) {
            key = e.nextElement();
            if (key instanceof String) {
                try {
//            System.out.println("Key is "+ key);
                    if (((String) key).startsWith(typeAttrib)) {
                        value = (sfResolve((String) key));
//                System.out.println("Found Key Match  "+ value);
                        //if (value instanceof Vector) {
                        data.add(value);
                        //System.out.println("Put:"+((Vector)value).elementAt(0)+"->"+((Vector)value).elementAt(1));
                        //}
                    }
                } catch (Exception ex) {
                    if (sfLog().isErrorEnabled()){
                        sfLog().debug(ex);
                    }
                }
            }
        }

        if (data.isEmpty()) {
            data = null;
        }

        return data;
    }

}
//end

