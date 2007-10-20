/*
 * HTMLTokenMarker.java - HTML token marker
 * Copyright (C) 1998, 1999 Slava Pestov
 */
package org.smartfrog.tools.gui.browser.syntax;


import javax.swing.text.Segment;
import org.gjt.sp.jedit.syntax.*;


/**
 * HTML token marker.
 *
 * @author Slava Pestov
 * @version $Id: SfXMLTokenMarker.java 482 2004-05-11 18:56:55Z julgui $
 */
public class SfXMLTokenMarker extends TokenMarker
{
  public static final byte JAVASCRIPT = Token.INTERNAL_FIRST;
  public static final byte HTML_LITERAL_QUOTE = Token.INTERNAL_FIRST + 1;
  public static final byte HTML_LITERAL_NO_QUOTE = Token.INTERNAL_FIRST + 2;
  public static final byte INSIDE_TAG = Token.INTERNAL_FIRST + 3;

  public SfXMLTokenMarker()
  {
    this(true);
  }

  public SfXMLTokenMarker(boolean js)
  {
    this.js = js;
    keywords = JavaScriptTokenMarker.getKeywords();
  }

  public byte markTokensImpl(byte token, Segment line, int lineIndex)
  {
    char[] array = line.array;
    int offset = line.offset;
    lastOffset = offset;
    lastKeyword = offset;
    int length = line.count + offset;
    boolean backslash = false;

    loop:
    for (int i = offset; i < length; i++)
    {
      int i1 = (i + 1);

      char c = array[i];
      if (c == '\\')
      {
        backslash = !backslash;
        if (token == JAVASCRIPT)
          continue;
      }

      switch (token)
      {
        case Token.NULL: // HTML text
          backslash = false;
          switch (c)
          {
            case '\\':
              addToken(i - lastOffset, token);
              lastOffset = lastKeyword = i;
              token = Token.OPERATOR;
              break;
            case '<':
              addToken(i - lastOffset, token);
              lastOffset = lastKeyword = i;
              if (SyntaxUtilities.regionMatches(false, line, i1, "!--"))
              {
                i += 3;
                token = Token.COMMENT1;
              }
              else if (js && SyntaxUtilities.regionMatches(true, line, i1, "script>"))
              {
                addToken(8, Token.KEYWORD1);
                lastOffset = lastKeyword = (i += 8);
                token = JAVASCRIPT;
              } else
                token = Token.KEYWORD1;
              break;
            case '&':
              addToken(i - lastOffset, token);
              lastOffset = lastKeyword = i;
              token = Token.KEYWORD2;
              break;
          }
          break;
        case Token.OPERATOR:
          backslash = false;
          if (c != '<')
          {
            addToken(i1 - lastOffset, token);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          }
          break;
        case Token.KEYWORD1: // Inside a tag
          backslash = false;
          if (c == '>')
          {
            addToken(i1 - lastOffset, token);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          } else if (c == ' ' || c == '\t') {
            addToken(i1 - lastOffset, token);
            lastOffset = lastKeyword = i1;
            token = INSIDE_TAG; //Token.KEYWORD3;
          }
          break;
        case INSIDE_TAG:
          if (c == '>')
          {
            addToken(i1 - lastOffset, Token.KEYWORD1);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          } else {//if (c != ' ' && c != '\t') {
            addToken(i - lastOffset, Token.NULL);
            lastOffset = lastKeyword = i;
            token = Token.KEYWORD3;
          }
          break;
        case Token.KEYWORD2: // Inside an entity
          backslash = false;
          if (c == ';')
          {
            addToken(i1 - lastOffset, token);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          }
          break;
        case Token.KEYWORD3: // Inside an attribute
          if (c == '=')
          {
            addToken(i - lastOffset, token);
            addToken(1, Token.LABEL);
            lastOffset = lastKeyword = i1;
            if (i1 < array.length && array[i1] == '"')
            {
              token = HTML_LITERAL_QUOTE;
              i++;
            } else {
              token = HTML_LITERAL_NO_QUOTE;
            }
          } else if (c == '>') {
            addToken(i - lastOffset, token);
            addToken(1, Token.KEYWORD1);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          } else if (c == ' ' || c == '\t') {
            addToken(i1 - lastOffset, token);
            lastOffset = lastKeyword = i1;
            token = INSIDE_TAG;
          }
          break;
        case HTML_LITERAL_QUOTE:
          if (c == '"')
          {
            addToken(i1 - lastOffset, Token.LITERAL1);
            lastOffset = lastKeyword = i1;
            token = INSIDE_TAG;
          }
          break;
        case HTML_LITERAL_NO_QUOTE:
          if (c == ' ' || c == '\t')
          {
            addToken(i1 - lastOffset, Token.LITERAL1);
            lastOffset = lastKeyword = i1;
            token = INSIDE_TAG;
          } else if (c == '>') {
            addToken(i - lastOffset, Token.LITERAL1);
            addToken(1, Token.KEYWORD1);
            lastOffset = lastKeyword = i1;
            token = Token.NULL;
          }
          break;
        case Token.COMMENT1: // Inside a comment
          backslash = false;
          if (SyntaxUtilities.regionMatches(false, line, i, "-->"))
          {
            addToken((i + 3) - lastOffset, token);
            lastOffset = lastKeyword = i + 3;
            token = Token.NULL;
          }
          break;
        case JAVASCRIPT: // Inside a JavaScript
          switch (c)
          {
            case '<':
              backslash = false;
              doKeyword(line, i, c);
              if (SyntaxUtilities.regionMatches(true, line, i1, "/script>"))
              {
                addToken(i - lastOffset, Token.NULL);
                addToken(9, Token.KEYWORD1);
                lastOffset = lastKeyword = (i += 9);
                token = Token.NULL;
              }
              break;
            case '"':
              if (backslash)
                backslash = false;
              else
              {
                doKeyword(line, i, c);
                addToken(i - lastOffset, Token.NULL);
                lastOffset = lastKeyword = i;
                token = Token.LITERAL1;
              }
              break;
            case '\'':
              if (backslash)
                backslash = false;
              else
              {
                doKeyword(line, i, c);
                addToken(i - lastOffset, Token.NULL);
                lastOffset = lastKeyword = i;
                token = Token.LITERAL2;
              }
              break;
            case '/':
              backslash = false;
              doKeyword(line, i, c);
              if (length - i > 1)
              {
                addToken(i - lastOffset, Token.NULL);
                lastOffset = lastKeyword = i;
                if (array[i1] == '/')
                {
                  addToken(length - i, Token.COMMENT2);
                  lastOffset = lastKeyword = length;
                  break loop;
                }
                else if (array[i1] == '*')
                {
                  token = Token.COMMENT2;
                }
              }
              break;
            default:
              backslash = false;
              if (!Character.isLetterOrDigit(c) && c != '_')
                doKeyword(line, i, c);
              break;
          }
          break;
        case Token.LITERAL1: // JavaScript "..."
          if (backslash)
            backslash = false;
          else if (c == '"')
          {
            addToken(i1 - lastOffset, Token.LITERAL1);
            lastOffset = lastKeyword = i1;
            token = JAVASCRIPT;
          }
          break;
        case Token.LITERAL2: // JavaScript '...'
          if (backslash)
            backslash = false;
          else if (c == '\'')
          {
            addToken(i1 - lastOffset, Token.LITERAL1);
            lastOffset = lastKeyword = i1;
            token = JAVASCRIPT;
          }
          break;
        case Token.COMMENT2: // Inside a JavaScript comment
          backslash = false;
          if (c == '*' && length - i > 1 && array[i1] == '/')
          {
            addToken((i += 2) - lastOffset, Token.COMMENT1);
            lastOffset = lastKeyword = i;
            token = JAVASCRIPT;
          }
          break;
        default:
          throw new InternalError("Invalid state: " + token);
      }
    }

    switch (token)
    {
      case Token.LITERAL1:
      case Token.LITERAL2:
        addToken(length - lastOffset, Token.INVALID);
        token = JAVASCRIPT;
        break;
      case Token.KEYWORD2:
        addToken(length - lastOffset, Token.INVALID);
        token = Token.NULL;
        break;
      case JAVASCRIPT:
        doKeyword(line, length, '\0');
        addToken(length - lastOffset, Token.NULL);
        break;
      case Token.COMMENT2:
        addToken(length - lastOffset, Token.COMMENT1);
        break;
      case INSIDE_TAG:
        break;
      case HTML_LITERAL_QUOTE: case HTML_LITERAL_NO_QUOTE:
        addToken(length - lastOffset, Token.LITERAL1);
        break;
      default:
        addToken(length - lastOffset, token);
        break;
    }

    return token;
  }

  // private members
  private KeywordMap keywords;
  private boolean js;
  private int lastOffset;
  private int lastKeyword;

  private boolean doKeyword(Segment line, int i, char c)
  {
    int i1 = i + 1;

    int len = i - lastKeyword;
    byte id = keywords.lookup(line, lastKeyword, len);
    if (id != Token.NULL)
    {
      if (lastKeyword != lastOffset)
        addToken(lastKeyword - lastOffset, Token.NULL);
      addToken(len, id);
      lastOffset = i;
    }
    lastKeyword = i1;
    return false;
  }
}
