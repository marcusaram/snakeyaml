/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CollectionEndEvent;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;

/**
 * <pre>
 * Emitter expects events obeying the following grammar:
 * stream ::= STREAM-START document* STREAM-END
 * document ::= DOCUMENT-START node DOCUMENT-END
 * node ::= SCALAR | sequence | mapping
 * sequence ::= SEQUENCE-START node* SEQUENCE-END
 * mapping ::= MAPPING-START (node node)* MAPPING-END
 * </pre>
 * 
 * @see PyYAML 3.06 for more information
 */
public class Emitter {
    private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();

    static {
        ESCAPE_REPLACEMENTS.put(new Character('\0'), "0");
        ESCAPE_REPLACEMENTS.put(new Character('\u0007'), "a");
        ESCAPE_REPLACEMENTS.put(new Character('\u0008'), "b");
        ESCAPE_REPLACEMENTS.put(new Character('\u0009'), "t");
        ESCAPE_REPLACEMENTS.put(new Character('\n'), "n");
        ESCAPE_REPLACEMENTS.put(new Character('\u000B'), "v");
        ESCAPE_REPLACEMENTS.put(new Character('\u000C'), "f");
        ESCAPE_REPLACEMENTS.put(new Character('\r'), "r");
        ESCAPE_REPLACEMENTS.put(new Character('\u001B'), "e");
        ESCAPE_REPLACEMENTS.put(new Character('"'), "\"");
        ESCAPE_REPLACEMENTS.put(new Character('\\'), "\\");
        ESCAPE_REPLACEMENTS.put(new Character('\u0085'), "N");
        ESCAPE_REPLACEMENTS.put(new Character('\u00A0'), "_");
        ESCAPE_REPLACEMENTS.put(new Character('\u2028'), "L");
        ESCAPE_REPLACEMENTS.put(new Character('\u2029'), "P");
    }

    private final static Map<String, String> DEFAULT_TAG_PREFIXES = new HashMap<String, String>();
    static {
        DEFAULT_TAG_PREFIXES.put("!", "!");
        DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
    }
    // The stream should have the methods `write` and possibly `flush`.
    private Writer stream;

    // Encoding is defined by Writer (cannot be overriden by STREAM-START.)
    // private Charset encoding;

    // Emitter is a state machine with a stack of states to handle nested
    // structures.
    private LinkedList<EmitterState> states;
    private EmitterState state;

    // Current event and the event queue.
    private Queue<Event> events;
    private Event event;

    // The current indentation level and the stack of previous indents.
    private LinkedList<Integer> indents;
    private Integer indent;

    // Flow level.
    private int flowLevel;

    // Contexts.
    private boolean mappingContext;
    private boolean simpleKeyContext;

    //
    // Characteristics of the last emitted character:
    // - current position.
    // - is it a whitespace?
    // - is it an indention character
    // (indentation space, '-', '?', or ':')?
    private int line;
    private int column;
    private boolean whitespace;
    private boolean indention;

    // Formatting details.
    private Boolean canonical;
    private int bestIndent;
    private int bestWidth;
    private String bestLineBreak;

    // Tag prefixes.
    private Map<String, String> tagPrefixes;

    // Prepared anchor and tag.
    private String preparedAnchor;
    private String preparedTag;

    // Scalar analysis and style.
    private ScalarAnalysis analysis;
    private char style = 0;

    public Emitter(Writer stream, Dumper opts) {
        // The stream should have the methods `write` and possibly `flush`.
        this.stream = stream;
        // Emitter is a state machine with a stack of states to handle nested
        // structures.
        this.states = new LinkedList<EmitterState>();
        this.state = new ExpectStreamStart();
        // Current event and the event queue.
        this.events = new LinkedList<Event>();
        this.event = null;
        // The current indentation level and the stack of previous indents.
        this.indents = new LinkedList<Integer>();
        this.indent = null;
        // Flow level.
        this.flowLevel = 0;
        // Contexts.
        mappingContext = false;
        simpleKeyContext = false;

        //
        // Characteristics of the last emitted character:
        // - current position.
        // - is it a whitespace?
        // - is it an indention character
        // (indentation space, '-', '?', or ':')?
        line = 0;
        column = 0;
        whitespace = true;
        indention = true;

        // Formatting details.
        this.canonical = opts.isCanonical();
        this.bestIndent = 2;
        if ((opts.getIndent() > 1) && (opts.getIndent() < 10)) {
            this.bestIndent = opts.getIndent();
        }
        this.bestWidth = 80;
        if (opts.getWidth() > this.bestIndent * 2) {
            this.bestWidth = opts.getWidth();
        }
        this.bestLineBreak = "\n";
        if (opts.getLineBreak().equals("\n") || opts.getLineBreak().equals("\r")
                || opts.getLineBreak().equals("\r\n")) {
            this.bestLineBreak = opts.getLineBreak();
        }

        // Tag prefixes.
        this.tagPrefixes = new HashMap<String, String>();

        // Prepared anchor and tag.
        this.preparedAnchor = null;
        this.preparedTag = null;

        // Scalar analysis and style.
        this.analysis = null;
        this.style = (char) 0;
    }

    public void emit(final Event event) throws IOException {
        this.events.offer(event);
        while (!needMoreEvents()) {
            this.event = this.events.poll();
            this.state.expect();
            this.event = null;
        }
    }

