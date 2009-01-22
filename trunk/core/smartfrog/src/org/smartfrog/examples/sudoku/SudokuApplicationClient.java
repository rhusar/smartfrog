package org.smartfrog.examples.sudoku;

import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Response;
import org.smartfrog.services.restlet.overrides.ProxyEnabledClient;

public class SudokuApplicationClient extends BaseSudokuApplication {
	
    //static public String hostip = "16.25.168.89:8080";
        static public String hostip = "www-apps.hpl.hp.com";
	
	public void process(){
		//Collect together puzzle input...
		
		String url="http://"+hostip+"/sudoku/solver";
	        //String url="http://"+hostip+"/solver";
		
		boolean first=true;
		
		for(int i=0;i<bsfe.board.size();i++){
			String text = bsfe.board.get(i).getText();
			if (text.equals("")) continue;
			if (first) {
				url+="?";
				first=false;
			}
			else url+="&";
			Integer val = Integer.parseInt(text);
			url+=""+i+"="+val;
		}
		
		//System.out.println(url);
		
		// Define our Restlet HTTP client.  
        //Client client = new Client(Protocol.HTTP);  
  
		Client client = new ProxyEnabledClient(Protocol.HTTP);
		
        // The URI of the resource "list of items".  
        Reference reference = new Reference(url);

        Response response = client.get(reference);  
        
        String result = null;
        
        if (response.getStatus().isSuccess()) {  
               if (response.isEntityAvailable()) {
            	   try {result = response.getEntity().getText();} catch (Exception e){}
               }  
         }           
        
        if (result!=null){
	        while (true){
	        	int idx = result.indexOf("=");
	        	if (idx==-1) break;
	        	String sq_s = result.substring(0, idx);
	        	int idx1 = result.indexOf("&");
	        	String val_s = result.substring(idx+1,idx1);
	        	result = result.substring(idx1+1);
	        	Integer sq_i = Integer.parseInt(sq_s);
	        	bsfe.board.get(sq_i.intValue()).setText(val_s);
	        }
	        bsfe.unsolvable.setText("");
        } else bsfe.unsolvable.setText("No solution found");
	}
	
	public static void main(String[] args){
		SudokuApplicationClient frame = new SudokuApplicationClient();
		frame.app_init();
	}
}
