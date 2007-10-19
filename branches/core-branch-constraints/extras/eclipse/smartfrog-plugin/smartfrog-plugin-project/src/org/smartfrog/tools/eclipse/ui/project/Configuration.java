
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


package org.smartfrog.tools.eclipse.ui.project;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import org.eclipse.jdt.core.IClasspathEntry;

import org.osgi.framework.Bundle;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.core.SDKEnvironment;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.ui.preference.SmartFrogPreferencePage;

import java.net.URL;
import java.io.IOException;

/**
 * Configure SmartFrog project properties: libs
 */
public class Configuration
    implements SDKEnvironment
{
    private IJavaCoreWrapper fJavaCoreWrapper = null;
    private static final int LIB_NUM = 1;



    /**
     * Constructor for Configuration.
     */
    public Configuration(IJavaCoreWrapper javaCoreWrapper)
    {
        super();

        fJavaCoreWrapper = javaCoreWrapper;
    }

    /**
     * Constructor for Configuration.
     */
    private Configuration()
    {
        super();
    }

    /**
     * Returns an array of classpaths to libraries.
     */
  /*  public IClasspathEntry[] getClasspath()
    {
        IClasspathEntry[] cpe = null;
        Bundle bundle = SmartFrogPlugin.getDefault().getBundle();
        URL installURL = bundle.getEntry("/"); //$NON-NLS-1$
        URL localURL = null;

        cpe = new IClasspathEntry[ ISmartFrogConstants.SMARTFROG_LIBS.length ];

        for (int i = 0; i < ISmartFrogConstants.SMARTFROG_LIBS.length; i++) {

            String libPathStr = SmartFrogPreferencePage.getSmartFrogLocation() +
            ISmartFrogConstants.SMARTFROG_LIBS[ i ];
            
            Path libPath = new Path(libPathStr);
            String sourcePathStr = SmartFrogPreferencePage.getSmartFrogLocation() + "/src.zip"; //$NON-NLS-1$
            Path sourcePath = new Path(sourcePathStr);
            cpe[ i ] = fJavaCoreWrapper.newLibraryEntry(libPath, sourcePath, null);
        }

        return cpe;
    }   */

     public IClasspathEntry[] getClasspath()
    {
        IClasspathEntry[] cpe = null;
        Bundle bundle = SmartFrogPlugin.getDefault().getBundle();
        URL installURL = bundle.getEntry("/"); //$NON-NLS-1$
        URL localURL = null;

        cpe = new IClasspathEntry[ SmartFrogPlugin.getSmartFrogLib().length ];

      /*
         StringBuffer sb = new StringBuffer( getAbsoluteInstallPath(  ) );
       sb.append( "SmartFrog");
       //  sb.append( "/" );


        for (int i = 0; i < SmartFrogPlugin.getSmartFrogLib().length; i++) {

            String libPathStr = sb.toString() + SmartFrogPlugin.getSmartFrogLib()[ i ];

            Path libPath = new Path(libPathStr);
            String sourcePathStr = SmartFrogPreferencePage.getSmartFrogLocation() + "/src.zip"; //$NON-NLS-1$
            Path sourcePath = new Path(sourcePathStr);
            cpe[ i ] = fJavaCoreWrapper.newLibraryEntry(libPath, sourcePath, null);
        }  */
        cpe[ 0 ] = fJavaCoreWrapper.newLibraryEntry(null, null, null);
        return cpe;
    }

    /**
     * Returns home directory for libraries
     */
    public String getHomeDirectory()
    {
        return ""; //$NON-NLS-1$
    }

   /* This method returns the Plugin Absolute Install path.
      *
      * @return
      */
     public static String getAbsoluteInstallPath(  )
     {
         String installDir = "";

         try {
             installDir = Platform.asLocalURL( getInstallURL(  ) ).getFile(  );

             if ( ( installDir.charAt( 0 ) == '/' ) && isWindows()) {
                 installDir = installDir.replaceFirst( "/", "" );
             }
         } catch ( Exception e ) {
             e.printStackTrace(  );
         }

         return installDir;
     }

     /**
      * Method to get the URL of the installed plugin
      *
      * @return
      */
     private static URL getInstallURL(  )
     {


         try {
             return SmartFrogPlugin.getDefault(  ).getBundle(  ).getEntry( "/" );
         } catch ( Exception  e ) {
            e.printStackTrace(  );
         }
         return null;

       }
    public static boolean isWindows(  )
    {
        String platform = System.getProperty( "os.name" );

        return ( platform.toLowerCase(  ).indexOf( "windows" ) >= 0 );
    }


}
