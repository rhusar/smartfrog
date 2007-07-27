/** (C) Copyright 2005-2006 Hewlett-Packard Development Company, LP

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

/*
 * Copyright  2000-2002,2004-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.smartfrog.services.fun.sound;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ShouldDetachOrTerminate;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingComponentImpl;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import java.rmi.RemoteException;
import java.io.File;
import java.io.IOException;

/**
 * Sound player based on code extracted from Ant's <sound> task.
 */

public class SoundPlayerImpl extends FileUsingComponentImpl
        implements SoundPlayer {

    private Log log;

    public static final int SLEEP_INTERVAL_MILLIS = 100;

    private SoundPlayerListener player = null;
    private boolean shouldTerminate;

    public SoundPlayerImpl() throws RemoteException {
    }

    public Log getLog() {
        return log;
    }

    /**
     * Can be called to start components.
     * Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        log = sfGetApplicationLog();
        bindWithDir(true, null);
        int loops = sfResolve(ATTR_LOOPS, 1, false);
        shouldTerminate = sfResolve(
                ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE,
                shouldTerminate,
                false);
        player = new SoundPlayerListener(this, getFile(), loops);
        player.start();
    }


    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link
     *                                  org.smartfrog.sfcore.prim.Liveness}
     *                                  interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (player != null) {
            synchronized (player) {
                if (player.getPlaybackException() != null) {
                    throw (SmartFrogLivenessException) SmartFrogLivenessException
                            .forward(player.getPlaybackException());
                }
                //clean up the player if we are finished.
                if (player.isFinished()) {
                    player = null;
                }
            }
        }
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (player != null) {
            synchronized (player) {
                player.kill();
                player = null;
            }
        }
    }


    /**
     * Plays the file for duration milliseconds or loops.
     * <p/>
     * Taken from org.apache.tools.ant.taskdefs.optional.sound.AntSoundPlayer;
     * ASF 2.0 license
     */


    private static class SoundPlayerListener implements Runnable, LineListener {
        private SoundPlayerImpl owner;
        private File file;
        private int loops;
        private Log log;
        private Throwable playbackException;
        private boolean finished;
        public static final String ERROR_DURING_PLAYBACK = "during playback";
        public static final String ERROR_NO_SOUNDCARD =
                "The sound device is currently unavailable";
        public static final String ERROR_NO_DATA =
                "Can't get audio data from file ";
        public static final String ERROR_UNKNOWN_FORMAT =
                "Audio format is not yet supported: ";
        public static final String ERROR_PLAYBACK_FAILURE =
                "Failed to play file";
        private Thread thread;


        private SoundPlayerListener(SoundPlayerImpl owner,
                                   File file,
                                   int loops) {
            this.owner = owner;
            this.file = file;
            this.loops = loops;
        }

        public void start() {
            thread = new Thread(this);
            thread.run();
        }

        public Throwable getPlaybackException() {
            return playbackException;
        }

        public boolean isFinished() {
            return finished;
        }

        public void kill() {
            if (thread == null) {
                return;
            } else {
                thread.interrupt();
            }
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p/>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        public void run() {
            log = owner.getLog();
            log.info("Playing " + file);
            play(file, loops);
            //forget about ourselves
            finished = true;
            thread = null;
            ComponentHelper componentHelper = new ComponentHelper(owner);
            componentHelper.sfSelfDetachAndOrTerminate(null,
                    "Playback",
                    null,
                    playbackException);
        }


        /**
         * This is implemented to listen for any line events and closes the clip
         * if required. Runs in the separate thread.
         *
         * @param event the line event to follow
         */
        public void update(LineEvent event) {
            if (event.getType().equals(LineEvent.Type.STOP)) {
                Line line = event.getLine();
                line.close();
            } else if (event.getType().equals(LineEvent.Type.CLOSE)) {
                //line closure
            }
        }

        /**
         * Play a clip. this is based on apache code
         *
         * @param file
         * @param loops
         * @return true if it started
         */
        private boolean play(File file, int loops) {

            Clip audioClip = null;

            AudioInputStream audioInputStream = null;

            try {
                audioInputStream = AudioSystem.getAudioInputStream(file);
                if (audioInputStream == null) {
                    String message = ERROR_NO_DATA + file.getName();
                    log.error(message);
                    playbackException = new SmartFrogException(message);
                    return false;
                }


                AudioFormat format = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format,
                        AudioSystem.NOT_SPECIFIED);
                audioClip = (Clip) AudioSystem.getLine(info);
                audioClip.addLineListener(this);
                audioClip.open(audioInputStream);
                boolean interrupted = false;

                audioClip.loop(loops);
                while (!interrupted && audioClip.isRunning()) {
                    try {
                        Thread.sleep(SLEEP_INTERVAL_MILLIS);
                    } catch (InterruptedException interrupt) {
                        //we have been interrupted
                        // Ignore Exception
                        audioClip.stop();
                        interrupted = true;
                    }
                }
                if (!interrupted) {
                    audioClip.drain();
                }
            } catch (LineUnavailableException e) {
                playbackException = e;
                log.error(ERROR_NO_SOUNDCARD);
                return false;
            } catch (UnsupportedAudioFileException uafe) {
                log.error(ERROR_UNKNOWN_FORMAT
                        + uafe.getMessage());
                playbackException = uafe;
                return false;
            } catch (IOException ioe) {
                log.error(ERROR_PLAYBACK_FAILURE, ioe);
                playbackException = ioe;
                return false;
            } finally {
                if (audioClip != null) {
                    audioClip.close();
                }
                if (audioInputStream != null) {
                    try {
                        audioInputStream.close();
                    } catch (IOException ignored) {
                        log.error(ignored);
                    }
                }
            }
            return true;
        }

    }


}
