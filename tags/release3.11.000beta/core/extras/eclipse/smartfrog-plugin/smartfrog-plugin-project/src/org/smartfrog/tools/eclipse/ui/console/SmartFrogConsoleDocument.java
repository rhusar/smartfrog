
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


package org.smartfrog.tools.eclipse.ui.console;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import org.smartfrog.tools.eclipse.model.ExceptionHandler;

import java.util.ArrayList;


/**
 * Store the SmartFrog messages
 */
public class SmartFrogConsoleDocument
{
    public final static int MAX_DOC_SIZE = 50000;

    private static Color ERROR_COLOR;
    private static Color DEFAULT_COLOR;
    static Font MSG_FONT;
    public static final int MSG_ERR = 0;
    public static final int MSG_DEFAULT = 1;
    private static final SmartFrogConsoleDocument instance =
        new SmartFrogConsoleDocument();
    private ArrayList mViews = new ArrayList();
    private Document mDocument;
    private ArrayList mStyleRanges;

    /**
     * Singleton
     */
    public static SmartFrogConsoleDocument getInstance()
    {
        return instance;
    }

    /**
     * Constructor
     */
    private SmartFrogConsoleDocument()
    {
        mDocument = new Document();
        mStyleRanges = new ArrayList(5);
    }

    /**
     * Append the msg to the console
     * @param message
     * @param priority
     * @param clear the output or not, if the size exceed MAX_DOC_SIZE
     */
    public void append(final String message, final int priority,
        final boolean clear)
    {
        if (mViews.size() == 0) {
            return;
        }

        ( (ConsoleView)mViews.get(0) ).getViewSite().getShell().getDisplay()
         .syncExec(new Runnable() {
                public void run()
                {
                    int start = getDocument().getLength();

                    try {
                        if (( start >= MAX_DOC_SIZE ) && clear) {
                            clearOutput();
                            getDocument().set(message);
                            start = 0;
                        } else {
                            getDocument().replace(start, 0, message);
                        }
                    } catch (BadLocationException e) {
                        ExceptionHandler.handle(e, Messages.getString("SmartFrogConsoleDocument.title.Error"), //$NON-NLS-1$
                            Messages.getString("SmartFrogConsoleDocument.description.badLocaiton")); //$NON-NLS-1$
                    }

                    setOutputLevelColor(priority, start, message.length());
                }
            });

        for (int i = 0; i < mViews.size(); i++) {
            ( (ConsoleView)mViews.get(i) ).append(message, priority);
        }
    }

    public void append(final String message, final int priority)
    {
        append(message, priority, true);
    }

    /**
     * Clear the console
     */
    public void clearOutput()
    {
        mDocument.set(""); //$NON-NLS-1$
        mStyleRanges.clear();
    }

    private void addRangeStyle(int start, int length, Color color)
    {
        // Don't add a StyleRange if the length is 0.
        if (length == 0) {
            return;
        }

        if (mStyleRanges.size() != 0) {
            StyleRange lastStyle = (StyleRange)mStyleRanges.get(
                    mStyleRanges.size() - 1);

            if (color.equals(lastStyle.foreground)) {
                lastStyle.length += length;
            } else {
                mStyleRanges.add(new StyleRange(start, length, color, null));
            }
        } else {
            mStyleRanges.add(new StyleRange(start, length, color, null));
        }

        StyleRange[] styleArray = (StyleRange[])mStyleRanges.toArray(
                new StyleRange[ mStyleRanges.size() ]);

        for (int i = 0; i < mViews.size(); i++) {
            TextViewer tv = ( (ConsoleView)mViews.get(i) ).getTextViewer();

            if (tv != null) {
                tv.getTextWidget().setStyleRanges(styleArray);
            }
        }
    }

    /**
     * Returns the color used for error messages on the log console.
     */
    private static Color getErrorColor()
    {
        if (( ERROR_COLOR == null ) || ERROR_COLOR.isDisposed()) {
            ERROR_COLOR = new Color(null, 200, 0, 0);
        }

        return ERROR_COLOR;
    }

    /**
     * Returns the default color
     */
    private static Color getDefaultColor()
    {
        if (( DEFAULT_COLOR == null ) || DEFAULT_COLOR.isDisposed()) {
            DEFAULT_COLOR = new Color(null, 0, 0, 160);
        }

        return DEFAULT_COLOR;
    }

    Document getDocument()
    {
        return mDocument;
    }

    ArrayList getStyleRanges()
    {
        return mStyleRanges;
    }

    private boolean hasViews()
    {
        return ( mViews.size() > 0 );
    }

    /**
     * register thew View.
     * @param view
     */
    public void registerView(ConsoleView view)
    {
        if (!hasViews()) {
            MSG_FONT = new Font(null, "MS Sans Serif", 8, 1); //$NON-NLS-1$
        }

        mViews.add(view);
    }

    public void unregisterView(ConsoleView view)
    {
        mViews.remove(view);

        if (!hasViews()) {
            MSG_FONT.dispose();
        }
    }

    void setOutputLevelColor(int level, int start, int end)
    {
        switch (level) {
        case SmartFrogConsoleDocument.MSG_ERR:
            addRangeStyle(start, end, getErrorColor());

            break;

        case SmartFrogConsoleDocument.MSG_DEFAULT:
            addRangeStyle(start, end, getDefaultColor());

            break;

        default:
            addRangeStyle(start, end, getDefaultColor());
        }
    }

    /**
     * Replaces the old color with the new one in all style ranges,
     */
    private void updateStyleRanges(Color oldColor, Color newColor)
    {
        for (int i = 0; i < mStyleRanges.size(); i++) {
            StyleRange range = (StyleRange)mStyleRanges.get(i);

            if (range.foreground == oldColor) {
                range.foreground = newColor;
            }
        }
    }
}
