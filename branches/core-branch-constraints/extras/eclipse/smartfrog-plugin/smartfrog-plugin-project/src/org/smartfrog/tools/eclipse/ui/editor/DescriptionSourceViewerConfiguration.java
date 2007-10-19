
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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import org.eclipse.swt.graphics.RGB;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.ui.editor.sf2.DescriptionColorProvider;
import org.smartfrog.tools.eclipse.ui.editor.sf2.DescriptionCompletionProcessor;
import org.smartfrog.tools.eclipse.ui.editor.sf2.DescriptionScanner;


/**
 * You can configure the description behavior in this class, partioner, code assistance ...
 */
public class DescriptionSourceViewerConfiguration
    extends SourceViewerConfiguration
{
    private static final int DELAY_TIME = 500;
	/**
     * Single token scanner.
     */
    static class SingleTokenScanner
        extends BufferedRuleBasedScanner
    {
        public SingleTokenScanner(TextAttribute attribute)
        {
            setDefaultReturnToken(new Token(attribute));
        }
    }

    /*
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
     */
    public String getConfiguredDocumentPartitioning(
        ISourceViewer sourceViewer)
    {
        return SmartFrogPlugin.SMARTFROG_PARTITIONING;
    }

    /* (non-Javadoc)
     * Method declared on SourceViewerConfiguration
     */
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
    {
        return new String[] {
                IDocument.DEFAULT_CONTENT_TYPE,
                DescriptionPartitionScanner.SMARTFROG_MULTILINE_COMMENT
            };
    }

    /* (non-Javadoc)
     * Method declared on SourceViewerConfiguration
     */
    public String getDefaultPrefix(ISourceViewer sourceViewer,
        String contentType)
    {
        return ( IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)
                ? "//" //$NON-NLS-1$
                : null ); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * Method declared on SourceViewerConfiguration
     */
    public String[] getIndentPrefixes(ISourceViewer sourceViewer,
        String contentType)
    {
        return new String[] { "\t", "    " }; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /* (non-Javadoc)
     * Method declared on SourceViewerConfiguration
     */
    public IPresentationReconciler getPresentationReconciler(
        ISourceViewer sourceViewer)
    {
        DescriptionColorProvider provider = DescriptionColorProvider
            .getInstance();
        PresentationReconciler reconciler = new PresentationReconciler();
        reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(
                sourceViewer));

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
                DescriptionScanner.getInstance());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        dr = new DefaultDamagerRepairer(new SingleTokenScanner(
                    new TextAttribute(
                        provider.getColor(
                            DescriptionColorProvider.MULTI_LINE_COMMENT))));
        reconciler.setDamager(dr,
            DescriptionPartitionScanner.SMARTFROG_MULTILINE_COMMENT);
        reconciler.setRepairer(dr,
            DescriptionPartitionScanner.SMARTFROG_MULTILINE_COMMENT);

        dr = new DefaultDamagerRepairer(new SingleTokenScanner(
                    new TextAttribute(
                        provider.getColor(DescriptionColorProvider.STRING))));
        reconciler.setDamager(dr,
            DescriptionPartitionScanner.SMARTFROG_MULTILINE_STRING);
        reconciler.setRepairer(dr,
            DescriptionPartitionScanner.SMARTFROG_MULTILINE_STRING);

        return reconciler;
    }

    /* (non-Javadoc)
     * Method declared on SourceViewerConfiguration
     */
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer)
    {
        ContentAssistant assistant = new ContentAssistant();
        assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(
                sourceViewer));
        assistant.setContentAssistProcessor(
            new DescriptionCompletionProcessor(),
            IDocument.DEFAULT_CONTENT_TYPE);

        assistant.enableAutoActivation(true);
        assistant.setAutoActivationDelay(DELAY_TIME);
        assistant.setProposalPopupOrientation(
            IContentAssistant.PROPOSAL_OVERLAY);
        assistant.setContextInformationPopupOrientation(
            IContentAssistant.CONTEXT_INFO_ABOVE);
        assistant.setContextInformationPopupBackground(DescriptionColorProvider
            .getInstance().getColor(new RGB(150, 150, 0)));

        return assistant;
    }
}
