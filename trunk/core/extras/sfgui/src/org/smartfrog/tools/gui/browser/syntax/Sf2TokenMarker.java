
/*
 * Based in JavaTokenMarker.java - Java token marker
 * Copyright (C) 1999 Slava Pestov

 */
package org.smartfrog.tools.gui.browser.syntax;

import javax.swing.text.Segment;
import org.gjt.sp.jedit.syntax.*;

// Special syntax for SF
/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    28 November 2001
 */
public class Sf2TokenMarker extends CTokenMarker {

   // private members
   private static KeywordMap sfKeywords;
   // private members

   private boolean javadoc;

   private int lastOffset;
   private int lastKeyword;

   /**
    *  Constructor for the SfTokenMarker object
    */
   public Sf2TokenMarker() {
      super(false, true, getKeywords());
   }

   /**
    *  Gets the keywords attribute of the SfTokenMarker class
    *
    *@return    The keywords value
    */
   public static KeywordMap getKeywords() {
      if (sfKeywords == null) {
         sfKeywords = new KeywordMap(false);

         /**
          *  Keyword 2 token id. This can be used to mark a keyword. This should
          *  be used for preprocessor commands, or variables.
          */
         //KEYWORD2
         sfKeywords.add("#include", Token.KEYWORD2);
         /**
          *  Keyword 3 token id. This can be used to mark a keyword. This should
          *  be used for data types.
          */
         //KEYWORD3
         sfKeywords.add("Prim", Token.KEYWORD3);
         sfKeywords.add("Compound", Token.KEYWORD3);
         /**
          *  Keyword 1 token id. This can be used to mark a keyword. This should
          *  be used for general language constructs.
          */
         //KEYWORD1
         sfKeywords.add("extends", Token.KEYWORD1);
         //SF
         sfKeywords.add("LAZY", Token.KEYWORD1);
         /**
          *  Literal 2 token id. This can be used to mark an object literal (eg,
          *  Java mode uses this to mark true, false, etc)
          */
         //LITEREAL2
         sfKeywords.add("true", Token.LITERAL2);
         sfKeywords.add("false", Token.LITERAL2);
         //sf
         sfKeywords.add("ROOT", Token.LITERAL2);
         sfKeywords.add("PARENT", Token.LITERAL2);
         sfKeywords.add("ATTRIB", Token.LITERAL2);
         sfKeywords.add("HOST", Token.LITERAL2);
         sfKeywords.add("PROPERTY", Token.LITERAL2);
         sfKeywords.add("NULL", Token.LITERAL2);
         /**
          *  Label token id. This can be used to mark labels (eg, C mode uses
          *  this to mark ...: sequences)
          */
         //Labels
         sfKeywords.add("sfConfig", Token.LABEL);
         sfKeywords.add("sfHost", Token.LABEL);
         sfKeywords.add("sfExport", Token.LABEL);
         sfKeywords.add("sfClass", Token.LABEL);
         sfKeywords.add("sfClassBase", Token.LABEL);
         sfKeywords.add("sfDeployerClass", Token.LABEL);
         sfKeywords.add("sfProcessName", Token.LABEL);
         sfKeywords.add("sfProcessHost", Token.LABEL);
         sfKeywords.add("sfProcessCompound", Token.LABEL);
         sfKeywords.add("sfProcessComponentName", Token.LABEL);
         sfKeywords.add("sfLiveness", Token.LABEL);
         sfKeywords.add("sfLivenessFactor", Token.LABEL);
      }
      return sfKeywords;
   }

