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
package org.smartfrog.services.dependencies.statemodel.state;

import static org.smartfrog.services.dependencies.statemodel.state.Constants.*;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * created 30-Jun-2010 11:31:06
 */

public class QueryMetaData {
    public QueryMetaData(final LogSF sfLog){
        this.sfLog = sfLog;
    }

    public QueryMetaData(final Prim me, final LogSF sfLog) {
        this(sfLog);
        setPrim(me);
    }

    private Prim me;
    private LogSF sfLog;
    private Prim eventLog;
    private String fullName;
    private String fullNamePath;

    public void setPrim(final Prim me){
        this.me =me;
        fullName="";
        try {
            fullName = me.sfCompleteName().toString();
            int idx = fullName.indexOf("rootProcess:");
            if (idx != -1) fullName = fullName.substring("rootProcess:".length() + idx);
        } catch (RemoteException e) {
            sfLog.ignore(e);
        }
        fullNamePath = SERVICERESOURCE.substring(1).replaceAll(PATHDELIM, SFDELIM) + fullName;
    }

    public void setEventLog(final Prim eventLog) {
        this.eventLog = eventLog;
    }

    //The following aren't really "StateComponent" methods and should be rehoused at some stage
    public String getModelInfoAsString() throws RemoteException, SmartFrogResolutionException {
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1]);

        /*  EXAMPLE:
       sfModelMetaData extends DATA {
      description "This is the foobar service";
      links extends DATA {
         -- extends DATA {
           description "link to my friend";
           link "/friend";
         }
      }
   }
        */

        StringBuilder result = new StringBuilder();

        ComponentDescription metaData = me.sfResolve(MODELMETADATA, (ComponentDescription) null, false);

        if (metaData != null) {
            result.append(MAINHEADER).append(metaData.sfResolve(DESCRIPTION).toString()).
                    append(MAINHEADERCLOSE);

            ComponentDescription links = (ComponentDescription) metaData.sfContext().get(LINKS);
            if (links != null) {
                Enumeration keys = links.sfContext().keys();
                while (keys.hasMoreElements()) {
                    ComponentDescription link = (ComponentDescription) links.sfContext().get(keys.nextElement());
                    result.append(SCRIPTHEADER).
                            append("<A HREF=\"").
                            append(link.sfContext().get(LINK)).
                            append("\">").
                            append(link.sfContext().get(DESCRIPTION)).
                            append("</A>").
                            append(SCRIPTHEADERCLOSE);
                    //append(LINEBREAK);
                }
            }
        }
        if (sfLog.isDebugEnabled())
            sfLog.debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");

        return result.toString();
    }

    public String getTransitionLogAsString() throws RemoteException, SmartFrogResolutionException {
        if (sfLog.isDebugEnabled())
            sfLog.debug(Thread.currentThread().getStackTrace()[1]);

        StringBuilder result = new StringBuilder();

        if (eventLog != null) {
            int count = eventLog.sfResolve(COUNT, 0, true);
            for (int i = 0; i < count; i++) {
                String transition = "transition" + i;
                result.append(SCRIPTHEADER).
                        append(transition).
                        append(": ").
                        append((String) eventLog.sfContext().get(transition)).
                        append(SCRIPTHEADERCLOSE);
            }
        }
        if (sfLog.isDebugEnabled())
            sfLog.debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");

        return result.toString();
    }

    void observed(ComponentDescription entry, StringBuilder result, int indent) throws SmartFrogResolutionException, RemoteException {
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1]);
        ComponentDescription observed = (ComponentDescription) entry.sfContext().get(OBSERVED);
        if (observed != null) {
            boolean cont = true;
            /*if (observed.sfContext().get(GUARD)!=null){
                Boolean guardEval = (Boolean) observed.sfResolve(GUARD);
                if ((guardEval != null) && !(guardEval)) cont=false;
            }*/
            if (cont) {

                boolean annotate = observed.sfResolve(ANNOTATE, false, false);

                String description = null;
                try {
                    description = observed.sfResolve(DESCRIPTION).toString();
                } catch (SmartFrogResolutionException e) {
                    sfLog.debug(e);
                }


                sfLog.debug("ATTRIBUTE! ");
                String attribute = null;
                try {
                    attribute = observed.sfResolve(ATTRIBUTE).toString();
                } catch (SmartFrogResolutionException e) {
                    sfLog.debug(e);
                    return; //take no action...
                }

                sfLog.debug("ATTRIBUTE! " + attribute);
                String value = null;
                try {
                    value = me.sfResolve(attribute).toString();
                } catch (SmartFrogResolutionException e) {
                    sfLog.debug(e);
                    return; //take no action...
                }

                sfLog.debug("VALUE! " + value);

                if (description != null) {
                    result.append(SCRIPTHEADER1).append(indent).append(SCRIPTHEADER2).//append(SCRIPTHEADER).
                            append(ITAL).
                            append(description).append(ITALCLOSE);//.append(SCRIPTHEADERCLOSE);
                }


                sfLog.debug("ALIASES! ");
                ComponentDescription aliases = null;
                try {
                    aliases = (ComponentDescription) observed.sfResolve(ALIASES);
                } catch (SmartFrogResolutionException ignore) {

                }
                if (aliases != null) {
                    String alias = (String) aliases.sfContext().get(value);
                    if (alias != null) value = alias;
                }
                sfLog.debug("VALUE " + value);
                result.append(SOMEPADDING).//append(SCRIPTHEADER1).append(indent).append(SCRIPTHEADER2).
                        append(value).append(annotate ? POBSERVED : "").append(SCRIPTHEADERCLOSE);
            }
        }
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }

    void desired(ComponentDescription entry, StringBuilder result, int indent) throws SmartFrogResolutionException, RemoteException {
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1]);
        ComponentDescription desired = (ComponentDescription) entry.sfContext().get(DESIRED);
        boolean cont = true;
        if (desired != null) {

            Vector values = null;
            try {
                values = (Vector) desired.sfResolve(VALUES);
            } catch (SmartFrogResolutionException ignore) {

            }

            sfLog.debug("DESIREDGUARD! ");

            boolean annotate = desired.sfResolve(ANNOTATE, false, false);

            String description = null;
            try {
                description = desired.sfResolve(DESCRIPTION).toString();
            } catch (SmartFrogResolutionException e) {
                sfLog.debug(e);
            }

            if (description != null) {
                result.append(SCRIPTHEADER1).append(indent).append(SCRIPTHEADER2).//append(SCRIPTHEADER).
                        append(ITAL).
                        append(description).append(ITALCLOSE);//.append(SCRIPTHEADERCLOSE);
            }

            String attribute = null;
            try {
                attribute = desired.sfResolve(ATTRIBUTE).toString();
            } catch (SmartFrogResolutionException e) {
                sfLog.ignore(e);
                return; //ok
            }

            String cvalue = me.sfContext().get(attribute).toString();
            if (values != null) {

                sfLog.debug("DESIREDATTR! ");

                ComponentDescription aliases = null;
                try {
                    aliases = (ComponentDescription) desired.sfResolve(STATEALIASES);
                } catch (SmartFrogResolutionException ignore) {

                }
                sfLog.debug("DESIREDSTATEALIAS! ");

                if (aliases != null) {
                    String alias = (String) aliases.sfContext().get(cvalue);
                    if (alias != null) cvalue = alias;
                }
                result.append(SOMEPADDING).//append(SCRIPTHEADER1).append(indent).append(SCRIPTHEADER2).
                        append(cvalue).append(annotate ? PDESIRED : "").append(SCRIPTHEADERCLOSE);
            }


            sfLog.debug("DESIREDSTATEPRINT! ");

            /*try {
                Boolean guardEval = (Boolean) desired.sfResolve(GUARD);
                if ((guardEval != null) && !(guardEval)) cont = false; //round while
            } catch (Exception ignore) {

            }*/

            if (cont) {

                result.append(SCRIPTHEADER1).append(indent + 1).append(SCRIPTHEADER2).append(SETVALUE);


                ComponentDescription aliases = null;
                try {
                    aliases = (ComponentDescription) desired.sfResolve(ACTIONALIASES);
                } catch (SmartFrogResolutionException ignore) {

                }

                sfLog.debug("DESIREDACTIONALIAS! ");

                String dStateName = ":::" + fullName + ":::" + attribute;
                if (values != null) {

                    result.append("<SELECT name=\"").append(dStateName).append("\">");
                    for (Object value : values) {
                        result.append("<OPTION value=\"");
                        String realValue = value.toString();
                        String shownValue = realValue;
                        if (aliases != null) {
                            String alias = (String) aliases.sfContext().get(realValue);
                            if (alias != null) shownValue = alias;
                        }
                        result.append(realValue).append("\">").
                                append(shownValue).append("</OPTION>");
                    }
                    result.append("</SELECT>");

                } else {
                    result.append("<INPUT type=\"text\" ").//value=\"").append(cvalue).append("\" ").
                            append("name=\"").append(dStateName).append("\"/>");
                }
                result.append(SCRIPTHEADERCLOSE);
                sfLog.debug("DESIREDSTATESET! ");
            }
        }
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }


    void links(ComponentDescription entry, StringBuilder result, int indent) {
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1]);
        ComponentDescription links = null;
        try {
            links = (ComponentDescription) entry.sfResolve(LINKS);
        } catch (SmartFrogResolutionException ignore) {

        } catch (ClassCastException ignore) {

        }
        if (links != null) {

            sfLog.debug("LINKS...yes ");
            String dns = null;
            try {
                dns = (String) me.sfResolve(DNS);
            } catch (Exception ignore) {

            }

            sfLog.debug("LINKS...dns " + dns);

            if (dns != null) {
                Enumeration keys = links.sfContext().keys();
                while (keys.hasMoreElements()) {
                    ComponentDescription link = (ComponentDescription) links.sfContext().get(keys.nextElement());
                    sfLog.debug("LINKS...link " + link);
                    try {
                        Boolean guardEval = (Boolean) link.sfResolve(GUARD);
                        if ((guardEval != null) && !(guardEval)) continue; //round while
                    } catch (Exception ignore) {
                        sfLog.debug("LINKS...did not resolve guard " + ignore);
                    }
                    result.append(SCRIPTHEADER1).append(indent + 1).append(SCRIPTHEADER2).
                            append("<A HREF=\"http://").append(dns).
                            append(link.sfContext().get(LINK)).
                            append("\">").
                            append(link.sfContext().get(DESCRIPTION)).
                            append("</A>").
                            append(SCRIPTHEADERCLOSE);
                }
            }

        }
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
    }

    public String getOrchestrationStateDetails() throws RemoteException, SmartFrogResolutionException {
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1]);

        //First off - what about me?
        boolean qmd = me.sfResolve(QUERYMETADATA, false, false);
        sfLog.debug("QueryMetaData:"+qmd);
        StringBuilder details = new StringBuilder();
        if (qmd){
            details.append(getOrchestrationStateDetailsWkr());
        }

        Prim parent = me.sfParent();
        if (parent!=null) {
            sfLog.debug("$$$$"+parent.sfAttributeKeyFor(me));
        } else {
            sfLog.debug("$$$$root");
        }

        //Do I have any children?
        sfLog.debug("Children:");
        if (me instanceof Compound){
            Compound cme = (Compound) me;
            
            for (Enumeration<Liveness> e = cme.sfChildren(); e.hasMoreElements();) {
                sfLog.debug("Children:yes");

                Prim c = (Prim) e.nextElement();

                details.append(new QueryMetaData(c, sfLog).getOrchestrationStateDetails());
            }
        }

        return details.toString();
    }

    public String getOrchestrationStateDetailsWkr() throws RemoteException, SmartFrogResolutionException {
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1]);

        ComponentDescription metaData = me.sfResolve(INFORMMETADATA, (ComponentDescription) null, false);

        StringBuilder status = new StringBuilder();

        if (metaData != null) {
            boolean show = me.sfResolve(SHOW, false, false);
            String cdescription = me.sfResolve(DESCRIPTION, (String) null, false);
            int indent = me.sfResolve(MYINDENT, MYINDENTDEFAULT, false) - 1;

            String myParent = MAINDIV;
            try {
                Object myParentObj = me.sfResolve(PARENT);
                if (myParentObj instanceof String) {
                    Reference myParentRef = Reference.fromString(myParentObj.toString());
                    myParentObj = me.sfResolve(myParentRef);
                }
                Prim myParentPrim = (Prim) myParentObj;
                myParent = myParentPrim.sfResolve(FULLNAMEPATH).toString();
                myParent = myParent + SFDELIM + CONTAINER;
            } catch (Exception e) {
                sfLog.debug(e); //ok
            }

            sfLog.debug("PARENT:::"+myParent);

            //Ok this should be moved...(hence direct specified literal)
            String vmName = me.sfResolve("vmName", (String) null, false);
            String additional = (vmName != null ? SOMEPADDING + "(" + vmName + ")" + SOMEPADDING : "");

            status.append("<COMP>");
            status.append("<STATUS>").append(show ? SHOW : NOSHOW).append("</STATUS>");
            status.append("<DESCRIPTION>").append(cdescription).append(additional).append("</DESCRIPTION>");
            String resource = fullNamePath + SFDELIM + CONTAINER;
            status.append("<PATH>").append(resource).append("</PATH>");
            status.append("<PARENT>").append(myParent).append("</PARENT>");
            status.append("<INDENT>").append(indent).append("</INDENT>");
            status.append("</COMP>");

            StringBuilder extra = new StringBuilder();

            Enumeration keys = metaData.sfContext().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                ComponentDescription entry = null;
                try {
                    entry = (ComponentDescription) metaData.sfContext().get(key);
                } catch (Exception e) {
                    continue; //round while
                }

                sfLog.debug("ENTRY!!! " + entry + ":" + key);
                sfLog.debug("***GUARD***" + entry.sfContext().get(GUARD));

                try {
                    Boolean guardEval = (Boolean) entry.sfResolve(GUARD);
                    if ((guardEval != null) && !(guardEval)) continue; //round while
                } catch (Exception ignore) {
                    sfLog.debug(ignore);
                }

                sfLog.debug("***GUARD*** PASSES");

                ComponentDescription observed = (ComponentDescription) entry.sfContext().get(OBSERVED);
                ComponentDescription links = null;
                try {
                    links = (ComponentDescription) entry.sfResolve(LINKS);
                } catch (SmartFrogResolutionException ignore) {

                } catch (ClassCastException ignore) {

                }

                sfLog.debug("***LINKS***" + links);
                sfLog.debug("***OBSERVED***" + observed);

                if (links != null || observed != null) {
                    extra.append("<COMP>");
                    extra.append("<STATUS>").append(observed != null ? "updating" : "static").append("</STATUS>");

                    resource = fullNamePath + SFDELIM + key + SFDELIM + OBSERVED;
                    extra.append("<PATH>").append(resource).append("</PATH>");
                    extra.append("<PARENT>").append(fullNamePath).append(SFDELIM).append(CONTAINER).append("</PARENT>");
                    extra.append("</COMP>");
                }

                ComponentDescription desired = (ComponentDescription) entry.sfContext().get(DESIRED);
                sfLog.debug("***DESIRED***" + desired);

                if (desired != null) {
                    extra.append("<COMP>");
                    extra.append("<STATUS>").append("static").append("</STATUS>");
                    resource = fullNamePath + SFDELIM + key + SFDELIM + DESIRED;
                    extra.append("<PATH>").append(resource).append("</PATH>");
                    extra.append("<PARENT>").append(fullNamePath).append(SFDELIM).append(CONTAINER).append("</PARENT>");
                    extra.append("</COMP>");
                }
            }

            if (extra.toString().length() == 0) return "";
            else status.append(extra);
        }
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");

        return status.toString();
    }

    public String getOrchestrationStateObserved(String key) throws RemoteException, SmartFrogResolutionException {
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1]);


        StringBuilder result = new StringBuilder();

        ComponentDescription metaData = me.sfResolve(INFORMMETADATA, (ComponentDescription) null, false);
        ComponentDescription entry = (ComponentDescription) metaData.sfContext().get(key);

        sfLog.debug("OBSERVED! " + key);

        int indent = me.sfResolve(MYINDENT, MYINDENTDEFAULT, false);

        observed(entry, result, indent);

        //ANY LINKS?
        links(entry, result, indent);

        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
        return result.toString();
    }

    public String getOrchestrationStateDesired(String key) throws RemoteException, SmartFrogResolutionException {
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1]);


        StringBuilder result = new StringBuilder();

        ComponentDescription metaData = me.sfResolve(INFORMMETADATA, (ComponentDescription) null, false);
        ComponentDescription entry = (ComponentDescription) metaData.sfContext().get(key);

        int indent = me.sfResolve(MYINDENT, MYINDENTDEFAULT, false);

        desired(entry, result, indent);
        sfLog.debug("DESIRED! " + key);

        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
        return result.toString();
    }

    //TO DO!!!
    public String getOrchestrationStateContainer() throws RemoteException, SmartFrogResolutionException {
        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1]);
        StringBuilder result = new StringBuilder();

        //This should be removed but it requires changing the index.html implementation to not ask for a "container!"...

        if (sfLog.isDebugEnabled()) sfLog.debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");
        return result.toString();
    }

}