    // In some cases, we wait for a few next events before emitting.

    private boolean needMoreEvents() {
        if (events.isEmpty()) {
            return true;
        }
        Event event = events.peek();
        if (event instanceof DocumentStartEvent) {
            return needEvents(1);
        } else if (event instanceof SequenceStartEvent) {
            return needEvents(2);
        } else if (event instanceof MappingStartEvent) {
            return needEvents(3);
        } else {
            return false;
        }
    }

    private boolean needEvents(int count) {
        int level = 0;
        Iterator<Event> iter = events.iterator();
        iter.next();
        while (iter.hasNext()) {
            Event event = iter.next();
            if (event instanceof DocumentStartEvent || event instanceof CollectionStartEvent) {
                level++;
            } else if (event instanceof DocumentEndEvent || event instanceof CollectionEndEvent) {
                level--;
            } else if (event instanceof StreamEndEvent) {
                level = -1;
            }
            if (level < 0) {
                return false;
            }
        }
        return events.size() < count + 1;
    }

    private void increaseIndent(boolean flow, boolean indentless) {
        indents.push(indent);
        if (indent == null) {
            if (flow) {
                indent = bestIndent;
            } else {
                indent = 0;
            }
        } else if (!indentless) {
            this.indent += bestIndent;
        }
    }

    // States

    // Stream handlers.

    private class ExpectStreamStart implements EmitterState {
        public void expect() throws IOException {
            if (event instanceof StreamStartEvent) {
                writeStreamStart();
                state = new ExpectFirstDocumentStart();
            } else {
                throw new EmitterException("expected StreamStartEvent, but got " + event);
            }
        }
    }

    private class ExpectNothing implements EmitterState {
        public void expect() throws IOException {
            throw new EmitterException("expecting nothing, but got " + event);
        }
    }

    // Document handlers.

    private class ExpectFirstDocumentStart implements EmitterState {
        public void expect() throws IOException {
            new ExpectDocumentStart(true).expect();
        }
    }

    private class ExpectDocumentStart implements EmitterState {
        private boolean first;

        public ExpectDocumentStart(boolean first) {
            this.first = first;
        }

        public void expect() throws IOException {
            if (event instanceof DocumentStartEvent) {
                DocumentStartEvent ev = (DocumentStartEvent) event;
                if (ev.getVersion() != null) {
                    String versionText = prepareVersion(ev.getVersion());
                    writeVersionDirective(versionText);
                }
                tagPrefixes = new HashMap<String, String>(DEFAULT_TAG_PREFIXES);
                if (ev.getTags() != null) {
                    Set<String> handles = new TreeSet<String>(ev.getTags().keySet());
                    for (String handle : handles) {
                        String prefix = ev.getTags().get(handle);
                        tagPrefixes.put(prefix, handle);
                        String handleText = prepareTagHandle(handle);
                        String prefixText = prepareTagPrefix(prefix);
                        writeTagDirective(handleText, prefixText);
                    }
                }
                boolean implicit = first && !ev.getExplicit() && !canonical
                        && ev.getVersion() == null && ev.getTags() == null && !checkEmptyDocument();
                if (!implicit) {
                    writeIndent();
                    writeIndicator("---", true, false, false);
                    if (canonical) {
                        writeIndent();
                    }
                }
                state = new ExpectDocumentRoot();
            } else if (event instanceof StreamEndEvent) {
                writeStreamEnd();
                state = new ExpectNothing();
            } else {
                throw new EmitterException("expected DocumentStartEvent, but got " + event);
            }
        }
    }

    private class ExpectDocumentEnd implements EmitterState {
        public void expect() throws IOException {
            if (event instanceof DocumentEndEvent) {
                writeIndent();
                if (((DocumentEndEvent) event).getExplicit()) {
                    writeIndicator("...", true, false, false);
                    writeIndent();
                }
                flushStream();
                state = new ExpectDocumentStart(false);
            } else {
                throw new EmitterException("expected DocumentEndEvent, but got " + event);
            }
        }
    }

    private class ExpectDocumentRoot implements EmitterState {
        public void expect() throws IOException {
            states.push(new ExpectDocumentEnd());
            expectNode(true, false, false, false);
        }
    }

    // Node handlers.

    private void expectNode(boolean root, boolean sequence, boolean mapping, boolean simpleKey)
            throws IOException {
        mappingContext = mapping;
        simpleKeyContext = simpleKey;
        if (event instanceof AliasEvent) {
            expectAlias();
        } else if (event instanceof ScalarEvent || event instanceof CollectionStartEvent) {
            processAnchor("&");
            processTag();
            if (event instanceof ScalarEvent) {
                expectScalar();
            } else if (event instanceof SequenceStartEvent) {
                if (flowLevel != 0 || canonical || ((SequenceStartEvent) event).getFlowStyle()
                        || checkEmptySequence()) {
                    expectFlowSequence();
                } else {
                    expectBlockSequence();
                }
            } else if (event instanceof MappingStartEvent) {
                if (flowLevel != 0 || canonical || ((MappingStartEvent) event).getFlowStyle()
                        || checkEmptyMapping()) {
                    expectFlowMapping();
                } else {
                    expectBlockMapping();
                }
            }
        } else {
            throw new EmitterException("expected NodeEvent, but got " + event);
        }
    }