   /**
    *  Description of the Method
    *
    *@param  token      Description of Parameter
    *@param  line       Description of Parameter
    *@param  lineIndex  Description of Parameter
    *@return            Description of the Returned Value
    */
   public byte markTokensImpl(byte token, Segment line, int lineIndex) {
      char[] array = line.array;
      int offset = line.offset;
      lastOffset = offset;
      lastKeyword = offset;
      int length = line.count + offset;
      boolean backslash = false;
      loop :
      for (int i = offset; i < length; i++) {
         int i1 = (i + 1);

         char c = array[i];
         if (c == '\\') {
            backslash = !backslash;
            continue;
         }

         switch (token) {
             case Token.NULL:
                switch (c) {
                    case '#':
                       if (backslash) {
                          backslash = false;
                       }
                       if (length - i > 1) {
                          if (array[i1] == '#') {
                             backslash = false;
                             //     doKeyword(line,i,c);
                             addToken(i - lastOffset, token);
                             lastOffset = lastKeyword = i;
                             token = Token.BSHSTRING;
                             break;
                          }
                       }

                       break;
                    case '"':
                       doKeyword(line, i, c);
                       if (backslash) {
                          backslash = false;
                       } else {
                          addToken(i - lastOffset, token);
                          token = Token.LITERAL1;
                          lastOffset = lastKeyword = i;
                       }
                       break;
                    case '\'':
                       doKeyword(line, i, c);
                       if (backslash) {
                          backslash = false;
                       } else {
                          addToken(i - lastOffset, token);
                          token = Token.LITERAL2;
                          lastOffset = lastKeyword = i;
                       }
                       break;
                    case ':':
                       if (lastKeyword == offset) {
                          if (doKeyword(line, i, c)) {
                             break;
                          }
                          backslash = false;
                          addToken(i1 - lastOffset, Token.LABEL);
                          lastOffset = lastKeyword = i1;
                       } else if (doKeyword(line, i, c)) {
                          break;
                       }
                       break;
                    case '/':
                       backslash = false;
                       doKeyword(line, i, c);
                       if (length - i > 1) {
                          switch (array[i1]) {
                              case '*':
                                 addToken(i - lastOffset, token);
                                 lastOffset = lastKeyword = i;
                                 if (javadoc && length - i > 2
                                        && array[i + 2] == '*') {
                                    token = Token.COMMENT2;
                                 } else {
                                    token = Token.COMMENT1;
                                 }
                                 break;
                              case '/':
                                 addToken(i - lastOffset, token);
                                 addToken(length - i, Token.COMMENT1);
                                 lastOffset = lastKeyword = length;
                                 break loop;
                          }
                       }
                       break;
                    default:
                       backslash = false;
                       if (!Character.isLetterOrDigit(c)
                              && c != '_') {
                          doKeyword(line, i, c);
                       }
                       break;
                }
                break;
             case Token.COMMENT1:
             case Token.COMMENT2:
                backslash = false;
                if (c == '*' && length - i > 1) {
                   if (array[i1] == '/') {
                      i++;
                      addToken((i + 1) - lastOffset, token);
                      token = Token.NULL;
                      lastOffset = lastKeyword = i + 1;
                   }
                }
                break;
             case Token.LITERAL1:
                if (backslash) {
                   backslash = false;
                } else if (c == '"') {
                   addToken(i1 - lastOffset, token);
                   token = Token.NULL;
                   lastOffset = lastKeyword = i1;
                }
                break;
             case Token.LITERAL2:
                if (backslash) {
                   backslash = false;
                } else if (c == '\'') {
                   addToken(i1 - lastOffset, Token.LITERAL1);
                   token = Token.NULL;
                   lastOffset = lastKeyword = i1;
                }
                break;
             case Token.BSHSTRING:
                if (c == '#' && (array[i - 1] != '#')) {
                   addToken((i + 1) - lastOffset, token);
                   token = Token.NULL;
                   lastOffset = lastKeyword = i + 1;
                }
                break;
             default:
                throw new InternalError("Invalid state: "
                       + token);
         }
      }

      if (token == Token.NULL) {
         doKeyword(line, length, '\0');
      }

      switch (token) {
          case Token.LITERAL1:
          case Token.LITERAL2:
             addToken(length - lastOffset, Token.INVALID);
             token = Token.NULL;
             break;
          case Token.KEYWORD2:
             addToken(length - lastOffset, token);
             if (!backslash) {
                token = Token.NULL;
             }
          default:
             addToken(length - lastOffset, token);
             break;
      }

      return token;
   }

   /**
    *  Description of the Method
    *
    *@param  line  Description of Parameter
    *@param  i     Description of Parameter
    *@param  c     Description of Parameter
    *@return       Description of the Returned Value
    */
   private boolean doKeyword(Segment line, int i, char c) {
      int i1 = i + 1;

      int len = i - lastKeyword;
      byte id = sfKeywords.lookup(line, lastKeyword, len);
      if (id != Token.NULL) {
         if (lastKeyword != lastOffset) {
            addToken(lastKeyword - lastOffset, Token.NULL);
         }
         addToken(len, id);
         lastOffset = i;
      }
      lastKeyword = i1;
      return false;
   }

}

/*
 * ChangeLog:
 *
 */
