<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:err="http://gridforum.org/cddlm/serviceAPI/faultsets/2004/10/11/" version="1.0">
  <xsl:output method="text" encoding="us-ascii"/>

  <xsl:template match="/err:faults">

package org.smartfrog.services.cddlm.generated.api;
import javax.xml.namespace.QName;
import org.apache.axis.types.URI;
/**
 This file defines SOAP error codes for axis.
 This is machine generated! Do Not Edit!
*/

public class DeployApiConstants {
    
    /**
      empty private constructor -this class can not be instantiated
    */
    private DeployApiConstants() { }

    <xsl:apply-templates select="err:constants"/>

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
    &quot;<xsl:value-of select="@namespace"/>&quot;;

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
    &quot;<xsl:value-of select="err:code"/>&quot;;

        /**
        * text to go with the
    <xsl:value-of select="err:code"/> error
        */
     public static final String
    <xsl:value-of select="err:name"/>_WIRE_MESSAGE =
    &quot;<xsl:value-of select="err:wiremessage"/>&quot;;


        /**
        * text to go with the
    <xsl:value-of select="err:code"/> error
        */
     public static final String
    <xsl:value-of select="err:name"/>_ERROR_MESSAGE =
    &quot;<xsl:value-of select="err:errormessage"/>&quot;;

      /**
    <xsl:value-of select="err:documentation"/>
     */
    public static final QName
      FAULT_<xsl:value-of select="err:name"/>=
            new QName(CDDLM_FAULT_NAMESPACE,
    <xsl:value-of select="err:name"/>_FAULTCODE);
  </xsl:template>


  <!--constants -->
  <xsl:template match="err:constants">
    <xsl:apply-templates select="err:uri"/>
    <xsl:apply-templates select="err:string"/>
    <xsl:apply-templates select="err:int"/>
  </xsl:template>


  <xsl:template match="err:uri">

    /**
    <xsl:value-of select="err:description"/>
    */

    public static final String
    <xsl:value-of select="@name"/> =
    &quot;<xsl:value-of select="@value"/>&quot;;

    /**
    <xsl:value-of select="err:description"/>
    */
    public static final URI URI_<xsl:value-of select="@name"/>;
    //static initializer
    static {
        try {
            URI_<xsl:value-of select="@name"/> =
              new URI(&quot;<xsl:value-of select="@value"/>&quot;);
        } catch (URI.MalformedURIException e) {
            throw new RuntimeException("Cannot instantiate URI_<xsl:value-of select="@name"/>");
        }
    }



  </xsl:template>
  <xsl:template match="err:string">

    /**
    <xsl:value-of select="err:description"/>
    */

    public static final String
    <xsl:value-of select="@name"/> =
    &quot;<xsl:value-of select="@value"/>&quot;;
  </xsl:template>
  <xsl:template match="err:int">

    /**
    <xsl:value-of select="err:description"/>
    */

    public static final int
    <xsl:value-of select="@name"/> =
    <xsl:value-of select="@value"/>;
  </xsl:template>

</xsl:stylesheet>
