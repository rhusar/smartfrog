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
package org.smartfrog.extras.wrapper;

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

/**
 * Hosting of SmartFrog under Java Service Wrapper.
 *
 * @see "http://wrapper.tanukisoftware.org/doc/english/introduction.html" Date:
 *      01-Oct-2004
 */
public class ServiceWrapper implements WrapperListener {


    private ServiceWrapper() {
    }

    private WrappedSFSystem system;

    /**
     * The start method is called when the WrapperManager is signalled by the
     * native wrapper code that it can start its application.  This method call
     * is expected to return, so a new thread should be launched if necessary.
     * <p/>
     * If this method throws an exception the Wrapper will shutdown the current
     * JVM in an error state and then relaunch a new JVM.  It is the
     * responsibility of the user code to catch any exceptions and return an
     * appropriate exit code if the exception should result in the Wrapper
     * stopping.
     *
     * @param args List of arguments used to initialize the application.
     * @return Any error code if the application should exit on completion of
     *         the start method.  If there were no problems then this method
     *         should return null.
     */
    public Integer start(String[] args) {
        //create a new system
        system = new WrappedSFSystem(args);
        system.setSystemExitOnRootProcessTermination(true);
        system.run();
        return null;
    }

    /**
     * Called when the application is shutting down.  The Wrapper assumes that
     * this method will return fairly quickly.  If the shutdown code code could
     * potentially take a long time, then WrapperManager.stopping() should be
     * called to extend the timeout period.  If for some reason, the stop method
     * can not return, then it must call WrapperManager.stopped() to avoid
     * warning messages from the Wrapper.
     * <p/>
     * WARNING - Directly calling System.exit in this method will result in a
     * deadlock in cases where this method is called from within a shutdown
     * hook.  This method will be invoked by a shutdown hook if the JVM shutdown
     * was originally initiated by a call to System.exit.
     *
     * @param exitCode The suggested exit code that will be returned to the OS
     *                 when the JVM exits.
     * @return The exit code to actually return to the OS.  In most cases, this
     *         should just be the value of exitCode, however the user code has
     *         the option of changing the exit code if there are any problems
     *         during shutdown.
     */
    public int stop(int exitCode) {
        WrapperManager.signalStopping(system.getExpectedShutdownTime());
        system.stop();
        system.waitTillStopped(0);
        return system.getExitCode();
    }

    /**
     * Called whenever the native wrapper code traps a system control signal
     * against the Java process.  It is up to the callback to take any actions
     * necessary.  Possible values are: WrapperManager.WRAPPER_CTRL_C_EVENT,
     * WRAPPER_CTRL_CLOSE_EVENT, WRAPPER_CTRL_LOGOFF_EVENT, or
     * WRAPPER_CTRL_SHUTDOWN_EVENT.
     * <p/>
     * The WRAPPER_CTRL_C_EVENT will be called whether or not the JVM is
     * controlled by the Wrapper.  If controlled by the Wrapper, it is
     * undetermined as to whether the Wrapper or the JVM will receive this
     * signal first, but the Wrapper will always initiate a shutdown.  In most
     * cases, the implementation of this method should call
     * WrapperManager.stop() to initiate a shutdown from within the JVM. The
     * WrapperManager will always handle the shutdown correctly whether shutdown
     * is initiated from the Wrapper, within the JVM or both. By calling stop
     * here, it will ensure that the application will behave correctly when run
     * standalone, without the Wrapper.
     * <p/>
     * WRAPPER_CTRL_CLOSE_EVENT, WRAPPER_CTRL_LOGOFF_EVENT, and
     * WRAPPER_CTRL_SHUTDOWN_EVENT events will only be encountered on Windows
     * systems.  Like the WRAPPER_CTRL_C_EVENT event, it is undetermined as to
     * whether the Wrapper or JVM will receive the signal first.  All signals
     * will be triggered by the OS whether the JVM is being run as an NT service
     * or as a console application.  If the JVM is running as a console
     * application, the Application must respond to the CLOSE and LOGOFF events
     * by calling WrapperManager.stop() in a timely manner. In these cases,
     * Windows will wait for the JVM process to exit before moving on to signal
     * the next process.  If the JVM process does not exit within a reasonable
     * amount of time, Windows will pop up a message box for the user asking if
     * they wish to wait for the process or exit or forcibly close it.  The JVM
     * must call stop() in response to the SHUTDOWN method whether running as a
     * console or NT service.  Usually, the LOGOFF event should be ignored when
     * the Wrapper is running as an NT service.
     *
     * @param event The system control signal.
     * @see WrapperManager#controlEvent(int)
     */
    public void controlEvent(int event) {
        if ((event == WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT)
                && WrapperManager.isLaunchedAsService()) {
            // Ignore
        } else {
            WrapperManager.stop(0);
        }
    }

    /**
     * public entry point for both testing and the service
     *
     * @param args
     */
    public static void main(String[] args) {
        // Start the application.  If the JVM was launched from the native
        //  Wrapper then the application will wait for the native Wrapper to
        //  call the application's start method.  Otherwise the start method
        //  will be called immediately.
        WrapperManager.start(new ServiceWrapper(), args);
    }

}
