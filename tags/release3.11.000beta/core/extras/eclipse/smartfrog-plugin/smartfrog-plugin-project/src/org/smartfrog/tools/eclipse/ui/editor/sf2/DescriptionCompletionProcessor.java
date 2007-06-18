
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


package org.smartfrog.tools.eclipse.ui.editor.sf2;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.smartfrog.tools.eclipse.model.ExceptionHandler;


/**
 * code completion processor.
 */
public class DescriptionCompletionProcessor
    implements IContentAssistProcessor
{
    protected final static String[] PROPOSE_RESERVED_WORDS = {
            
            //mLitereal2
            "true", "false", //$NON-NLS-1$ //$NON-NLS-2$
            // mSf
            "ROOT", "PARENT", "ATTRIB", "HOST", "PROPERTY", "NULL", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            //mKeywords1
            "extends", "LAZY", //$NON-NLS-1$ //$NON-NLS-2$
            //mKeywords2
            "#include", //$NON-NLS-1$
            //mKeywords3
            "Prim", "Compound", //$NON-NLS-1$ //$NON-NLS-2$
            //mLabels
            "sfConfig", "sfHost", "sfExport", "sfClass", "sfClassBase", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "sfDeployerClass", "sfProcessName", "sfProcessHost", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "sfProcessCompound", "sfProcessComponentName", "sfLiveness", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "sfLivenessFactor" //$NON-NLS-1$
        };


    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
        int documentOffset)
    {
        LinkedList words = new LinkedList(Arrays.asList(
                    PROPOSE_RESERVED_WORDS));

        Collections.sort(words);

        String prefix = getPrefix(viewer, documentOffset);

        for (Iterator it = words.iterator(); it.hasNext();) {
            String s = (String)it.next();

            if (!s.startsWith(prefix)) {
                it.remove();
            }
        }

        ICompletionProposal[] result = new ICompletionProposal[ words.size() ];

        for (int i = 0; i < words.size(); i++) {
            IContextInformation info = new ContextInformation((String)words.get(
                        i), (String)words.get(i));
            result[ i ] = new CompletionProposal(( (String)words.get(i) )
                    .substring(prefix.length()), documentOffset, 0,
                    ( (String)words.get(i) ).length(), null,
                    (String)words.get(i), info, null);
        }

        return result;
    }

    /**
     * Return the word between the previous white space and current location.
     * @param viewer
     * @param documentOffset
     * @return
     */
    private String getPrefix(ITextViewer viewer, int documentOffset)
    {
        StringBuffer prefix = new StringBuffer(""); //$NON-NLS-1$

        try {
            if (documentOffset-- > 0) {
                // get first char
                char prefixChar = viewer.getDocument().getChar(documentOffset);

                if (!Character.isWhitespace(prefixChar)) {
                    prefix.append(prefixChar);
                }

                // get the rest of char
                while (( documentOffset-- > 0 ) &&
                        ( !Character.isWhitespace(prefixChar) )) {
                    prefixChar = viewer.getDocument().getChar(documentOffset);

                    if (!Character.isWhitespace(prefixChar)) {
                        prefix.insert(0, prefixChar);
                    } else {
                        break;
                    }
                }
            }
        } catch (BadLocationException e) {
            ExceptionHandler.log(e);
        }

        return new String(prefix);
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public IContextInformation[] computeContextInformation(ITextViewer viewer,
        int documentOffset)
    {
        return null;
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return null;
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public char[] getContextInformationAutoActivationCharacters()
    {
        return null; 
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }

    /* (non-Javadoc)
     * Method declared on IContentAssistProcessor
     */
    public String getErrorMessage()
    {
        return null;
    }
}
