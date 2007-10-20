
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.smartfrog.tools.eclipse.ui.editor.DescriptionWhitespaceDetector;


/**
 * A SmartFrog reserved word scanner for SmartFrog2 language
 */
public class DescriptionScanner
    extends RuleBasedScanner
{
    private static String[] mLitereal2 = { "true", "false" }; //$NON-NLS-1$ //$NON-NLS-2$
    private static String[] mSf = {
            "ROOT", "PARENT", "ATTRIB", "HOST", "PROPERTY", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "NULL" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        };

    private static String[] mKeywords1 = { "extends", "LAZY" }; //$NON-NLS-1$ //$NON-NLS-2$
    private static String[] mKeywords2 = { "#include" }; //$NON-NLS-1$
    private static String[] mKeywords3 = { "Prim", "Compound" }; //$NON-NLS-1$ //$NON-NLS-2$
    private static String[] mLabels = {
            "sfConfig", "sfExport", "sfClass", "sfClassBase", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "sfDeployerClass", "sfProcessName", "sfProcessHost", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "sfProcessCompound", "sfProcessComponentName", "sfLiveness", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "sfLivenessFactor" //$NON-NLS-1$
        };

    private static DescriptionScanner INSTANCE;

    public static DescriptionScanner getInstance()
    {
        if (null == INSTANCE) {
            INSTANCE = new DescriptionScanner(DescriptionColorProvider
                    .getInstance());
        }

        return INSTANCE;
    }

    /**
     * Creates a description code scanner
     */
    private DescriptionScanner(DescriptionColorProvider manager)
    {
        IToken defaultToken = new Token(new TextAttribute(
                    manager.getColor(DescriptionColorProvider.DEFAULT)));

        IToken keyword1 = new Token(new TextAttribute(
                    manager.getColor(
                        DescriptionColorProvider.KEYWORDS_1_COLOR), null,
                    SWT.BOLD));
        IToken keyword2 = new Token(new TextAttribute(
                    manager.getColor(
                        DescriptionColorProvider.KEYWORDS_2_COLOR)));
        IToken keyword3 = new Token(new TextAttribute(
                    manager.getColor(
                        DescriptionColorProvider.KEYWORDS_3_COLOR)));
        IToken litereal2 = new Token(new TextAttribute(
                    manager.getColor(
                        DescriptionColorProvider.LITERAL2_COLOR), null,
                    SWT.BOLD));
        IToken sf = new Token(new TextAttribute(
                    manager.getColor(DescriptionColorProvider.SF_COLOR), null,
                    SWT.BOLD));
        IToken labelsToken = new Token(new TextAttribute(
                    manager.getColor(DescriptionColorProvider.LABELS_COLOR),
                    null, SWT.BOLD));
        IToken string = new Token(new TextAttribute(
                    manager.getColor(DescriptionColorProvider.STRING)));
        IToken comment = new Token(new TextAttribute(
                    manager.getColor(
                        DescriptionColorProvider.SINGLE_LINE_COMMENT)));

        List rules = new ArrayList();

        // comments.
        rules.add(new EndOfLineRule("//", comment)); //$NON-NLS-1$

        // Constansts
        rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
        rules.add(new SingleLineRule("'", "'", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

        // whitespace rule.
        rules.add(new WhitespaceRule(new DescriptionWhitespaceDetector()));


        // SmartFrog keywords
        WordRule wordRule = new WordRule(new DescriptionWordDetector(),
                defaultToken);

        for (int i = 0; i < mKeywords1.length; i++) {
            wordRule.addWord(mKeywords1[ i ], keyword1);
        }

        for (int i = 0; i < mKeywords2.length; i++) {
            wordRule.addWord(mKeywords2[ i ], keyword2);
        }

        for (int i = 0; i < mKeywords3.length; i++) {
            wordRule.addWord(mKeywords3[ i ], keyword3);
        }

        for (int i = 0; i < mLitereal2.length; i++) {
            wordRule.addWord(mLitereal2[ i ], litereal2);
        }

        for (int i = 0; i < mSf.length; i++) {
            wordRule.addWord(mSf[ i ], sf);
        }

        for (int i = 0; i < mLabels.length; i++) {
            wordRule.addWord(mLabels[ i ], labelsToken);
        }

        rules.add(wordRule);

        IRule[] result = new IRule[ rules.size() ];
        rules.toArray(result);
        setRules(result);
    }
    
    
    /**
     * A SmartFrog word detector.
     */
    class DescriptionWordDetector
        implements IWordDetector
    {
        /* (non-Javadoc)
         * Method declared on IWordDetector.
         */
        public boolean isWordPart(char character)
        {
            return ( Character.isLetter(character) );
        }

        /* (non-Javadoc)
         * Method declared on IWordDetector.
         */
        public boolean isWordStart(char character)
        {
            return ( ( character == '#' ) || Character.isLetter(character) );
        }
    }

}
