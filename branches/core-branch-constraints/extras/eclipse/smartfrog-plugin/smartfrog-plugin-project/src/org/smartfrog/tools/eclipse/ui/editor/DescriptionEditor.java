
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


package org.smartfrog.tools.eclipse.ui.editor;

import org.smartfrog.tools.eclipse.model.IHelpContextIds;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;


/**
 * SmartFrog description editor.
 */
public class DescriptionEditor
    extends TextEditor
{
    private ProjectionSupport mProjectionSupport;

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class required)
    {
        if (mProjectionSupport != null) {
            Object adapter = mProjectionSupport.getAdapter(getSourceViewer(),
                    required);

            if (adapter != null) {
                return adapter;
            }
        }

        return super.getAdapter(required);
    }

    protected void initializeEditor()
    {
        super.initializeEditor();
        setSourceViewerConfiguration(
            new DescriptionSourceViewerConfiguration());
        setHelpContextId(IHelpContextIds.DESCRIPTION_EDITOR_HELP_ID);
    }

    /*
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    protected ISourceViewer createSourceViewer(Composite parent,
        IVerticalRuler ruler, int styles)
    {
        fAnnotationAccess = createAnnotationAccess();
        fOverviewRuler = createOverviewRuler(getSharedColors());

        ISourceViewer viewer = new ProjectionViewer(parent, ruler,
                getOverviewRuler(), isOverviewRulerVisible(), styles);

        getSourceViewerDecorationSupport(viewer);

        return viewer;
    }

    /*
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent)
    {
        super.createPartControl(parent);

        ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
        mProjectionSupport = new ProjectionSupport(viewer,
                getAnnotationAccess(), getSharedColors());
        mProjectionSupport.addSummarizableAnnotationType(DescriptionMessages
            .getString("DescriptionEditor.ErrorType")); //$NON-NLS-1$
        mProjectionSupport.addSummarizableAnnotationType(DescriptionMessages
            .getString("DescriptionEditor.WarningType")); //$NON-NLS-1$
        mProjectionSupport.install();
        viewer.doOperation(ProjectionViewer.TOGGLE);
    }

    /*
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#adjustHighlightRange(int, int)
     */
    protected void adjustHighlightRange(int offset, int length)
    {
        ISourceViewer viewer = getSourceViewer();

        if (viewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension = (ITextViewerExtension5)viewer;
            extension.exposeModelRange(new Region(offset, length));
        }
    }

    /** The <code>SmartFrogEditor</code> implementation of this
     * <code>AbstractTextEditor</code> method extend the
     * actions to add those specific to the receiver
     */
    protected void createActions()
    {
        super.createActions();

        IAction action = new TextOperationAction(DescriptionMessages
                .getResourceBundle(), "DescriptionEditor.ContentAssist.", this, //$NON-NLS-1$
                ISourceViewer.CONTENTASSIST_PROPOSALS); //$NON-NLS-1$
        action.setActionDefinitionId(
            ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("SmartFrog.ContentAssistProposalID", action); //$NON-NLS-1$

        action = new DefineFoldingRegionAction(DescriptionMessages
                .getResourceBundle(), "DescriptionEditor.DefineFoldingRegion.", //$NON-NLS-1$
                this); //$NON-NLS-1$
        setAction("SmartFrog.DefineFoldingRegionID", action); //$NON-NLS-1$
    }

    /*
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    protected void editorContextMenuAboutToShow(IMenuManager menu)
    {
        super.editorContextMenuAboutToShow(menu);
        addAction(menu, "SmartFrog.ContentAssistProposalID"); //$NON-NLS-1$
        addAction(menu, "SmartFrog.DefineFoldingRegionID"); //$NON-NLS-1$
    }
}
