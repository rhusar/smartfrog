<%-- /**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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
*/ --%>
<%-- $Id: Downloader.jsp 81 2006-05-30 06:09:38Z uppada $ --%>
<%@ page language="java" contentType="application/binary" %>

<%@ page import="java.io.*" %>

<%@ include file="web/InitBeans.jsp" %>
<%
    String file = request.getParameter("filePath");
    File fname = new File(factory.getAvalancheHome() + File.separatorChar + "smartfrog" + File.separatorChar + "lib" + File.separatorChar + file);
    if (fname.exists()) {
        FileInputStream istr = null;
        OutputStream ostr = null;
        try {
            istr = new FileInputStream(fname);
            //response.setContentType("application/binary");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file + "\";");
            int curByte = -1;

            while ((curByte = istr.read()) != -1)
                out.write(curByte);


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (istr != null) istr.close();
            } catch (Exception ex) {
                System.out.println("" + ex);
            }
        }
        try {
            //out.clear();
            out = pageContext.pushBody();
            response.flushBuffer();
        } catch (Exception ex) {
            System.out.println("Error flushing the Response: " + ex.toString());
        }
    }
%>
