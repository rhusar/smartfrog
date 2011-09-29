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

/**
 * created 15-Dec-2009 10:17:42
 */

public class Constants {
    public static final String QUERYMETADATA = "sfQueryMetaData";
    public static final String MAPPING = "sfMapping";
    public static final String ASANDCONNECTOR="asAndConnector";
    public static final String BY = "by";
    public static final String COUNT = "count";
    public static final String DO_SCRIPT = "doScript";
    public static final String DPE="DynamicPolicyEvaluation";
    public static final String ENABLED = "enabled";
    public static final String EXISTS = "exists";
    public static final String EVENTLOG = "sfOrchEventLog";
    public static final String FINALIZE = "finalize";
    public static final String FUNCTIONCLASS = "sfFunctionClass";
    public static final String ISSTATECOMPONENTTRANSITION = "sfIsStateComponentTransition";
    public static final String LAG="lag";
    public static final String NAME = "name";
    public static final String NORMALTERMINATION = "sfNormalTermination";
    public static final String NOT = "not";
    //public static final String ON = "on";
    public static final String ORCHMODEL = "sfIsOrchModel";
    public static final String THREADEDCOMP = "sfIsThreadedComposite";
    public static final String NOTIFDELAY = "sfNotificationDelay";
    public static final String PREPARE = "prepare";
    public static final String RELEVANT = "relevant";
    public static final String REQUIRES_THREAD = "requiresThread";
    public static final String RUN = "run";
    public static final String RUNNING = "running";
    public static final String T_FINALIZE = "tFinalize";
    public static final String T_ONTERMINATION = "tOnTermination";
    public static final String T_PREPARE = "tPrepare";
    public static final String THREADPOOL = "threadpool";
    public static final String TRANSITION = "transition";

    public static final String PARENT = "parent";
    public static final String DESCRIPTION = "description";
    public static final String MYINDENT = "indent";
    public static final int MYINDENTDEFAULT = 2;
    public static final String MODELMETADATA = "sfModelMetaData";
    public static final String IGNOREMETADATA = "sfIgnoreMyMetaData";
    public static final String MODELMETADATAATTR = "modelMetaData";
    public static final String INFORMMETADATA = "sfInformMetaData";
    public static final String DESIREDMETADATA = "sfDesiredMetaData";
    public static final String LINKS = "links";
    public static final String LINK = "link";
    public static final String FEEDBACK = "feedback";
    public static final String ATTRIBUTE = "attribute";
    public static final String ALIASES = "aliases";
    public static final String STATEALIASES = "statealiases";
    public static final String ACTIONALIASES = "actionaliases";
    public static final String GUARD = "guard";

    public static final String SERVICEDASH = "/service/dashboard/";
    public static final String SERVICE = "service";

    public static final String STDOUT = "stdout";
    public static final String STDERR = "stderr";

    public static final String STATIC = "static";
    public static final String UPDATING = "updating";
    public static final String MAINDIV = "mainDiv";
    public static final String DETAILS = "details";
    public static final String DESIRED = "desired";
    public static final String OBSERVED = "observed";
    public static final String CONTAINER = "container";
    public static final String MODELINFO = "info";
    public static final String TLOG = "tlog";
    public static final String PATHDELIM = "/";
    public static final String SFDELIM = ":";
    public static final String PDESIRED = " (desired) ";
    public static final String POBSERVED = " (observed) ";
    public static final String ANNOTATE = "annotate";
    public static final String SERVICERESOURCE = "/service/resource/";
    public static final String FULLNAMEPATH = "fullNamePath";

    public static final String DNS = "vmExtDns";
    
    public static final String VALUES = "values";

    public static final String MAINHEADER = "<H1>";
    public static final String MAINHEADERCLOSE = "</H1>";
    public static final String MIDHEADER = "<H3>";
    public static final String MIDHEADERCLOSE = "</H3>";
    public static final String SMALLHEADER = "<H5>";
    public static final String SMALLHEADERCLOSE = "</H5>";
    public static final String SOMEPADDING = "<span style=\"padding-left:20px\"/>";
    public static final String SCRIPTHEADER = "<H6>";
    public static final String SCRIPTHEADER1 = "<H6 style=\"padding-left: ";
    public static final String SCRIPTHEADER2 = "0pt;\">";
    public static final String SCRIPTHEADERCLOSE = "</H6>";
    public static final String LINEBREAK = "<BR>";
    public static final String MAINLINEBREAK = "<BR><P>";

   
    public static final String TABLE= "";//"<TABLE BORDER=1>";
    public static final String TABLECLOSE = "";//"</TABLE>";
    public static final String HR = "<BR><HR><BR>";
    public static final String SMALLHR = "";//<HR SIZE=7>";
    public static final String MIDHR = "";//<HR SIZE=15>";
    public static final String ITAL = "<I>";
    public static final String ITALCLOSE = "</I>";
    public static final String SHOW = "show";
    public static final String NOSHOW = "noshow";
    public static final String DECOR = "decoration";
    public static final String INFO = "info";

    public static final String SETVALUE = "Set desired: ";
}
