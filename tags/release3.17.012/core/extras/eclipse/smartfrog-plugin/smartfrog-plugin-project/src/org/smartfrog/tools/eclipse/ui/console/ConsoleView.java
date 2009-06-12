
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.TextViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import org.osgi.framework.Bundle;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.ExceptionHandler;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;


/**
 * Create the SmartFrog console view to show the output
 */
public class ConsoleView
    extends ViewPart
{
    private TextViewer mTextViewer;

    private Action mClearOutputAction = null;

    private Action mCopyAction;

    private Action mSelectAllAction;

    /**
     * Constructor
     */
    public ConsoleView()
    {
        super();
        SmartFrogConsoleDocument.getInstance().registerView(this);
    }

    /**
     * @see IViewPart
     */
    public void init(IViewSite site, IMemento memento)
        throws PartInitException
    {
        super.init(site, memento);
    }

    /**
     * Append the message to the end
     * @param value
     * @param ouputLevel
     */
    public void append(final String value, final int ouputLevel)
    {
        getViewSite().getShell().getDisplay().syncExec(new Runnable() {
                public void run()
                {
                    if (( value.length() > 0 ) && ( mTextViewer != null )) {
                        revealEndOfDocument();
                        mTextViewer.getTextWidget().setStyleRanges(
                            (StyleRange[])getStyleRanges().toArray(
                                new StyleRange[ getStyleRanges().size() ]));
                    }
                }
            });
    }

    /**
     * Create the console's GUI
     */
    public void createPartControl(Composite parent)
    {
        mTextViewer = new TextViewer(parent,
                SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);

        GridData viewerData = new GridData(GridData.FILL_BOTH);
        mTextViewer.getControl().setLayoutData(viewerData);
        mTextViewer.setEditable(false);
        mTextViewer.setDocument(getDocument());
        mTextViewer.getTextWidget().setStyleRanges((StyleRange[])
            getStyleRanges().toArray(
                new StyleRange[ getStyleRanges().size() ]));
        createActions();

        IActionBars actionBars = getViewSite().getActionBars();
        actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
            mCopyAction);
        getViewSite().getActionBars().setGlobalActionHandler(
            ActionFactory.SELECT_ALL.getId(), mSelectAllAction);

        IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
        tbm.add(mClearOutputAction);
        tbm.add(mCopyAction);

        getViewSite().getActionBars().updateActionBars();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose()
    {
        SmartFrogConsoleDocument.getInstance().unregisterView(this);
        super.dispose();
    }

    private void setOutputLevelColor(int level, int start, int end)
    {
        SmartFrogConsoleDocument.getInstance().setOutputLevelColor(level, start,
            end);
    }

    protected void copySelectionToClipboard()
    {
        mTextViewer.doOperation(TextViewer.COPY);
    }

    private void revealEndOfDocument()
    {
        IDocument doc = getDocument();
        int docLength = doc.getLength();

        if (docLength > 0) {
            StyledText widget = mTextViewer.getTextWidget();
            widget.setCaretOffset(docLength);
            widget.showSelection();
        }
    }

    public Object getAdapter(Class required)
    {
        if (IFindReplaceTarget.class.equals(required)) {
            return mTextViewer.getFindReplaceTarget();
        }

        return super.getAdapter(required);
    }

    /**
     * @return
     */
    private Document getDocument()
    {
        return SmartFrogConsoleDocument.getInstance().getDocument();
    }

    private ArrayList getStyleRanges()
    {
        return SmartFrogConsoleDocument.getInstance().getStyleRanges();
    }

    public TextViewer getTextViewer()
    {
        return mTextViewer;
    }

    protected ImageDescriptor getImageDescriptor(String relativePath)
    {
        try {
            Bundle bundle = SmartFrogPlugin.getDefault().getBundle();
            URL installURL = bundle.getEntry("/"); //$NON-NLS-1$
            URL url = new URL(installURL, relativePath);

            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException mue) {
            // Should not happen.
            ExceptionHandler.handle(mue,
                
            Messages.getString("ConsoleView.title.Error"), //$NON-NLS-1$
                
            "Incorrect image url provided."); //$NON-NLS-1$ //$NON-NLS-2$

            return null;
        }
    }

    protected void selectAllText()
    {
        mTextViewer.doOperation(TextViewer.SELECT_ALL);
    }

    /**
     * @see WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
    }

    /**
     * Create console if haven't created, then move the console to front.
     * @param window
     * @return boolean
     */
    public static boolean activeConsole(IWorkbenchWindow window,
        String viewID)
    {
        try {
            IWorkbenchPage page = window.getActivePage();
            IViewPart view = page.findView(viewID);

            if (null == view) {
                page.showView(viewID);
                view = page.findView(viewID);
            }

            page.bringToTop(view);
        } catch (PartInitException e) {
            ExceptionHandler.handle(e, window.getShell(),
                Messages.getString("ConsoleView.title.Error2"), //$NON-NLS-1$
                Messages.getString("ConsoleView.error.noView")); //$NON-NLS-1$

            return false;
        }

        return true;
    }

    protected void createActions()
    {
        mClearOutputAction = new Action(Messages.getString(
                    "ConsoleView.button.ClearOuput")) { // $NON-NLS-1$ //$NON-NLS-1$
                public void run()
                {
                    SmartFrogConsoleDocument.getInstance().clearOutput();
                }
            };

        mClearOutputAction.setToolTipText(Messages.getString(
                "ConsoleView.toolTip.ClearOutput")); //$NON-NLS-1$
        mClearOutputAction.setImageDescriptor(getImageDescriptor(
                "icons/clearConsole.gif")); //$NON-NLS-1$
        mCopyAction = new Action("Copy", getImageDescriptor("icons/Copy.gif")) { // $NON-NLS-1$
                public void run()
                {
                    copySelectionToClipboard();
                }
            };
        mCopyAction.setToolTipText("Copy");
        mSelectAllAction = new Action("Select All") { // $NON-NLS-1$
                public void run()
                {
                    selectAllText();
                }
            };
    }
}
