<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:err="http://gridforum.org/cddlm/serviceAPI/faultsets/2004/10/11/" version="1.0">
<!--   <xsl:output method="html" encoding="us-ascii"/> -->

  <xsl:template match="/err:faults">
<html>
<head></head>
<body>

    <xsl:apply-templates select="err:constants"/>

    <xsl:apply-templates select="err:faultset"/>
</body>
</html>

  </xsl:template>

  <!-- this is the faultset handler. 
    creates a const string declaration of the ns
    -->
  <xsl:template match="err:faultset">

  <h1>Faults</h1>
  
  Faults are in the namespace <tt><xsl:value-of select="@namespace"/></tt>
  <p></p>
    <xsl:value-of select="err:description"/>
  <p></p>
  
  <table border="1">
    <xsl:apply-templates select="err:fault"/>
  </table>
  </xsl:template>


  <!-- for each fault we create const strings for code and wire message
  -->
  <xsl:template match="err:fault">
  <tr>
  <td><xsl:value-of select="err:code"/></td>
  <td><xsl:value-of select="err:wiremessage"/></td>
  <td><xsl:value-of select="err:errormessage"/></td>
  </tr>
  <tr>
  <td colspan="3"><xsl:value-of select="err:documentation"/></td>
  </tr>
  </xsl:template>


  <!--constants -->
  <xsl:template match="err:constants">
    <h2>URIs</h2>
    <table>
        <xsl:apply-templates select="err:uri"/>
    </table>
    <h2>Strings</h2>
    <table>
        <xsl:apply-templates select="err:string"/>
    </table>
        
    <h2>Integers</h2>
    <table>
      <xsl:apply-templates select="err:int"/>
    </table>
  </xsl:template>


  <xsl:template match="err:uri">

  <tr>
    <td><xsl:value-of select="@name"/></td>
    <td><xsl:value-of select="@value"/></td>
    <td ><xsl:value-of select="err:description"/></td>
  </tr>
  </xsl:template>
  
  
  <xsl:template match="err:string">
    <tr>
      <td><xsl:value-of select="@name"/></td>
      <td><xsl:value-of select="@value"/></td>
      <td ><xsl:value-of select="err:description"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="err:int">
    <tr>
      <td><xsl:value-of select="@name"/></td>
      <td><xsl:value-of select="@value"/></td>
      <td ><xsl:value-of select="err:description"/></td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