    private void expectAlias() throws IOException {
        if (((NodeEvent) event).getAnchor() == null) {
            throw new EmitterException("anchor is not specified for alias");
        }
        processAnchor("*");
        state = states.pop();
    }

    private void expectScalar() throws IOException {
        increaseIndent(true, false);
        processScalar();
        indent = indents.pop();
        state = states.pop();
    }

    // Flow sequence handlers.

    private void expectFlowSequence() throws IOException {
        writeIndicator("[", true, true, false);
        flowLevel++;
        increaseIndent(true, false);
        state = new ExpectFirstFlowSequenceItem();
    }

    private class ExpectFirstFlowSequenceItem implements EmitterState {
        public void expect() throws IOException {
            if (event instanceof SequenceEndEvent) {
                indent = indents.pop();
                flowLevel--;
                writeIndicator("]", false, false, false);
                state = states.pop();
            } else {
                if (canonical || column > bestWidth) {
                    writeIndent();
                }
                states.push(new ExpectFlowSequenceItem());
                expectNode(false, true, false, false);
            }
        }
    }

    private class ExpectFlowSequenceItem implements EmitterState {
        public void expect() throws IOException {
            if (event instanceof SequenceEndEvent) {
                indent = indents.pop();
                flowLevel--;
                if (canonical) {
                    writeIndicator(",", false, false, false);
                    writeIndent();
                }
                writeIndicator("]", false, false, false);
                state = states.pop();
            } else {
                writeIndicator(",", false, false, false);
                if (canonical || column > bestWidth) {
                    writeIndent();
                }
                states.push(new ExpectFlowSequenceItem());
                expectNode(false, true, false, false);
            }
        }
    }

    // Flow mapping handlers.

    private void expectFlowMapping() throws IOException {
        writeIndicator("{", true, true, false);
        flowLevel++;
        increaseIndent(true, false);
        state = new ExpectFirstFlowMappingKey();
    }

    private class ExpectFirstFlowMappingKey implements EmitterState {
        public void expect() throws IOException {
            if (event instanceof MappingEndEvent) {
                indent = indents.pop();
                flowLevel--;
                writeIndicator("}", false, false, false);
                state = states.pop();
            } else {
                if (canonical || column > bestWidth) {
                    writeIndent();
                }
                if (!canonical && checkSimpleKey()) {
                    states.push(new ExpectFlowMappingSimpleValue());
                    expectNode(false, false, true, true);
                } else {
                    writeIndicator("?", true, false, false);
                    states.push(new ExpectFlowMappingValue());
                    expectNode(false, false, true, false);
                }
            }
        }
    }

    private class ExpectFlowMappingKey implements EmitterState {
        public void expect() throws IOException {
            if (event instanceof MappingEndEvent) {
                indent = indents.pop();
                flowLevel--;
                if (canonical) {
                    writeIndicator(",", false, false, false);
                    writeIndent();
                }
                writeIndicator("}", false, false, false);
                state = states.pop();
            } else {
                writeIndicator(",", false, false, false);
                if (canonical || column > bestWidth) {
                    writeIndent();
                }
                if (!canonical && checkSimpleKey()) {
                    states.push(new ExpectFlowMappingSimpleValue());
                    expectNode(false, false, true, true);
                } else {
                    writeIndicator("?", true, false, false);
                    states.push(new ExpectFlowMappingValue());
                    expectNode(false, false, true, false);
                }
            }
        }
    }

    private class ExpectFlowMappingSimpleValue implements EmitterState {
        public void expect() throws IOException {
            writeIndicator(":", false, false, false);
            states.push(new ExpectFlowMappingKey());
            expectNode(false, false, true, false);
        }
    }

    private class ExpectFlowMappingValue implements EmitterState {
        public void expect() throws IOException {
            if (canonical || column > bestWidth) {
                writeIndent();
            }
            writeIndicator(":", true, false, false);
            states.push(new ExpectFlowMappingKey());
            expectNode(false, false, true, false);
        }
    }

    // Block sequence handlers.

    private void expectBlockSequence() throws IOException {
        boolean indentless = (mappingContext && !indention);
        increaseIndent(false, indentless);
        state = new ExpectFirstBlockSequenceItem();
    }

    private class ExpectFirstBlockSequenceItem implements EmitterState {
        public void expect() throws IOException {
            new ExpectBlockSequenceItem(true).expect();
        }
    }

    private class ExpectBlockSequenceItem implements EmitterState {
        private boolean first;

        public ExpectBlockSequenceItem(boolean first) {
            this.first = first;
        }

        public void expect() throws IOException {
            if (!this.first && event instanceof SequenceEndEvent) {
                indent = indents.pop();
                state = states.pop();
            } else {
                writeIndent();
                writeIndicator("-", true, false, true);
                states.push(new ExpectBlockSequenceItem(false));
                expectNode(false, true, false, false);
            }
        }
    }

    // Block mapping handlers.
    private void expectBlockMapping() throws IOException {
        increaseIndent(false, false);
        state = new ExpectFirstBlockMappingKey();
    }

    private class ExpectFirstBlockMappingKey implements EmitterState {
        public void expect() throws IOException {
            new ExpectBlockMappingKey(true).expect();
        }
    }

