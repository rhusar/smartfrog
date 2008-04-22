/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.monitor.ganglia;

import org.smartfrog.avalanche.server.monitor.ModuleState;
import org.smartfrog.avalanche.server.monitor.MonitoringEventListener;
import org.smartfrog.avalanche.server.monitor.MonitoringServer;
import org.smartfrog.avalanche.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Ganglia interface for sever side, it connects to gmond given the host/port and 
 * gets the state as XML. 
 * @author sanjaydahiya
 *
 */
public class GmondAdapter implements MonitoringServer {
	
	private String gmondHost = "lx9622.india.hp.com" ;
	private int gmondXMLExportPort = 8649 ;
	private String state ;
	private Document doc ;
	
	/**
	 * Connects to default configured host and port for state.
	 * Defaults are localhost/8649
	 *
	 */
	public void update(){
		update(gmondHost, gmondXMLExportPort);
	}
	
	/**
	 * gets the new XML from gmond, doesnt provide incremental update, all of the state
	 * is updated with new XML from gmond.
	 * @param host host name of the gmond
	 * @param port port of XML export thread of gmond (see gmond.conf)
	 */
	public void update(String host, int port){
		try{
			Socket socket = new Socket(host, port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String str = null;
			StringBuffer buf = new StringBuffer();
			while(null != (str=in.readLine())){
				buf.append(str + "\n");
	    		}
			in.close();
			socket.close();
			state = buf.toString();
			//TODO: bad code fix it. 
			doc = getStateAsXML();
		}catch(UnknownHostException e){
			e.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	public String getState(){
		return this.state;
	}
	
	public Document getStateAsXML(){
		Document dom = null;
		try{
			dom = XMLUtils.loadFromString(this.state, false);
		}catch(Exception e){
			e.printStackTrace();
		}
		return dom;
	}
	
	public void registerListener(MonitoringEventListener listener) {
		// TODO Auto-generated method stub

	}

	public void unregisterListener(MonitoringEventListener listener) {
		// TODO Auto-generated method stub

	}
	
	public String[] listHosts(){
		String xpath = "/GANGLIA_XML/CLUSTER/HOST/@NAME" ;
		
		ArrayList nodes = XMLUtils.getElementsByXPath(doc, xpath);
		
		String []hosts = new String[nodes.size()];
		for( int i=0;i<hosts.length;i++){
			hosts[i] = ((Node)nodes.get(i)).getNodeValue();
		}
		return hosts;
	}
	
	public String[] listModuleForHost(String host){
		// FIXME: SOURCE should be something unique to Avalanche, see if gmetric SOURCE string 
		// can be changed
		String xpath = "/GANGLIA_XML/CLUSTER/HOST[@NAME=\"" + host +"\"]/METRIC[@SOURCE=\"gmetric\"]/@NAME" ;
		ArrayList nodes = XMLUtils.getElementsByXPath(doc, xpath);
		String []modules = new String[nodes.size()];
		for( int i=0;i<modules.length;i++){
			modules[i] = ((Node)nodes.get(i)).getNodeValue();
		}
		return modules;
	}
	
	public ModuleState getModuleState(String host, String moduleId){
		ModuleState state = null;
		try{
			
			String xpath = "/GANGLIA_XML/CLUSTER/HOST[@NAME=\"" + host +"\"]/METRIC[@NAME=\""+moduleId + "\"]" ;
			System.out.println(xpath);
			Element n = (Element)XMLUtils.getElementByXPath(doc, xpath	);
			if ( null != n){
				String val = n.getAttribute("VAL");
				StringTokenizer tok = new StringTokenizer(val, ",");
				state = new ModuleState();
				
				state.setModuleId(moduleId);
				state.setHost(host);
				
				while( tok.hasMoreTokens() ){
					String val1 = tok.nextToken();
					int tag = val1.indexOf("=");
					
					String key = val1.substring(0, tag);
					String value = val1.substring(tag+1, val1.length());
					
					if (key.equals("STATUS")){
						state.setStatus(value);
					}else if(key.equals("MSG")){
						state.setMessage(value);
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return state;
	}

}
