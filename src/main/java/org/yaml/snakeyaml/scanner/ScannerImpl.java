/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.scanner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEndToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.DocumentEndToken;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.Token;

/**
 * Reader do the dirty work of checking for BOM and converting the input data to
 * Unicode. It also adds NUL to the end.
 * 
 * Reader supports the following methods
 * 
 * <pre>
 * reader.peek(i=0) # peek the next i-th character self.prefix(l=1)
 * reader.peek the next l characters
 * reader.forward(l=1) read the next l characters and move the pointer.
 * </pre>
 */
/**
 * @author as80418
 * 
 */
public class ScannerImpl implements Scanner {

    private final static String NULL_BL_LINEBR = "\0 \r\n\u0085";
    private final static String NULL_BL_T_LINEBR = "\0 \t\r\n\u0085";
    private final static String NULL_OR_OTHER = NULL_BL_T_LINEBR;
    private final static String NULL_OR_LINEBR = "\0\r\n\u0085";
    private final static String FULL_LINEBR = "\r\n\u0085";
    private final static String BLANK_OR_LINEBR = " \r\n\u0085";
    private final static String S4 = "\0 \t\r\n\u0028[]{}";
    private final static String ALPHA = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    private final static String STRANGE_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][-';/?:@&=+$,.!~*()%";
    private final static String RN = "\r\n";
    private final static String BLANK_T = " \t";
    private final static String SPACES_AND_STUFF = "'\"\\\0 \t\r\n\u0085";
    private final static String DOUBLE_ESC = "\"\\";
    private final static String NON_ALPHA_OR_NUM = "\0 \t\r\n\u0085?:,]}%@`";

    private final static Pattern NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
    private final static Pattern NON_ALPHA = Pattern.compile("[^-0-9A-Za-z_]");
    private final static Pattern R_FLOWZERO = Pattern
            .compile("[\0 \t\r\n\u0085]|(:[\0 \t\r\n\u0028])");
    private final static Pattern R_FLOWNONZERO = Pattern.compile("[\0 \t\r\n\u0085\\[\\]{},:?]");
    private final static Pattern END_OR_START = Pattern
            .compile("^(---|\\.\\.\\.)[\0 \t\r\n\u0085]$");
    private final static Pattern ENDING = Pattern.compile("^---[\0 \t\r\n\u0085]$");
    private final static Pattern START = Pattern.compile("^\\.\\.\\.[\0 \t\r\n\u0085]$");
    private final static Pattern BEG = Pattern
            .compile("^([^\0 \t\r\n\u0085\\-?:,\\[\\]{}#&*!|>'\"%@]|([\\-?:][^\0 \t\r\n\u0085]))");

    private final static Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
    private final static Map<Character, Integer> ESCAPE_CODES = new HashMap<Character, Integer>();

    static {
        ESCAPE_REPLACEMENTS.put(new Character('0'), "\0");
        ESCAPE_REPLACEMENTS.put(new Character('a'), "\u0007");
        ESCAPE_REPLACEMENTS.put(new Character('b'), "\u0008");
        ESCAPE_REPLACEMENTS.put(new Character('t'), "\u0009");
        ESCAPE_REPLACEMENTS.put(new Character('\t'), "\u0009");
        ESCAPE_REPLACEMENTS.put(new Character('n'), "\n");
        ESCAPE_REPLACEMENTS.put(new Character('v'), "\u000B");
        ESCAPE_REPLACEMENTS.put(new Character('f'), "\u000C");
        ESCAPE_REPLACEMENTS.put(new Character('r'), "\r");
        ESCAPE_REPLACEMENTS.put(new Character('e'), "\u001B");
        ESCAPE_REPLACEMENTS.put(new Character(' '), "\u0020");
        ESCAPE_REPLACEMENTS.put(new Character('"'), "\"");
        ESCAPE_REPLACEMENTS.put(new Character('\\'), "\\");
        ESCAPE_REPLACEMENTS.put(new Character('N'), "\u0085");
        ESCAPE_REPLACEMENTS.put(new Character('_'), "\u00A0");
        ESCAPE_REPLACEMENTS.put(new Character('L'), "\u2028");
        ESCAPE_REPLACEMENTS.put(new Character('P'), "\u2029");

        ESCAPE_CODES.put(new Character('x'), new Integer(2));
        ESCAPE_CODES.put(new Character('u'), new Integer(4));
        ESCAPE_CODES.put(new Character('U'), new Integer(8));
    }
    private org.yaml.snakeyaml.reader.Reader reader;
    // Had we reached the end of the stream?
    private boolean done = false;

    // The number of unclosed '{' and '['. `flow_level == 0` means block
    // context.
    private int flowLevel = 0;

    // List of processed tokens that are not yet emitted.
    private List<Token> tokens;

    // Number of tokens that were emitted through the `get_token` method.
    private int tokensTaken = 0;

    // The current indentation level.
    private int indent = -1;

    // Past indentation levels.
    private List<Integer> indents;

    // Variables related to simple keys treatment. See PyYAML.
    private boolean allowSimpleKey = true;

    /*
     * Keep track of possible simple keys. This is a dictionary. The key is
     * `flow_level`; there can be no more that one possible simple key for each
     * level. The value is a SimpleKey record: (token_number, required, index,
     * line, column, mark) A simple key may start with ALIAS, ANCHOR, TAG,
     * SCALAR(flow), '[', or '{' tokens.
     */
    private Map<Integer, SimpleKey> possibleSimpleKeys;

    private boolean docStart = false;// only JvYAML ???

    public ScannerImpl(org.yaml.snakeyaml.reader.Reader reader) {
        this.reader = reader;
        this.tokens = new LinkedList<Token>();
        this.indents = new LinkedList<Integer>();
        this.possibleSimpleKeys = new HashMap<Integer, SimpleKey>();
        fetchStreamStart();// Add the STREAM-START token.
    }

    /**
     * Check if the next token is one of the given types.
     */
    public boolean checkToken(final Class<Token>[] choices) {
        while (needMoreTokens()) {
            fetchMoreTokens();
        }
        if (!this.tokens.isEmpty()) {
            if (choices.length == 0) {
                return true;
            }
            final Token first = this.tokens.get(0);
            for (int i = 0, j = choices.length; i < j; i++) {
                if (choices[i].isInstance(first)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return the next token, but do not delete if from the queue.
     */
    public Token peekToken() {
        while (needMoreTokens()) {
            fetchMoreTokens();
        }
        return (Token) (this.tokens.isEmpty() ? null : this.tokens.get(0));
    }

    /**
     * Return the next token.
     */
    public Token getToken() {
        while (needMoreTokens()) {
            fetchMoreTokens();
        }
        if (!this.tokens.isEmpty()) {
            this.tokensTaken++;
            return (Token) this.tokens.remove(0);
        }
        return null;
    }

    private class TokenIterator implements Iterator<Token> {
        public boolean hasNext() {
            return null != peekToken();
        }

        public Token next() {
            return getToken();
        }

        public void remove() {
        }
    }

    public Iterator<Token> eachToken() {
        return new TokenIterator();
    }

    private boolean needMoreTokens() {
        if (this.done) {
            return false;
        }
        // TODO not implemented but present in PyYAML
        // The current token may be a potential simple key, so we
        // need to look further.
        /* self.stale_possible_simple_keys() */
        return this.tokens.isEmpty() || nextPossibleSimpleKey() == this.tokensTaken;
    }

    private Token fetchMoreTokens() {
        // Eat whitespaces and comments until we reach the next token.
        scanToNextToken();
        // TODO not implemented but present in PyYAML
        // Remove obsolete possible simple keys.
        /* self.stale_possible_simple_keys() */
        // Compare the current indentation and column. It may add some tokens
        // and decrease the current indentation level.
        unwindIndent(reader.getColumn());
        // Peek the next character.
        final char ch = reader.peek();
        final boolean colz = reader.getColumn() == 0;
        switch (ch) {
        case '\0':
            // Is it the end of stream?
            return fetchStreamEnd();
        case '%':
            // Is it a directive?
            if (colz) {
                return fetchDirective();
            }
            break;
        case '-':
            // Is it the document start?
            if ((colz || docStart) && ENDING.matcher(reader.prefix(4)).matches()) {
                return fetchDocumentStart();
            } else if (NULL_OR_OTHER.indexOf(reader.peek(1)) != -1) {
                return fetchBlockEntry();
            }
            break;
        case '.':
            // Is it the document end?
            if (colz && START.matcher(reader.prefix(4)).matches()) {
                return fetchDocumentEnd();
            }
            break;
        // TODO support for BOM within a stream. (not implemented in PyYAML)
        case '[':
            // Is it the flow sequence start indicator?
            return fetchFlowSequenceStart();
        case '{':
            // Is it the flow mapping start indicator?
            return fetchFlowMappingStart();
        case ']':
            // Is it the flow sequence end indicator?
            return fetchFlowSequenceEnd();
        case '}':
            // Is it the flow mapping end indicator?
            return fetchFlowMappingEnd();
        case ',':
            // Is it the flow entry indicator?
            return fetchFlowEntry();
            // TODO missing block entry indicator from PyYAML
        case '?':
            // Is it the key indicator?
            if (this.flowLevel != 0 || NULL_OR_OTHER.indexOf(reader.peek(1)) != -1) {
                return fetchKey();
            }
            break;
        case ':':
            // Is it the value indicator?
            if (this.flowLevel != 0 || NULL_OR_OTHER.indexOf(reader.peek(1)) != -1) {
                return fetchValue();
            }
            break;
        case '*':
            // Is it an alias?
            return fetchAlias();
        case '&':
            // Is it an anchor?
            return fetchAnchor();
        case '!':
            // Is it a tag?
            return fetchTag();
        case '|':
            // Is it a literal scalar?
            if (this.flowLevel == 0) {
                return fetchLiteral();
            }
            break;
        case '>':
            // Is it a folded scalar?
            if (this.flowLevel == 0) {
                return fetchFolded();
            }
            break;
        case '\'':
            // Is it a single quoted scalar?
            return fetchSingle();
        case '"':
            // Is it a double quoted scalar?
            return fetchDouble();
        }
        // It must be a plain scalar then.
        if (BEG.matcher(reader.prefix(2)).find()) {
            return fetchPlain();
        }
        throw new ScannerException("while scanning for the next token", null, "found character "
                + ch + "(" + ((int) ch) + " that cannot start any token", reader.getMark(), null);
    }

    /**
     * Return the number of the nearest possible simple key. Actually we don't
     * need to loop through the whole dictionary.
     */
    private int nextPossibleSimpleKey() {
        for (final Iterator<SimpleKey> iter = this.possibleSimpleKeys.values().iterator(); iter
                .hasNext();) {
            final SimpleKey key = iter.next();
            if (key.getTokenNumber() > 0) {
                return key.getTokenNumber();
            }
        }
        return -1;
    }

    // TODO implement def stale_possible_simple_keys(self):

    /**
     * The next token may start a simple key. We check if it's possible and save
     * its position. This function is called for ALIAS, ANCHOR, TAG,
     * SCALAR(flow), '[', and '{'.
     */
    private void savePossibleSimpleKey() {
        // TODO missing stuff from PyYAML

        // The next token might be a simple key. Let's save it's number and
        // position.
        if (this.allowSimpleKey) {
            this.possibleSimpleKeys.put(new Integer(this.flowLevel), new SimpleKey(this.tokensTaken
                    + this.tokens.size(), (this.flowLevel == 0)
                    && this.indent == this.reader.getColumn(), -1, -1, this.reader.getColumn(),
                    this.reader.getMark()));
        }
    }

    // TODO implement def remove_possible_simple_key(self):

    /**
     * <pre>
     * In flow context, tokens should respect indentation.
     * Actually the condition should be `self.indent &gt;= column` according to
     * the spec. But this condition will prohibit intuitively correct
     * constructions such as
     * key : {
     * }
     * </pre>
     */
    private void unwindIndent(final int col) {
        // In the flow context, indentation is ignored. We make the scanner less
        // restrictive then specification requires.
        if (this.flowLevel != 0) {
            return;
        }

        // In block context, we may need to issue the BLOCK-END tokens.
        while (this.indent > col) {
            Mark mark = reader.getMark();
            this.indent = ((Integer) (this.indents.remove(0))).intValue();
            this.tokens.add(new BlockEndToken(mark, mark));
        }
    }

    /**
     * Check if we need to increase indentation.
     */
    private boolean addIndent(final int col) {
        if (this.indent < col) {
            this.indents.add(0, new Integer(this.indent));
            this.indent = col;
            return true;
        }
        return false;
    }

    // Fetchers.

    /**
     * We always add STREAM-START as the first token and STREAM-END as the last
     * token.
     */
    private Token fetchStreamStart() {
        this.docStart = true;
        this.tokens.add(Token.STREAM_START);
        return Token.STREAM_START;
    }

    private Token fetchStreamEnd() {
        unwindIndent(-1);
        this.allowSimpleKey = false;
        this.possibleSimpleKeys = new HashMap<Integer, SimpleKey>();
        this.tokens.add(Token.STREAM_END);
        this.done = true;
        return Token.STREAM_END;
    }

    private Token fetchDirective() {
        unwindIndent(-1);
        this.allowSimpleKey = false;
        final Token tok = scanDirective();
        this.tokens.add(tok);
        return tok;
    }

    private Token fetchDocumentStart() {
        this.docStart = false;
        return fetchDocumentIndicator(true);
    }

    private Token fetchDocumentEnd() {
        return fetchDocumentIndicator(false);
    }

    private Token fetchDocumentIndicator(final boolean isDocumentStart) {
        // Set the current intendation to -1.
        unwindIndent(-1);
        // Reset simple keys. Note that there could not be a block collection
        // after '---'.
        /* TODO missing self.remove_possible_simple_key() */
        this.allowSimpleKey = false;

        // Add DOCUMENT-START or DOCUMENT-END.
        Mark startMark = reader.getMark();
        reader.forward(3);
        Mark endMark = reader.getMark();
        Token token;
        if (isDocumentStart) {
            token = new DocumentStartToken(startMark, endMark);
        } else {
            token = new DocumentEndToken(startMark, endMark);
        }
        this.tokens.add(token);
        return token;
    }

    private Token fetchFlowSequenceStart() {
        return fetchFlowCollectionStart(false);
    }

    private Token fetchFlowMappingStart() {
        return fetchFlowCollectionStart(true);
    }

    private Token fetchFlowCollectionStart(boolean isMappingStart) {
        // '[' and '{' may start a simple key.
        savePossibleSimpleKey();
        // Increase the flow level.
        this.flowLevel++;
        // Simple keys are allowed after '[' and '{'.
        this.allowSimpleKey = true;
        // Add FLOW-SEQUENCE-START or FLOW-MAPPING-START.
        Mark startMark = reader.getMark();
        reader.forward(1);
        Mark endMark = reader.getMark();
        Token token;
        if (isMappingStart) {
            token = new FlowMappingStartToken(startMark, endMark);
        } else {
            token = new FlowSequenceStartToken(startMark, endMark);
        }
        this.tokens.add(token);
        return token;
    }

    private Token fetchFlowSequenceEnd() {
        return fetchFlowCollectionEnd(false);
    }

    private Token fetchFlowMappingEnd() {
        return fetchFlowCollectionEnd(true);
    }

    private Token fetchFlowCollectionEnd(final boolean isMappingEnd) {
        // Reset possible simple key on the current level.
        /* TODO missing self.remove_possible_simple_key() */
        // Decrease the flow level.
        this.flowLevel--;
        // No simple keys after ']' or '}'.
        this.allowSimpleKey = false;
        // Add FLOW-SEQUENCE-END or FLOW-MAPPING-END.
        Mark startMark = reader.getMark();
        reader.forward(1);
        Mark endMark = reader.getMark();
        Token token;
        if (isMappingEnd) {
            token = new FlowMappingEndToken(startMark, endMark);
        } else {
            token = new FlowSequenceEndToken(startMark, endMark);
        }
        this.tokens.add(token);
        return token;
    }

    private Token fetchFlowEntry() {
        // Simple keys are allowed after ','.
        this.allowSimpleKey = true;
        // Reset possible simple key on the current level.
        /* TODO missing self.remove_possible_simple_key() */
        // Add FLOW-ENTRY.
        Mark startMark = reader.getMark();
        reader.forward(1);
        Mark endMark = reader.getMark();
        Token token = new FlowEntryToken(startMark, endMark);
        this.tokens.add(token);
        return token;
    }

    private Token fetchBlockEntry() {
        // Block context needs additional checks.
        if (this.flowLevel == 0) {
            // Are we allowed to start a new entry?
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, "sequence entries are not allowed here",
                        reader.getMark(), null);
            }
            // We may need to add BLOCK-SEQUENCE-START.
            if (addIndent(this.reader.getColumn())) {
                Mark mark = reader.getMark();
                this.tokens.add(new BlockSequenceStartToken(mark, mark));
            }
        } else {
            // It's an error for the block entry to occur in the flow
            // context,but we let the parser detect this.
        }
        // Simple keys are allowed after '-'.
        this.allowSimpleKey = true;
        // Reset possible simple key on the current level.
        /* TODO missing self.remove_possible_simple_key() */
        // Add BLOCK-ENTRY.
        Mark startMark = reader.getMark();
        reader.forward();
        Mark endMark = reader.getMark();
        Token token = new BlockEntryToken(startMark, endMark);
        this.tokens.add(token);
        return token;
    }

    private Token fetchKey() {
        // Block context needs additional checks.
        if (this.flowLevel == 0) {
            // Are we allowed to start a key (not nessesary a simple)?
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, "mapping keys are not allowed here", reader
                        .getMark(), null);
            }
            // We may need to add BLOCK-MAPPING-START.
            if (addIndent(this.reader.getColumn())) {
                Mark mark = reader.getMark();
                this.tokens.add(new BlockMappingStartToken(mark, mark));
            }
        }
        // Simple keys are allowed after '?' in the block context.
        this.allowSimpleKey = this.flowLevel == 0;
        // Reset possible simple key on the current level.
        /* TODO missing self.remove_possible_simple_key() */
        // Add KEY.
        Mark startMark = reader.getMark();
        reader.forward();
        Mark endMark = reader.getMark();
        this.tokens.add(new KeyToken(startMark, endMark));
        return Token.KEY;
    }

    private Token fetchValue() {
        // Do we determine a simple key?
        final SimpleKey key = (SimpleKey) this.possibleSimpleKeys.get(new Integer(this.flowLevel));
        if (key != null) {
            // Add KEY.
            this.possibleSimpleKeys.remove(new Integer(this.flowLevel));
            this.tokens.add(key.getTokenNumber() - this.tokensTaken, Token.KEY);
            if (this.flowLevel == 0 && addIndent(key.getColumn())) {
                this.tokens.add(key.getTokenNumber() - this.tokensTaken,
                        new BlockMappingStartToken(key.getMark(), key.getMark()));
            }
            /*
             * TODO missing If this key starts a new block mapping, we need to
             * add BLOCK-MAPPING-START.
             */
            // There cannot be two simple keys one after another.
            this.allowSimpleKey = false;

        } else {// It must be a part of a complex key.
            // Block context needs additional checks.Do we really need them?
            // They
            // will be catched by the parser anyway.)
            if (this.flowLevel == 0) {
                // We are allowed to start a complex value if and only if we can
                // start a simple key.
                if (!this.allowSimpleKey) {
                    throw new ScannerException(null, null, "mapping values are not allowed here",
                            reader.getMark(), null);
                }
            }

        }
        reader.forward();
        this.tokens.add(Token.VALUE);
        return Token.VALUE;
    }

    private Token fetchAlias() {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = scanAnchor(false);
        this.tokens.add(tok);
        return tok;
    }

    private Token fetchAnchor() {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = scanAnchor(true);
        this.tokens.add(tok);
        return tok;
    }

    private Token fetchTag() {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = scanTag();
        this.tokens.add(tok);
        return tok;
    }

    private Token fetchLiteral() {
        return fetchBlockScalar('|');
    }

    private Token fetchFolded() {
        return fetchBlockScalar('>');
    }

    private Token fetchBlockScalar(final char style) {
        this.allowSimpleKey = true;
        final Token tok = scanBlockScalar(style);
        this.tokens.add(tok);
        return tok;
    }

    private Token fetchSingle() {
        return fetchFlowScalar('\'');
    }

    private Token fetchDouble() {
        return fetchFlowScalar('"');
    }

    private Token fetchFlowScalar(final char style) {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = scanFlowScalar(style);
        this.tokens.add(tok);
        return tok;
    }

    private Token fetchPlain() {
        savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = scanPlain();
        this.tokens.add(tok);
        return tok;
    }

    private void scanToNextToken() {
        for (;;) {
            while (reader.peek() == ' ') {
                reader.forward();
            }
            if (reader.peek() == '#') {
                while (NULL_OR_LINEBR.indexOf(reader.peek()) == -1) {
                    reader.forward();
                }
            }
            if (scanLineBreak().length() != 0) {
                if (this.flowLevel == 0) {
                    this.allowSimpleKey = true;
                }
            } else {
                break;
            }
        }
    }

    private Token scanDirective() {
        // See the specification for details.
        Mark startMark = reader.getMark();
        reader.forward();
        final String name = scanDirectiveName(startMark);
        String[] value = null;
        if (name.equals("YAML")) {
            value = scanYamlDirectiveValue(startMark);
        } else if (name.equals("TAG")) {
            value = scanTagDirectiveValue(startMark);
        } else {
            while (NULL_OR_LINEBR.indexOf(reader.peek()) == -1) {
                reader.forward();
            }
        }
        scanDirectiveIgnoredLine(startMark);
        return new DirectiveToken(name, value, null, null);
    }

    private String scanDirectiveName(Mark startMark) {
        int length = 0;
        char ch = reader.peek(length);
        boolean zlen = true;
        while (ALPHA.indexOf(ch) != -1) {
            zlen = false;
            length++;
            ch = reader.peek(length);
        }
        if (zlen) {
            throw new ScannerException("while scanning a directive", startMark,
                    "expected alphabetic or numeric character, but found " + ch + "(" + ((int) ch)
                            + ")", reader.getMark(), null);
        }
        final String value = reader.prefixForward(length);
        // forward(length);
        if (NULL_BL_LINEBR.indexOf(reader.peek()) == -1) {
            throw new ScannerException("while scanning a directive", startMark,
                    "expected alphabetic or numeric character, but found " + ch + "(" + ((int) ch)
                            + ")", reader.getMark(), null);
        }
        return value;
    }

    private String[] scanYamlDirectiveValue(Mark startMark) {
        while (reader.peek() == ' ') {
            reader.forward();
        }
        final String major = scanYamlDirectiveNumber(startMark);
        if (reader.peek() != '.') {
            throw new ScannerException("while scanning a directive", startMark,
                    "expected a digit or '.', but found " + reader.peek() + "("
                            + ((int) reader.peek()) + ")", reader.getMark(), null);
        }
        reader.forward();
        final String minor = scanYamlDirectiveNumber(startMark);
        if (NULL_BL_LINEBR.indexOf(reader.peek()) == -1) {
            throw new ScannerException("while scanning a directive", startMark,
                    "expected a digit or ' ', but found " + reader.peek() + "("
                            + ((int) reader.peek()) + ")", reader.getMark(), null);
        }
        return new String[] { major, minor };
    }

    private String scanYamlDirectiveNumber(Mark startMark) {
        final char ch = reader.peek();
        if (!Character.isDigit(ch)) {
            throw new ScannerException("while scanning a directive", startMark,
                    "expected a digit, but found " + ch + "(" + ((int) ch) + ")", reader.getMark(),
                    null);
        }
        int length = 0;
        while (Character.isDigit(reader.peek(length))) {
            length++;
        }
        final String value = reader.prefixForward(length);
        // forward(length);
        return value;
    }

    private String[] scanTagDirectiveValue(Mark startMark) {
        while (reader.peek() == ' ') {
            reader.forward();
        }
        final String handle = scanTagDirectiveHandle(startMark);
        while (reader.peek() == ' ') {
            reader.forward();
        }
        final String prefix = scanTagDirectivePrefix(startMark);
        return new String[] { handle, prefix };
    }

    private String scanTagDirectiveHandle(Mark startMark) {
        final String value = scanTagHandle("directive", startMark);
        if (reader.peek() != ' ') {
            throw new ScannerException("while scanning a directive", startMark,
                    "expected ' ', but found " + reader.peek() + "(" + ((int) reader.peek()) + ")",
                    reader.getMark(), null);
        }
        return value;
    }

    private String scanTagDirectivePrefix(Mark startMark) {
        final String value = scanTagUri("directive", startMark);
        if (NULL_BL_LINEBR.indexOf(reader.peek()) == -1) {
            throw new ScannerException("while scanning a directive", startMark,
                    "expected ' ', but found " + reader.peek() + "(" + ((int) reader.peek()) + ")",
                    reader.getMark(), null);
        }
        return value;
    }

    private String scanDirectiveIgnoredLine(Mark startMark) {
        while (reader.peek() == ' ') {
            reader.forward();
        }
        if (reader.peek() == '"') {
            while (NULL_OR_LINEBR.indexOf(reader.peek()) == -1) {
                reader.forward();
            }
        }
        final char ch = reader.peek();
        if (NULL_OR_LINEBR.indexOf(ch) == -1) {
            throw new ScannerException("while scanning a directive", startMark,
                    "expected a comment or a line break, but found " + reader.peek() + "("
                            + ((int) reader.peek()) + ")", reader.getMark(), null);
        }
        return scanLineBreak();
    }

    private Token scanAnchor(final boolean isAnchor) {
        Mark startMark = reader.getMark();
        final char indicator = reader.peek();
        final String name = indicator == '*' ? "alias" : "anchor";
        reader.forward();
        int length = 0;
        int chunk_size = 16;
        Matcher m = null;
        for (;;) {
            final String chunk = reader.prefix(chunk_size);
            if ((m = NON_ALPHA.matcher(chunk)).find()) {
                break;
            }
            chunk_size += 16;
        }
        length = m.start();
        if (length == 0) {
            throw new ScannerException("while scanning an " + name, startMark,
                    "expected alphabetic or numeric character, but found something else...", reader
                            .getMark(), null);
        }
        final String value = reader.prefixForward(length);
        // forward(length);
        if (NON_ALPHA_OR_NUM.indexOf(reader.peek()) == -1) {
            throw new ScannerException("while scanning an " + name, startMark,
                    "expected alphabetic or numeric character, but found " + reader.peek() + "("
                            + ((int) reader.peek()) + ")", reader.getMark(), null);

        }
        Mark endMark = reader.getMark();
        Token tok;
        if (isAnchor) {
            tok = new AnchorToken(value, startMark, endMark);
        } else {
            tok = new AliasToken(value, startMark, endMark);
        }
        return tok;
    }

    private Token scanTag() {
        // See the specification for details.
        Mark startMark = reader.getMark();
        char ch = reader.peek(1);
        String handle = null;
        String suffix = null;
        if (ch == '<') {
            reader.forward(2);
            suffix = scanTagUri("tag", startMark);
            if (reader.peek() != '>') {
                throw new ScannerException("while scanning a tag", startMark,
                        "expected '>', but found " + reader.peek() + "(" + ((int) reader.peek())
                                + ")", reader.getMark(), null);
            }
            reader.forward();
        } else if (NULL_BL_T_LINEBR.indexOf(ch) != -1) {
            suffix = "!";
            reader.forward();
        } else {
            int length = 1;
            boolean useHandle = false;
            while (NULL_BL_T_LINEBR.indexOf(ch) == -1) {
                if (ch == '!') {
                    useHandle = true;
                    break;
                }
                length++;
                ch = reader.peek(length);
            }
            handle = "!";
            if (useHandle) {
                handle = scanTagHandle("tag", startMark);
            } else {
                handle = "!";
                reader.forward();
            }
            suffix = scanTagUri("tag", startMark);
        }
        if (NULL_BL_LINEBR.indexOf(reader.peek()) == -1) {
            throw new ScannerException("while scanning a tag", startMark,
                    "expected ' ', but found " + reader.peek() + "(" + ((int) reader.peek()) + ")",
                    reader.getMark(), null);
        }
        Mark endMark = reader.getMark();
        return new TagToken(new String[] { handle, suffix }, startMark, endMark);
    }

    private Token scanBlockScalar(final char style) {
        final boolean folded = style == '>';
        final StringBuffer chunks = new StringBuffer();
        Mark startMark = reader.getMark();
        reader.forward();
        final Object[] chompi = scanBlockScalarIndicators(startMark);
        final boolean chomping = ((Boolean) chompi[0]).booleanValue();
        final int increment = ((Integer) chompi[1]).intValue();
        scanBlockScalarIgnoredLine(startMark);
        int minIndent = this.indent + 1;
        if (minIndent < 1) {
            minIndent = 1;
        }
        String breaks = null;
        int maxIndent = 0;
        int ind = 0;
        if (increment == -1) {
            final Object[] brme = scanBlockScalarIndentation();
            breaks = (String) brme[0];
            maxIndent = ((Integer) brme[1]).intValue();
            if (minIndent > maxIndent) {
                ind = minIndent;
            } else {
                ind = maxIndent;
            }
        } else {
            ind = minIndent + increment - 1;
            breaks = scanBlockScalarBreaks(ind);
        }

        String lineBreak = "";
        while (this.reader.getColumn() == ind && reader.peek() != '\0') {
            chunks.append(breaks);
            final boolean leadingNonSpace = BLANK_T.indexOf(reader.peek()) == -1;
            int length = 0;
            while (NULL_OR_LINEBR.indexOf(reader.peek(length)) == -1) {
                length++;
            }
            chunks.append(reader.prefixForward(length));
            // forward(length);
            lineBreak = scanLineBreak();
            breaks = scanBlockScalarBreaks(ind);
            if (this.reader.getColumn() == ind && reader.peek() != '\0') {
                if (folded && lineBreak.equals("\n") && leadingNonSpace
                        && BLANK_T.indexOf(reader.peek()) == -1) {
                    if (breaks.length() == 0) {
                        chunks.append(" ");
                    }
                } else {
                    chunks.append(lineBreak);
                }
            } else {
                break;
            }
        }

        if (chomping) {
            chunks.append(lineBreak);
            chunks.append(breaks);
        }

        return new ScalarToken(chunks.toString(), false, null, null, style);
    }

    private Object[] scanBlockScalarIndicators(Mark startMark) {
        boolean chomping = false;
        int increment = -1;
        char ch = reader.peek();
        if (ch == '-' || ch == '+') {
            chomping = ch == '+';
            reader.forward();
            ch = reader.peek();
            if (Character.isDigit(ch)) {
                increment = Integer.parseInt(("" + ch));
                if (increment == 0) {
                    throw new ScannerException("while scanning a block scalar", startMark,
                            "expected indentation indicator in the range 1-9, but found 0", reader
                                    .getMark(), null);
                }
                reader.forward();
            }
        } else if (Character.isDigit(ch)) {
            increment = Integer.parseInt(("" + ch));
            if (increment == 0) {
                throw new ScannerException("while scanning a block scalar", startMark,
                        "expected indentation indicator in the range 1-9, but found 0", reader
                                .getMark(), null);
            }
            reader.forward();
            ch = reader.peek();
            if (ch == '-' || ch == '+') {
                chomping = ch == '+';
                reader.forward();
            }
        }
        if (NULL_BL_LINEBR.indexOf(reader.peek()) == -1) {
            throw new ScannerException("while scanning a block scalar", startMark,
                    "expected chomping or indentation indicators, but found " + reader.peek() + "("
                            + ((int) reader.peek()) + ")", reader.getMark(), null);
        }
        return new Object[] { Boolean.valueOf(chomping), new Integer(increment) };
    }

    private String scanBlockScalarIgnoredLine(Mark startMark) {
        while (reader.peek() == ' ') {
            reader.forward();
        }
        if (reader.peek() == '#') {
            while (NULL_OR_LINEBR.indexOf(reader.peek()) == -1) {
                reader.forward();
            }
        }
        if (NULL_OR_LINEBR.indexOf(reader.peek()) == -1) {
            throw new ScannerException("while scanning a block scalar", startMark,
                    "expected a comment or a line break, but found " + reader.peek() + "("
                            + ((int) reader.peek()) + ")", reader.getMark(), null);
        }
        return scanLineBreak();
    }

    private Object[] scanBlockScalarIndentation() {
        final StringBuffer chunks = new StringBuffer();
        int maxIndent = 0;
        while (BLANK_OR_LINEBR.indexOf(reader.peek()) != -1) {
            if (reader.peek() != ' ') {
                chunks.append(scanLineBreak());
            } else {
                reader.forward();
                if (this.reader.getColumn() > maxIndent) {
                    maxIndent = reader.getColumn();
                }
            }
        }
        return new Object[] { chunks.toString(), new Integer(maxIndent) };
    }

    private String scanBlockScalarBreaks(final int indent) {
        final StringBuffer chunks = new StringBuffer();
        while (this.reader.getColumn() < indent && reader.peek() == ' ') {
            reader.forward();
        }
        while (FULL_LINEBR.indexOf(reader.peek()) != -1) {
            chunks.append(scanLineBreak());
            while (this.reader.getColumn() < indent && reader.peek() == ' ') {
                reader.forward();
            }
        }
        return chunks.toString();
    }

    private Token scanFlowScalar(final char style) {
        final boolean dbl = style == '"';
        final StringBuffer chunks = new StringBuffer();
        Mark startMark = reader.getMark();
        final char quote = reader.peek();
        reader.forward();
        chunks.append(scanFlowScalarNonSpaces(dbl, startMark));
        while (reader.peek() != quote) {
            chunks.append(scanFlowScalarSpaces(startMark));
            chunks.append(scanFlowScalarNonSpaces(dbl, startMark));
        }
        reader.forward();
        return new ScalarToken(chunks.toString(), false, null, null, style);
    }

    private String scanFlowScalarNonSpaces(final boolean dbl, Mark startMark) {
        final StringBuffer chunks = new StringBuffer();
        for (;;) {
            int length = 0;
            while (SPACES_AND_STUFF.indexOf(reader.peek(length)) == -1) {
                length++;
            }
            if (length != 0) {
                chunks.append(reader.prefixForward(length));
                // forward(length);
            }
            char ch = reader.peek();
            if (!dbl && ch == '\'' && reader.peek(1) == '\'') {
                chunks.append("'");
                reader.forward(2);
            } else if ((dbl && ch == '\'') || (!dbl && DOUBLE_ESC.indexOf(ch) != -1)) {
                chunks.append(ch);
                reader.forward();
            } else if (dbl && ch == '\\') {
                reader.forward();
                ch = reader.peek();
                if (ESCAPE_REPLACEMENTS.containsKey(new Character(ch))) {
                    chunks.append(ESCAPE_REPLACEMENTS.get(new Character(ch)));
                    reader.forward();
                } else if (ESCAPE_CODES.containsKey(new Character(ch))) {
                    length = ((Integer) ESCAPE_CODES.get(new Character(ch))).intValue();
                    reader.forward();
                    final String val = reader.prefix(length);
                    if (NOT_HEXA.matcher(val).find()) {
                        throw new ScannerException("while scanning a double-quoted scalar",
                                startMark, "expected escape sequence of " + length
                                        + " hexadecimal numbers, but found something else: " + val,
                                reader.getMark(), null);
                    }
                    char unicode = (char) Integer.parseInt(val, 16);
                    chunks.append(unicode);
                    reader.forward(length);
                } else if (FULL_LINEBR.indexOf(ch) != -1) {
                    scanLineBreak();
                    chunks.append(scanFlowScalarBreaks(startMark));
                } else {
                    throw new ScannerException("while scanning a double-quoted scalar", startMark,
                            "found unknown escape character " + ch + "(" + ((int) ch) + ")", reader
                                    .getMark(), null);
                }
            } else {
                return chunks.toString();
            }
        }
    }

    private String scanFlowScalarSpaces(Mark startMark) {
        final StringBuffer chunks = new StringBuffer();
        int length = 0;
        while (BLANK_T.indexOf(reader.peek(length)) != -1) {
            length++;
        }
        final String whitespaces = reader.prefixForward(length);
        // forward(length);
        char ch = reader.peek();
        if (ch == '\0') {
            throw new ScannerException("while scanning a quoted scalar", startMark,
                    "found unexpected end of stream", reader.getMark(), null);
        } else if (FULL_LINEBR.indexOf(ch) != -1) {
            final String lineBreak = scanLineBreak();
            final String breaks = scanFlowScalarBreaks(startMark);
            if (!lineBreak.equals("\n")) {
                chunks.append(lineBreak);
            } else if (breaks.length() == 0) {
                chunks.append(" ");
            }
            chunks.append(breaks);
        } else {
            chunks.append(whitespaces);
        }
        return chunks.toString();
    }

    private String scanFlowScalarBreaks(Mark startMark) {
        final StringBuffer chunks = new StringBuffer();
        String pre = null;
        for (;;) {
            pre = reader.prefix(3);
            if ((pre.equals("---") || pre.equals("..."))
                    && NULL_BL_T_LINEBR.indexOf(reader.peek(3)) != -1) {
                throw new ScannerException("while scanning a quoted scalar", startMark,
                        "found unexpected document separator", reader.getMark(), null);
            }
            while (BLANK_T.indexOf(reader.peek()) != -1) {
                reader.forward();
            }
            if (FULL_LINEBR.indexOf(reader.peek()) != -1) {
                chunks.append(scanLineBreak());
            } else {
                return chunks.toString();
            }
        }
    }

    private Token scanPlain() {

        // * See the specification for details. We add an additional restriction
        // * for the flow context: plain scalars in the flow context cannot
        // * contain ',', ':' and '?'. We also keep track of the
        // * `allow_simple_key` flag here. Indentation rules are loosed for the
        // * flow context.

        final StringBuffer chunks = new StringBuffer();
        Mark startMark = reader.getMark();
        final int ind = this.indent + 1;
        String spaces = "";
        boolean f_nzero = true;
        Pattern r_check = R_FLOWNONZERO;
        if (this.flowLevel == 0) {
            f_nzero = false;
            r_check = R_FLOWZERO;
        }
        while (reader.peek() != '#') {
            int length = 0;
            int chunkSize = 32;
            Matcher m = null;
            while (!(m = r_check.matcher(reader.prefix(chunkSize))).find()) {
                chunkSize += 32;
            }
            length = m.start();
            final char ch = reader.peek(length);
            if (f_nzero && ch == ':' && S4.indexOf(reader.peek(length + 1)) == -1) {
                reader.forward(length);
                throw new ScannerException("while scanning a plain scalar", startMark,
                        "found unexpected ':'", reader.getMark(),
                        "Please check http://pyyaml.org/wiki/YAMLColonInFlowContext for details.");
            }
            if (length == 0) {
                break;
            }
            this.allowSimpleKey = false;
            chunks.append(spaces);
            chunks.append(reader.prefixForward(length));
            // forward(length);
            spaces = scanPlainSpaces(ind);
            if (spaces == null || (this.flowLevel == 0 && this.reader.getColumn() < ind)) {
                break;
            }
        }
        return new ScalarToken(chunks.toString(), null, null, true);
    }

    private String scanPlainSpaces(final int indent) {
        final StringBuffer chunks = new StringBuffer();
        int length = 0;
        while (reader.peek(length) == ' ') {
            length++;
        }
        final String whitespaces = reader.prefixForward(length);
        // forward(length);
        char ch = reader.peek();
        if (FULL_LINEBR.indexOf(ch) != -1) {
            final String lineBreak = scanLineBreak();
            this.allowSimpleKey = true;
            if (END_OR_START.matcher(reader.prefix(4)).matches()) {
                return "";
            }
            final StringBuffer breaks = new StringBuffer();
            while (BLANK_OR_LINEBR.indexOf(reader.peek()) != -1) {
                if (' ' == reader.peek()) {
                    reader.forward();
                } else {
                    breaks.append(scanLineBreak());
                    if (END_OR_START.matcher(reader.prefix(4)).matches()) {
                        return "";
                    }
                }
            }
            if (!lineBreak.equals("\n")) {
                chunks.append(lineBreak);
            } else if (breaks == null || breaks.toString().equals("")) {
                chunks.append(" ");
            }
            chunks.append(breaks);
        } else {
            chunks.append(whitespaces);
        }
        return chunks.toString();
    }

    private String scanTagHandle(final String name, Mark startMark) {
        char ch = reader.peek();
        if (ch != '!') {
            throw new ScannerException("while scanning a " + name, startMark,
                    "expected '!', but found " + ch + "(" + ((int) ch) + ")", reader.getMark(),
                    null);
        }
        int length = 1;
        ch = reader.peek(length);
        if (ch != ' ') {
            while (ALPHA.indexOf(ch) != -1) {
                length++;
                ch = reader.peek(length);
            }
            if ('!' != ch) {
                reader.forward(length);
                throw new ScannerException("while scanning a " + name, startMark,
                        "expected '!', but found " + ch + "(" + ((int) ch) + ")", reader.getMark(),
                        null);
            }
            length++;
        }
        final String value = reader.prefixForward(length);
        // forward(length);
        return value;
    }

    private String scanTagUri(final String name, Mark startMark) {
        final StringBuffer chunks = new StringBuffer();
        int length = 0;
        char ch = reader.peek(length);
        while (STRANGE_CHAR.indexOf(ch) != -1) {
            if ('%' == ch) {
                chunks.append(reader.prefixForward(length));
                // forward(length);
                length = 0;
                chunks.append(scanUriEscapes(name, startMark));
            } else {
                length++;
            }
            ch = reader.peek(length);
        }
        if (length != 0) {
            chunks.append(reader.prefixForward(length));
            // forward(length);
        }

        if (chunks.length() == 0) {
            throw new ScannerException("while scanning a " + name, startMark,
                    "expected URI, but found " + ch + "(" + ((int) ch) + ")", reader.getMark(),
                    null);
        }
        return chunks.toString();
    }

    private String scanUriEscapes(final String name, Mark startMark) {
        final StringBuffer bytes = new StringBuffer();
        while (reader.peek() == '%') {
            reader.forward();
            try {
                bytes.append(Integer.parseInt(reader.prefix(2), 16));
            } catch (final NumberFormatException nfe) {
                throw new ScannerException("while scanning a " + name, startMark,
                        "expected URI escape sequence of 2 hexadecimal numbers, but found "
                                + reader.peek(1) + "(" + ((int) reader.peek(1)) + ") and "
                                + reader.peek(2) + "(" + ((int) reader.peek(2)) + ")", reader
                                .getMark(), null);
            }
            reader.forward(2);
        }
        return bytes.toString();
    }

    private String scanLineBreak() {
        // Transforms:
        // '\r\n' : '\n'
        // '\r' : '\n'
        // '\n' : '\n'
        // '\x85' : '\n'
        // default : ''
        final char val = reader.peek();
        if (FULL_LINEBR.indexOf(val) != -1) {
            if (RN.equals(reader.prefix(2))) {
                reader.forward(2);
            } else {
                reader.forward();
            }
            return "\n";
        } else {
            return "";
        }
    }

}