    private class ExpectBlockMappingKey implements EmitterState {
        private boolean first;

        public ExpectBlockMappingKey(boolean first) {
            this.first = first;
        }

        public void expect() throws IOException {
            if (!this.first && event instanceof MappingEndEvent) {
                indent = indents.pop();
                state = states.pop();
            } else {
                writeIndent();
                if (checkSimpleKey()) {
                    states.push(new ExpectBlockMappingSimpleValue());
                    expectNode(false, false, true, true);
                } else {
                    writeIndicator("?", true, false, true);
                    states.push(new ExpectBlockMappingValue());
                    expectNode(false, false, true, false);
                }
            }
        }
    }

    private class ExpectBlockMappingSimpleValue implements EmitterState {
        public void expect() throws IOException {
            writeIndicator(":", false, false, false);
            states.push(new ExpectBlockMappingKey(false));
            expectNode(false, false, true, false);
        }
    }

    private class ExpectBlockMappingValue implements EmitterState {
        public void expect() throws IOException {
            writeIndent();
            writeIndicator(":", true, false, true);
            states.push(new ExpectBlockMappingKey(false));
            expectNode(false, false, true, false);
        }
    }

    // Checkers.

    private boolean checkEmptySequence() {
        return (event instanceof SequenceStartEvent && !events.isEmpty() && events.peek() instanceof SequenceEndEvent);
    }

    private boolean checkEmptyMapping() {
        return (event instanceof MappingStartEvent && !events.isEmpty() && events.peek() instanceof MappingEndEvent);
    }

    private boolean checkEmptyDocument() {
        if (!(event instanceof DocumentStartEvent) || events.isEmpty()) {
            return false;
        }
        Event event = events.peek();
        if (event instanceof ScalarEvent) {
            ScalarEvent e = (ScalarEvent) event;
            return (e.getAnchor() == null && e.getTag() == null && e.getImplicit() != null && e
                    .getValue() == "");
        } else {
            return false;
        }
    }

    private boolean checkSimpleKey() {
        int length = 0;
        if (event instanceof NodeEvent && ((NodeEvent) event).getAnchor() != null) {
            if (preparedAnchor == null) {
                preparedAnchor = prepareAnchor(((NodeEvent) event).getAnchor());
            }
            length += preparedAnchor.length();
        }
        String tag = null;
        if (event instanceof ScalarEvent) {
            tag = ((ScalarEvent) event).getTag();
        } else if (event instanceof CollectionStartEvent) {
            tag = ((CollectionStartEvent) event).getTag();
        }
        if (tag != null) {
            if (preparedTag == null) {
                preparedTag = prepareTag(tag);
            }
            length += preparedTag.length();
        }
        if (event instanceof ScalarEvent) {
            if (analysis == null) {
                analysis = analyzeScalar(((ScalarEvent) event).getValue());
            }
            length += analysis.scalar.length();
        }
        return (length < 128 && (event instanceof AliasEvent
                || (event instanceof ScalarEvent && !analysis.empty && !analysis.multiline)
                || checkEmptySequence() || checkEmptyMapping()));
    }

    // Anchor, Tag, and Scalar processors.

    private void processAnchor(String indicator) throws IOException {
        NodeEvent ev = (NodeEvent) event;
        if (ev.getAnchor() == null) {
            preparedAnchor = null;
            return;
        }
        if (preparedAnchor == null) {
            preparedAnchor = prepareAnchor(ev.getAnchor());
        }
        if (preparedAnchor != null && !"".equals(preparedAnchor)) {
            writeIndicator(indicator + preparedAnchor, true, false, false);
        }
        preparedAnchor = null;
    }

    private void processTag() throws IOException {
        String tag = null;
        if (event instanceof ScalarEvent) {
            ScalarEvent ev = (ScalarEvent) event;
            tag = ev.getTag();
            if (style == 0) {
                style = chooseScalarStyle();
            }
            if (((!canonical || tag == null) && ((style == 0 && ev.getImplicit()[0]) || (style != 0 && ev
                    .getImplicit()[1])))) {
                preparedTag = null;
                return;
            }
            if (ev.getImplicit()[0] && tag == null) {
                tag = "!";
                preparedTag = null;
            }
        } else {
            CollectionStartEvent ev = (CollectionStartEvent) event;
            tag = ev.getTag();
            if ((!canonical || tag == null) && ev.getImplicit()) {
                preparedTag = null;
                return;
            }
        }
        if (tag == null) {
            throw new EmitterException("tag is not specified");
        }
        if (preparedTag == null) {
            preparedTag = prepareTag(tag);
        }
        if (preparedTag != null && !"".equals(preparedTag)) {
            writeIndicator(preparedTag, true, false, false);
        }
        preparedTag = null;
    }

    private char chooseScalarStyle() {
        ScalarEvent ev = (ScalarEvent) event;
        if (analysis == null) {
            analysis = analyzeScalar(ev.getValue());
        }
        if (ev.getStyle() == '"' || this.canonical) {
            return '"';
        }
        if (ev.getStyle() == 0 && ev.getImplicit()[0]) {
            if (!(simpleKeyContext && (analysis.empty || analysis.multiline))
                    && ((flowLevel != 0 && analysis.allowFlowPlain) || (flowLevel == 0 && analysis.allowBlockPlain))) {
                return 0;
            }
        }
        if (ev.getStyle() == '|' || ev.getStyle() == '>') {
            if (flowLevel == 0 && !simpleKeyContext && analysis.allowBlock) {
                return ev.getStyle();
            }
        }
        if (ev.getStyle() == 0 || ev.getStyle() == '\'') {
            if (analysis.allowSingleQuoted && !(simpleKeyContext && analysis.multiline)) {
                return '\'';
            }
        }
        return '"';
    }

