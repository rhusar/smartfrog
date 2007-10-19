/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

*/



/**
 * Title:        GUI Utilities
 * Description:  Includes some gui common basic facilities to speed up GUI
 */
// copied from package com.hp.serrano.utils.gui;
package org.smartfrog.tools.gui.browser;

import java.io.OutputStream;
import javax.swing.JTextArea;

/**
 * Output to Text Area
 */
public class TextAreaOutputStream extends OutputStream
{
  private static TextAreaOutputStream staticRef=null;

  /**
   * Set a single instance accesible via static methods
   */
  public static void setStaticOutput(Object jta)
  {
    staticRef=new TextAreaOutputStream((JTextArea) jta);
  }

  /**
   * Get a single instance accesible via static methods
   */
  public static TextAreaOutputStream getStaticOutput()
  {
    return staticRef;
  }

  /**
   * TextArea output destination
   */
  private JTextArea jta;

  /**
   * Constructor
   */
  public TextAreaOutputStream(Object jta)
  {
    this.jta=(JTextArea)jta;
  }

  /**
   * Method to write a byte subarray to the text area as a stream
   */
  public void write(byte[] buf, int off, int len)
  {
    jta.append(new String(buf,off,len));
  }

  /**
   * Method to write a byte array to the text area as a stream
   */
  public void write(byte b[])
  {
    jta.append(new String(b));
  }

  /**
   * Method to write a byte to the text area as a stream
   */
  public void write(int b)
  {
    byte ba[]=new byte[1];
    ba[0]=(byte)b;
    jta.append(new String(ba));
  }
}
