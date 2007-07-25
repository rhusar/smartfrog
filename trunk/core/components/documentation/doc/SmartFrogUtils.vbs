REM  *****  BASIC  *****



' see http://www.oooforum.org/forum/viewtopic.phtml?t=3772

Sub ConvertWordToPDF( cFile)
    ConvertToPDF(cFile,False,true)
end sub

Sub InnerConvertToPDF( cFile ,hidden,quiet)
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
    if not quiet then
        Print "["+cURL+"] => ["+url2+"]"
    end if
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



' Convert a bunch of SXW Documents.

Sub BulkConvert(cFolder)
    InnerConvert(cFolder,true)
end sub

Sub InnerConvert(cFolder,quiet)
' This is the hardcoded pathname to a folder containing sxw files.
'cFolder = "/home/someone/temp"

    ' Get the pathname of each file within the folder.
    pattern=cFolder + "/*.*"
    cFile = Dir$( pattern )
    converted = 0
    Do While cFile <> ""
    ' If it is not a directory...
        If cFile <> "." And cFile <> ".." and  LCase( Right( cFile, 4 ) ) = ".sxw" Then
            converted = converted + 1
            InnerConvertToPDF(cFolder+"/"+cFile,false,quiet)
        EndIf
    cFile = Dir$
    Loop
    if converted=0 then
        Print "Warning, no files matching "+pattern+" were found"
    end if
End Sub