    private void processScalar() throws IOException {
        ScalarEvent ev = (ScalarEvent) event;
        if (analysis == null) {
            analysis = analyzeScalar(ev.getValue());
        }
        if (style == 0) {
            style = chooseScalarStyle();
        }
        boolean split = !simpleKeyContext;
        if (style == '"') {
            writeDoubleQuoted(analysis.scalar, split);
        } else if (style == '\'') {
            writeSingleQuoted(analysis.scalar, split);
        } else if (style == '>') {
            writeFolded(analysis.scalar);
        } else if (style == '|') {
            writeLiteral(analysis.scalar);
        } else {
            writePlain(analysis.scalar, split);
        }
        analysis = null;
        style = 0;
    }

    // Analyzers.

    private String prepareVersion(final Integer[] version) {
        Integer major = version[0];
        Integer minor = version[1];
        if (major != 1) {
            throw new EmitterException("unsupported YAML version: " + version[0] + "." + version[1]);
        }
        return major.toString() + "." + minor.toString();
    }

    private final static Pattern HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");

    private String prepareTagHandle(String handle) {
        if (handle == null || "".equals(handle)) {
            throw new EmitterException("tag handle must not be empty");
        } else if (handle.charAt(0) != '!' || handle.charAt(handle.length() - 1) != '!') {
            throw new EmitterException("tag handle must start and end with '!': " + handle);
        } else if (!"!".equals(handle) && !HANDLE_FORMAT.matcher(handle).matches()) {
            throw new EmitterException("invalid character in the tag handle: " + handle);
        }
        return handle;
    }

    private String prepareTagPrefix(String prefix) {
        if (prefix == null || "".equals(prefix)) {
            throw new EmitterException("tag prefix must not be empty");
        }
        StringBuffer chunks = new StringBuffer();
        int start = 0;
        int end = 0;
        if (prefix.charAt(0) == '!') {
            end = 1;
        }
        while (end < prefix.length()) {
            // TODO unclear what is written in PyYAML
            end++;
        }
        if (start < end) {
            chunks.append(prefix.substring(start, end));
        }
        return chunks.toString();
    }

    private String prepareTag(final String tag) {
        if (tag == null || "".equals(tag)) {
            throw new EmitterException("tag must not be empty");
        }
        if (tag.equals("!")) {
            return tag;
        }
        String handle = null;
        String suffix = tag;
        for (String prefix : tagPrefixes.keySet()) {
            if (tag.startsWith(prefix) && (prefix.equals("!") || prefix.length() < tag.length())) {
                handle = tagPrefixes.get(prefix);
                suffix = tag.substring(prefix.length());
            }
        }
        StringBuffer chunks = new StringBuffer();
        int start = 0;
        int end = 0;
        while (end < suffix.length()) {
            // TODO unclear PYYAML code
            end++;
        }
        if (start < end) {
            chunks.append(suffix.substring(start, end));
        }
        String suffixText = chunks.toString();
        if (handle != null) {
            return handle + suffixText;
        } else {
            return "!<" + suffixText + ">";
        }
    }

    private final static Pattern ANCHOR_FORMAT = Pattern.compile("^[-_\\w]*$");

    static String prepareAnchor(String anchor) {
        if (anchor == null || "".equals(anchor)) {
            throw new EmitterException("anchor must not be empty");
        }
        if (!ANCHOR_FORMAT.matcher(anchor).matches()) {
            throw new EmitterException("invalid character in the anchor: " + anchor);
        }
        return anchor;
    }

