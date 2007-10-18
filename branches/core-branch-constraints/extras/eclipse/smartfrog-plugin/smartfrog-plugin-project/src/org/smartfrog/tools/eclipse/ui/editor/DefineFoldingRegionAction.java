
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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import java.util.ResourceBundle;

/**
 * Support source code folding
 */
class DefineFoldingRegionAction
    extends TextEditorAction
{
    public DefineFoldingRegionAction(ResourceBundle bundle, String prefix,
        ITextEditor editor)
    {
        super(bundle, prefix, editor);
    }

    private IAnnotationModel getAnnotationModel(ITextEditor editor)
    {
        return (IAnnotationModel)editor.getAdapter(
                ProjectionAnnotationModel.class);
    }

    /*
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        ITextEditor editor = getTextEditor();
        ISelection selection = editor.getSelectionProvider().getSelection();

        if (selection instanceof ITextSelection) {
            ITextSelection textSelection = (ITextSelection)selection;

            if (!textSelection.isEmpty()) {
                IAnnotationModel model = getAnnotationModel(editor);

                if (model != null) {
                    int start = textSelection.getStartLine();
                    int end = textSelection.getEndLine();

                    try {
                        IDocument document = editor.getDocumentProvider()
                                                   .getDocument(editor
                                .getEditorInput());
                        int offset = document.getLineOffset(start);
                        int endOffset = document.getLineOffset(end + 1);
                        Position position = new Position(offset,
                                endOffset - offset);
                        model.addAnnotation(new ProjectionAnnotation(),
                            position);
                    } catch (BadLocationException ignore) {
                    }
                }
            }
        }
    }
}
