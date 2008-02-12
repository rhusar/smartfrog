/* (C) Copyright 2007-2008 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/

package org.smartfrog.services.xmpp;

import java.util.Map;
/**
 * Interface to monitoring event information sent from client 
 * nodes to server. This is independent of the protocol used for monitoring 
 * abd should be implemented for a specific Monitoring protocol type. 
 * @author sanjaydahiya
 *
 */
public interface MonitoringEvent {

	public static String HOST = "_host" ;
	public static String MODULEID = "_moduleId";
	public static String MESSAGE = "_msg" ;
	public static String MODULE_STATE = "_moduleState" ;
	public static String INSTANCE_NAME = "_instanceName" ;
	public static String MESSAGE_TYPE = "_msgType" ;
	public static String ACTION_NAME = "_actionName" ;
	
	public String getModuleId() ;
	public String getInstanceName();
	
	public String getHost() ;
	public String getModuleState() ; 
	public int getMessageType();
	

	public String getMsg() ;
	public Map<String, String> getPropertyBag(); 
	public String getTimestamp();
	public String getLastAction();
	
	public void setModuleId(String id); 
	public void setHost(String h) ; 
	public void setModuleState(String state) ; 
	public void setInstanceName(String instanceName);
	public void setMessageType(int type);
	
	public void setMsg(String m) ; 
	public void setTimestamp(String t);
	public void addToPropertyBag(String key, String value) ; 
	public void setLastAction(String action);
}
