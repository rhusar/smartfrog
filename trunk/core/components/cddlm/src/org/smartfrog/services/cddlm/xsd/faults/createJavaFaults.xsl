<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:err="http://gridforum.org/cddlm/serviceAPI/faultsets/2004/07/30" version="1.0">
  <xsl:output method="text" encoding="us-ascii"/>

  <xsl:template match="/err:faults">

package org.smartfrog.services.cddlm.generated.faults;    

/**
 This file defines SOAP error codes for axis.
 This is machine generated! Do Not Edit!
*/

public class FaultCodes {
    
    /**
      empty private constructor -this class can not be instantiated
    */
    private FaultCodes() { }


    <xsl:apply-templates select="err:faultset"/>

//end class
}


  </xsl:template>

  <!-- this is the faultset handler. 
    creates a const string declaration of the ns
    -->
  <xsl:template match="err:faultset">

 /**
    <xsl:value-of select="err:description"/>
*/

    public static final String
    <xsl:value-of select="@name"/>_NAMESPACE =
    &quot;
    <xsl:value-of select="@namespace"/>&quot;;

    <xsl:apply-templates select="err:fault"/>

  </xsl:template>


  <!-- for each fault we create const strings for code and wire message
  -->
  <xsl:template match="err:fault">
  
      /**
    <xsl:value-of select="err:documentation"/>
     */
   
     public static final String
    <xsl:value-of select="err:name"/>_FAULTCODE =
    &quot;
    <xsl:value-of select="err:code"/>&quot;;

        /**
        * text to go with the
    <xsl:value-of select="err:code"/> error
        */
     public static final String
    <xsl:value-of select="err:name"/>_WIRE_MESSAGE =
    &quot;
    <xsl:value-of select="err:wiremessage"/>&quot;;


        /**
        * text to go with the
    <xsl:value-of select="err:code"/> error
        */
     public static final String
    <xsl:value-of select="err:name"/>_ERROR_MESSAGE =
    &quot;
    <xsl:value-of select="err:errormessage"/>&quot;;

  </xsl:template>

</xsl:stylesheet>
