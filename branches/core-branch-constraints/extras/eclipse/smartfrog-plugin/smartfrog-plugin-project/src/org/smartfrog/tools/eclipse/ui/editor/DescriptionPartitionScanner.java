
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

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import java.util.ArrayList;
import java.util.List;


/**
 * Description partition scanner
 */
public class DescriptionPartitionScanner
    extends RuleBasedPartitionScanner
{
    private static final String STAR_COMMENT_END = "*/"; //$NON-NLS-1$
    private static final String STAR_COMMENT_BEGIN = "/*"; //$NON-NLS-1$
    private static final String SINGLE_QUOTE = "'"; //$NON-NLS-1$
    private static final String DOUBLE_QUOTE = "\""; //$NON-NLS-1$
    private static final String BACK_SLASH = "//"; //$NON-NLS-1$
    public final static String SMARTFROG_MULTILINE_COMMENT =
        "__smartFrog_multiline_comment"; //$NON-NLS-1$
    public final static String SMARTFROG_MULTILINE_STRING =
        "__smartFrog_multiline_string"; //$NON-NLS-1$
    public final static String[] SMARTFROG_PARTITION_TYPES = new String[] {
            SMARTFROG_MULTILINE_COMMENT, SMARTFROG_MULTILINE_STRING
        };

    private static DescriptionPartitionScanner INSTANCE;

    public static DescriptionPartitionScanner getInstance()
    {
        if (null == INSTANCE) {
            INSTANCE = new DescriptionPartitionScanner();
        }

        return INSTANCE;
    }
  

    /**
     * Create the partition based on comments or not
     */
    private DescriptionPartitionScanner()
    {
        super();

        IToken comment = new Token(SMARTFROG_MULTILINE_COMMENT);

        List rules = new ArrayList();
        rules.add(new EndOfLineRule(BACK_SLASH, Token.UNDEFINED)); //$NON-NLS-1$
        rules.add(new SingleLineRule(DOUBLE_QUOTE, DOUBLE_QUOTE,
                Token.UNDEFINED, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
        rules.add(new SingleLineRule(SINGLE_QUOTE, SINGLE_QUOTE,
                Token.UNDEFINED, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
        rules.add(new MultiLineRule("##", "#", //$NON-NLS-1$ //$NON-NLS-2$
                new Token(SMARTFROG_MULTILINE_STRING), (char)0, true)); //$NON-NLS-1$ //$NON-NLS-2$
        rules.add(new CommentPredicateRule(comment));
        rules.add(new MultiLineRule(STAR_COMMENT_BEGIN, STAR_COMMENT_END,
                comment, (char)0, true)); //$NON-NLS-1$ //$NON-NLS-2$

        IPredicateRule[] result = new IPredicateRule[ rules.size() ];
        rules.toArray(result);
        setPredicateRules(result);
    }
    
    /**
     * Detector for empty comments.
     */
    static class EmptyCommentDetector
        implements IWordDetector
    {
        //Comments start char
        public boolean isWordStart(char c)
        {
            return ( c == '/' );
        }

        //Comments char // or /*
        public boolean isWordPart(char c)
        {
            return ( ( c == '*' ) || ( c == '/' ) );
        }
    }


    /**
     * Detect comments
     */
    static class CommentPredicateRule
        extends WordRule
        implements IPredicateRule
    {
        private IToken fSuccessToken;

        public CommentPredicateRule(IToken successToken)
        {
            super(new EmptyCommentDetector());
            fSuccessToken = successToken;
            addWord("/**/", fSuccessToken); //$NON-NLS-1$
        }

        /*
         * @see org.eclipse.jface.text.rules.IPredicateRule#evaluate(ICharacterScanner, boolean)
         */
        public IToken evaluate(ICharacterScanner scanner, boolean resume)
        {
            return super.evaluate(scanner);
        }

        /*
         * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
         */
        public IToken getSuccessToken()
        {
            return fSuccessToken;
        }
    }


}