    private ScalarAnalysis analyzeScalar(final String scalar) {
        // Empty scalar is a special case.
        if (scalar == null || "".equals(scalar)) {
            return new ScalarAnalysis(scalar, true, false, false, true, true, true, false);
        }
        // Indicators and special characters.
        boolean blockIndicators = false;
        boolean flowIndicators = false;
        boolean lineBreaks = false;
        boolean specialCharacters = false;

        // Whitespaces.
        boolean leadingSpaces = false; // ^ space+ (non-space | $)
        boolean leadingBreaks = false; // ^ break+ (non-space | $)
        boolean trailingSpaces = false; // (^ | non-space) space+ $
        boolean trailingBreaks = false; // (^ | non-space) break+ $
        boolean inlineBreaksSpaces = false; // non-space break+ space+ non-space
        boolean mixedBreaksSpaces = false; // anything else
        // Check document indicators.
        if (scalar.startsWith("---") || scalar.startsWith("...")) {
            blockIndicators = true;
            flowIndicators = true;
        }
        // First character or preceded by a whitespace.
        boolean preceededBySpace = true;
        boolean followedBySpace = (scalar.length() == 1 || "\0 \t\r\n\u0085\u2029\u2029"
                .indexOf(scalar.charAt(1)) != -1);
        // The current series of whitespaces contain plain spaces.
        boolean spaces = false;
        // The current series of whitespaces contain line breaks.
        boolean breaks = false;
        // The current series of whitespaces contain a space followed by a
        // break.
        boolean mixed = false;
        // The current series of whitespaces start at the beginning of the
        // scalar.
        boolean leading = false;

        int index = 0;

        while (index < scalar.length()) {
            char ch = scalar.charAt(index);
            // Check for indicators.
            if (index == 0) {
                // Leading indicators are special characters.
                if ("#,[]{}&*!|>\'\"%@`".indexOf(ch) != -1) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
                if (ch == '?' || ch == ':') {
                    flowIndicators = true;
                    if (followedBySpace) {
                        blockIndicators = true;
                    }
                }
                if (ch == '-' && followedBySpace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            } else {
                // Some indicators cannot appear within a scalar as well.
                if (",?[]{}".indexOf(ch) != -1) {
                    flowIndicators = true;
                }
                if (ch == ':') {
                    flowIndicators = true;
                    if (followedBySpace) {
                        blockIndicators = true;
                    }
                }
                if (ch == '#' && preceededBySpace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            }
            // Check for line breaks, special, and unicode characters.
            if (ch == '\n' || ch == '\u0085' || ch == '\u2028' || ch == '\u2029') {
                lineBreaks = true;
            }
            if (!(ch == '\n' || ('\u0020' <= ch && ch <= '\u007E'))) {
                if ((ch == '\u0085' || ('\u00A0' <= ch && ch <= '\uD7FF') || ('\uE000' <= ch && ch <= '\uFFFD'))
                        && (ch != '\uFEFF')) {
                    // unicode is used
                } else {
                    specialCharacters = true;
                }
            }
            // Spaces, line breaks, and how they are mixed. State machine.

            // Start or continue series of whitespaces.
            if (" \n\u0085\u2028\u2029".indexOf(ch) != -1) {
                if (spaces && breaks) { // break+ (space+ break+) => mixed
                    if (ch != ' ') {
                        mixed = true;
                    }
                } else if (spaces) { // (space+ break+) => mixed
                    if (ch != ' ') {
                        breaks = true;
                        mixed = true;
                    }
                } else if (breaks) { // break+ space+
                    if (ch == ' ') {
                        spaces = true;
                    }
                } else {
                    leading = (index == 0);
                    if (ch == ' ') { // space+
                        spaces = true;
                    } else { // break+
                        breaks = true;
                    }
                }
                // Series of whitespaces ended with a non-space.
            } else if (spaces || breaks) {
                if (leading) {
                    if (spaces && breaks) {
                        mixedBreaksSpaces = true;
                    } else if (spaces) {
                        leadingSpaces = true;
                    } else if (breaks) {
                        leadingBreaks = true;
                    }
                } else {
                    if (mixed) {
                        mixedBreaksSpaces = true;
                    } else if (spaces && breaks) {
                        inlineBreaksSpaces = true;
                    }
                }
                spaces = breaks = mixed = leading = false;
            }
            // Series of whitespaces reach the end.
            if ((spaces || breaks) && (index == scalar.length() - 1)) {
                if (spaces && breaks) {
                    mixedBreaksSpaces = true;
                } else if (spaces) {
                    trailingSpaces = true;
                    if (leading) {
                        leadingSpaces = true;
                    }
                } else if (breaks) {
                    trailingBreaks = true;
                    if (leading) {
                        leadingBreaks = true;
                    }
                }
                spaces = breaks = mixed = leading = false;
            }
            // Prepare for the next character.
            index++;
            preceededBySpace = "\0 \t\r\n\u0085\u2028\u2029".indexOf(ch) != -1;
            followedBySpace = (index + 1 >= scalar.length() || "\0 \t\r\n\u0085\u2028\u2029"
                    .indexOf(scalar.charAt(index + 1)) != -1);
        }
        // Let's decide what styles are allowed.
        boolean allowFlowPlain = true;
        boolean allowBlockPlain = true;
        boolean allowSingleQuoted = true;
        boolean allowDoubleQuoted = true;
        boolean allowBlock = true;
        // Leading and trailing whitespace are bad for plain scalars. We also
        // do not want to mess with leading whitespaces for block scalars.
        if (leadingSpaces || leadingBreaks || trailingSpaces) {
            allowFlowPlain = allowBlockPlain = allowBlock = false;
        }
        // Trailing breaks are fine for block scalars, but unacceptable for
        // plain scalars.
        if (trailingBreaks) {
            allowFlowPlain = allowBlockPlain = false;
        }
        // The combination of (space+ break+) is only acceptable for block
        // scalars.
        if (inlineBreaksSpaces) {
            allowFlowPlain = allowBlockPlain = allowSingleQuoted = false;
        }
        // Mixed spaces and breaks, as well as special character are only
        // allowed for double quoted scalars.
        if (mixedBreaksSpaces || specialCharacters) {
            allowFlowPlain = allowBlockPlain = allowSingleQuoted = allowBlock = false;
        }
        // We don't emit multiline plain scalars.
        if (lineBreaks) {
            allowFlowPlain = allowBlockPlain = false;
        }
        // Flow indicators are forbidden for flow plain scalars.
        if (flowIndicators) {
            allowFlowPlain = false;
        }
        // Block indicators are forbidden for block plain scalars.
        if (blockIndicators) {
            allowBlockPlain = false;
        }

        return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain,
                allowSingleQuoted, allowDoubleQuoted, allowBlock);
    }

    // Writers.

    void flushStream() throws IOException {
        stream.flush();
    }

    void writeStreamStart() {
        // BOM is written by Writer.
    }

    void writeStreamEnd() throws IOException {
        flushStream();
    }

    void writeIndicator(String indicator, boolean needWhitespace, boolean whitespace,
            boolean indentation) throws IOException {
        String data = null;
        if (this.whitespace || !needWhitespace) {
            data = indicator;
        } else {
            data = " " + indicator;
        }
        this.whitespace = whitespace;
        this.indention = this.indention && indentation;
        this.column += data.length();
        stream.write(data);
    }

    void writeIndent() throws IOException {
        int indent;
        if (this.indent != null) {
            indent = this.indent;
        } else {
            indent = 0;
        }

        if (!this.indention || this.column > indent || (this.column == indent && !this.whitespace)) {
            writeLineBreak(null);
        }

        if (this.column < indent) {
            this.whitespace = true;
            StringBuffer data = new StringBuffer();
            for (int i = 0; i < indent - this.column; i++) {
                data.append(" ");
            }
            this.column = indent;
            stream.write(data.toString());
        }
    }

    private void writeLineBreak(String data) throws IOException {
        if (data == null) {
            data = this.bestLineBreak;
        }
        this.whitespace = true;
        this.indention = true;
        this.line++;
        this.column = 0;
        stream.write(data);
    }

    void writeVersionDirective(String versionText) throws IOException {
        stream.write("%YAML " + versionText);
        writeLineBreak(null);
    }

    void writeTagDirective(String handleText, String prefixText) throws IOException {
        stream.write("%TAG " + handleText + " " + prefixText);
        writeLineBreak(null);
    }

    // Scalar streams.
    private void writeSingleQuoted(String text, boolean split) throws IOException {
        writeIndicator("'", true, false, false);
        boolean spaces = false;
        boolean breaks = false;
        int start = 0, end = 0;
        char ch;
        while (end <= text.length()) {
            ch = 0;
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (spaces) {
                if (ch == 0 || ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split && start != 0
                            && end != text.length()) {
                        writeIndent();
                    } else {
                        String data = text.substring(start, end);
                        this.column += data.length();
                        stream.write(data);
                    }
                    start = end;
                }
            } else if (breaks) {
                if (ch == 0 || "\n\u0085\u2028\u2029".indexOf(ch) == -1) {
                    if (text.charAt(start) == '\n') {
                        writeLineBreak(null);
                    }
                    String data = text.substring(start, end);
                    for (int i = 0; i < data.length(); i++) {
                        char br = data.charAt(i);
                        if (br == '\n') {
                            writeLineBreak(null);
                        } else {
                            writeLineBreak(String.valueOf(br));
                        }
                    }
                    writeIndent();
                    start = end;
                }
            } else {
                if (ch == 0 || " \n\u0085\u2028\u2029".indexOf(ch) != -1 || ch == '\'') {
                    if (start < end) {
                        String data = text.substring(start, end);
                        this.column += data.length();
                        stream.write(data);
                        start = end;
                    }
                }
            }
            if (ch == '\'') {
                String data = "''";
                this.column += 2;
                stream.write(data);
                start = end + 1;
            }
            if (ch != 0) {
                spaces = ch == ' ';
                breaks = "\n\u0085\u2028\u2029".indexOf(ch) != -1;
            }
            end++;
        }
        writeIndicator("'", false, false, false);
    }

