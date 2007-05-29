/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP
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

package org.smartfrog.services.persistence.example;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import org.smartfrog.services.persistence.recoverablecomponent.RComponent;
import org.smartfrog.services.persistence.recoverablecomponent.RComponentImpl;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;


public class Monitor extends RComponentImpl implements RComponent {

    Frame frame = null;

    private class Killer implements ActionListener {
        RMIServerInterface server;

        public Killer( RMIServerInterface server ) {
            this.server = server;
        }

        public void actionPerformed( ActionEvent e ) {
            try {
                server.killMe();
            } catch ( Exception exc ) {}
        }
    }


    public Monitor() throws RemoteException {
        super();
    }

    public void sfDeployWith( Prim parent, Context cxt ) throws
            SmartFrogDeploymentException, RemoteException {

        super.sfDeployWith( parent, cxt );
    }

    private synchronized void execute() throws SmartFrogException,
            RemoteException {
        frame = new Frame( "Processes to kill" );

        // Add a listener for the close event
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent evt ) {
                // Exit the application
                System.exit( 0 );
            }
        } );

        Panel panel = new Panel( new FlowLayout( FlowLayout.CENTER ) );

        Button b = new Button( "Jill" );
        b.addActionListener( new Killer( ( RMIServerInterface ) sfResolve( "jill" ) ) );
        panel.add( b );
        b = new Button( "John" );
        b.addActionListener( new Killer( ( RMIServerInterface ) sfResolve( "john" ) ) );
        panel.add( b );
        b = new Button( "Paterson" );
        b.addActionListener( new Killer( ( RMIServerInterface ) sfResolve( "paterson" ) ) );
        panel.add( b );
        b = new Button( "Marie" );
        b.addActionListener( new Killer( ( RMIServerInterface ) sfResolve( "marie" ) ) );
        panel.add( b );

        frame.add( panel, BorderLayout.NORTH );

        frame.pack();
        frame.setVisible( true );
    }

    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        execute();
    }

    public synchronized void sfRecover() throws SmartFrogException,
            RemoteException {
        super.sfRecover();
        execute();
    }

    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
    }

    public synchronized void sfTerminateWith( TerminationRecord status ) {
        super.sfTerminateWith( status );
        frame.dispose();
    }


}
