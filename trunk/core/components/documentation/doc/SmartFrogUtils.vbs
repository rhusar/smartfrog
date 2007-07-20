REM  *****  BASIC  *****
' see http://www.oooforum.org/forum/viewtopic.phtml?t=3772

' These routines need to be added to a script librarie called SmartFrog.Utils, which you create in OOO using the
' Organizer. 

Sub test( cArg )
   Print "|"+cArg+"|"
End Sub

Sub debugRun()
	 ConvertToPDF("/home/slo/Projects/SmartFrog/Forge/core/components/documentation/src/documentation/content/xdocs/smartfrogdoc/kernel/sfWorkflow.sxw",false)
end sub

Sub ConvertWordToPDF( cFile)
	ConvertToPDF(cFile,False)
end sub

Sub ConvertToPDF( cFile ,hidden)
    hiddenValue=MakePropertyValue( "Hidden", hidden )
    exportValue=MakePropertyValue( "FilterName", "writer_pdf_Export" )
    cURL = ConvertToURL( cFile )



    ' Open the document.
    ' Just blindly assume that the document is of a type that OOo will
    '  correctly recognize and open -- without specifying an import filter.
    oDoc = StarDesktop.loadComponentFromURL( cURL, "_blank", 0, Array(_
           hiddenValue ,_
            ) )


    file2 = Left( cFile, Len( cFile ) - 4 ) + ".pdf"
    url2 = ConvertToURL( file2 )
    'Print "["+cURL+"] => ["+url2+"]"
    ' Save the document using a filter.
    oDoc.storeToURL( url2, Array(exportValue,))
    oDoc.close( True )
    set oDoc=Nothing
End Sub


Function MakePropertyValue( Optional cName As String, Optional uValue ) As com.sun.star.beans.PropertyValue
   Dim oPropertyValue As New com.sun.star.beans.PropertyValue
   If Not IsMissing( cName ) Then
      oPropertyValue.Name = cName
   EndIf
   If Not IsMissing( uValue ) Then
      oPropertyValue.Value = uValue
   EndIf
   MakePropertyValue() = oPropertyValue
End Function