    private void writeDoubleQuoted(final String text, final boolean split) throws IOException {
        writeIndicator("\"", true, false, false);
        int start = 0;
        int end = 0;
        while (end <= text.length()) {
            char ch = 0;
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (ch == 0 || "\"\\\u0085\u2028\u2029\uFEFF".indexOf(ch) != -1
                    || !('\u0020' <= ch && ch <= '\u007E')) {
                if (start < end) {
                    String data = text.substring(start, end);
                    this.column += data.length();
                    stream.write(data);
                    start = end;
                }
                if (ch != 0) {
                    String data;
                    if (ESCAPE_REPLACEMENTS.containsKey(new Character(ch))) {
                        data = "\\" + ESCAPE_REPLACEMENTS.get(new Character(ch));
                    } else if (ch <= '\u00FF') {
                        String s = "0" + Integer.toString(ch, 16);
                        data = "\\x" + s.substring(s.length() - 2);
                    } else {
                        String s = "000" + Integer.toString(ch, 16);
                        data = "\\u" + s.substring(s.length() - 4);
                    }
                    this.column += data.length();
                    stream.write(data);
                    start = end + 1;
                }
            }
            if ((0 < end && end < (text.length() - 1)) && (ch == ' ' || start >= end)
                    && (this.column + (end - start)) > this.bestWidth && split) {
                String data = text.substring(start, end) + "\\";
                if (start < end) {
                    start = end;
                }
                this.column += data.length();
                stream.write(data);
                this.column += data.length();
                stream.write(data);
                writeIndent();
                this.whitespace = false;
                this.indention = false;
                if (text.charAt(start) == ' ') {
                    data = "\\";
                    this.column += data.length();
                    stream.write(data);
                }
            }
            end += 1;
        }
        writeIndicator("\"", false, false, false);
    }

