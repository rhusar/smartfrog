package org.smartfrog.examples.sudoku;

import java.io.IOException;

import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Response;

public class TestClient {
	
	public static void main(String[] args) throws IOException {  
         // Define our Restlet HTTP client.  
         Client client = new Client(Protocol.HTTP);  
   
         // The URI of the resource "list of items".  
         Reference reference = new Reference("http://localhost:8182/foo");

         Response response = client.get(reference);  
             if (response.getStatus().isSuccess()) {  
                if (response.isEntityAvailable()) {  
                    response.getEntity().write(System.out);  
                }  
          }           
	 }
}