    private String determineChomp(String text) {
        String tail = text.substring(text.length() - 2);
        while (tail.length() < 2) {
            tail = " " + tail;
        }
        char ch1 = tail.charAt(tail.length() - 1);
        char ch2 = tail.charAt(tail.length() - 2);
        if ("\n\0085\u2028\u2029".indexOf(ch1) != -1) {
            if ("\n\0085\u2028\u2029".indexOf(ch2) != -1) {
                return "+";
            } else {
                return "";
            }
        } else {
            return "-";
        }
    }

    void writeFolded(String text) throws IOException {
        String chomp = determineChomp(text);
        writeIndicator(">" + chomp, true, false, false);
        writeIndent();
        boolean leadingSpace = false;
        boolean spaces = false;
        boolean breaks = false;
        int start = 0, end = 0;
        while (end <= text.length()) {
            char ch = 0;
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (breaks) {
                if (ch == 0 || ("\n\0085\u2028\u2029".indexOf(ch) == -1)) {
                    if (!leadingSpace && ch != 0 && ch != ' ' && text.charAt(start) == '\n') {
                        writeLineBreak(null);
                    }
                    leadingSpace = (ch == ' ');
                    String data = text.substring(start, end);
                    for (int i = 0; i < data.length(); i++) {
                        char br = data.charAt(i);
                        if (br == '\n') {
                            writeLineBreak(null);
                        } else {
                            writeLineBreak(String.valueOf(br));
                        }
                    }
                    if (ch != 0) {
                        writeIndent();
                    }
                    start = end;
                }
            } else if (spaces) {
                if (ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth) {
                        writeIndent();
                    } else {
                        String data = text.substring(start, end);
                        this.column += data.length();
                        stream.write(data);
                    }
                    start = end;
                }
            } else {
                if (ch == 0 || " \n\0085\u2028\u2029".indexOf(ch) != -1) {
                    String data = text.substring(start, end);
                    stream.write(data);
                    if (ch == 0) {
                        writeLineBreak(null);
                    }
                    start = end;
                }
            }
            if (ch != 0) {
                breaks = ("\n\0085\u2028\u2029".indexOf(ch) != -1);
                spaces = (ch == ' ');
            }
            end++;
        }
    }

    void writeLiteral(String text) throws IOException {
        String chomp = determineChomp(text);
        writeIndicator("|" + chomp, true, false, false);
        writeIndent();
        boolean breaks = false;
        int start = 0, end = 0;
        while (end <= text.length()) {
            char ch = 0;
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (breaks) {
                if (ch == 0 || "\n\u0085\u2028\u2029".indexOf(ch) == -1) {
                    String data = text.substring(start, end);
                    for (int i = 0; i < data.length(); i++) {
                        char br = data.charAt(i);
                        if (br == '\n') {
                            writeLineBreak(null);
                        } else {
                            writeLineBreak(String.valueOf(br));
                        }
                    }
                    if (ch != 0) {
                        writeIndent();
                    }
                    start = end;
                }
            } else {
                if (ch == 0 || "\n\u0085\u2028\u2029".indexOf(ch) != -1) {
                    String data = text.substring(start, end);
                    stream.write(data);
                    if (ch == 0) {
                        writeLineBreak(null);
                    }
                    start = end;
                }
            }
            if (ch != 0) {
                breaks = ("\n\u0085\u2028\u2029".indexOf(ch) != -1);
            }
            end++;
        }
    }

    void writePlain(String text, boolean split) throws IOException {
        if (text == null || "".equals(text)) {
            return;
        }
        if (!this.whitespace) {
            String data = " ";
            this.column += data.length();
            stream.write(data);
        }
        this.whitespace = false;
        this.indention = false;
        boolean spaces = false;
        boolean breaks = false;
        int start = 0, end = 0;
        while (end <= text.length()) {
            char ch = 0;
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (spaces) {
                if (ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split) {
                        writeIndent();
                        this.whitespace = false;
                        this.indention = false;
                    } else {
                        String data = text.substring(start, end);
                        this.column += data.length();
                        stream.write(data);
                    }
                    start = end;
                }
            } else if (breaks) {
                if ("\n\u0085\u2028\u2029".indexOf(ch) == -1) {
                    if (text.charAt(start) == '\n') {
                        writeLineBreak(null);
                    }
                    String data = text.substring(start, end);
                    for (int i = 0; i < data.length(); i++) {
                        char br = data.charAt(i);
                        if (br == '\n') {
                            writeLineBreak(null);
                        } else {
                            writeLineBreak(String.valueOf(br));
                        }
                    }
                    writeIndent();
                    this.whitespace = false;
                    this.indention = false;
                    start = end;
                }
            } else {
                if (ch == 0 || "\n\u0085\u2028\u2029".indexOf(ch) != -1) {
                    String data = text.substring(start, end);
                    this.column += data.length();
                    stream.write(data);
                    start = end;
                }
            }
            if (ch != 0) {
                spaces = (ch == ' ');
                breaks = ("\n\u0085\u2028\u2029".indexOf(ch) != -1);
            }
            end++;
        }
    }
}
