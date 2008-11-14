/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.YamlConfig;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.scanner.Scanner;
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
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.ValueToken;

/**
 * <pre>
 * # The following YAML grammar is LL(1) and is parsed by a recursive descent
 * parser.
 * stream            ::= STREAM-START implicit_document? explicit_document* STREAM-END
 * implicit_document ::= block_node DOCUMENT-END*
 * explicit_document ::= DIRECTIVE* DOCUMENT-START block_node? DOCUMENT-END*
 * block_node_or_indentless_sequence ::=
 *                       ALIAS
 *                       | properties (block_content | indentless_block_sequence)?
 *                       | block_content
 *                       | indentless_block_sequence
 * block_node        ::= ALIAS
 *                       | properties block_content?
 *                       | block_content
 * flow_node         ::= ALIAS
 *                       | properties flow_content?
 *                       | flow_content
 * properties        ::= TAG ANCHOR? | ANCHOR TAG?
 * block_content     ::= block_collection | flow_collection | SCALAR
 * flow_content      ::= flow_collection | SCALAR
 * block_collection  ::= block_sequence | block_mapping
 * flow_collection   ::= flow_sequence | flow_mapping
 * block_sequence    ::= BLOCK-SEQUENCE-START (BLOCK-ENTRY block_node?)* BLOCK-END
 * indentless_sequence   ::= (BLOCK-ENTRY block_node?)+
 * block_mapping     ::= BLOCK-MAPPING_START
 *                       ((KEY block_node_or_indentless_sequence?)?
 *                       (VALUE block_node_or_indentless_sequence?)?)*
 *                       BLOCK-END
 * flow_sequence     ::= FLOW-SEQUENCE-START
 *                       (flow_sequence_entry FLOW-ENTRY)*
 *                       flow_sequence_entry?
 *                       FLOW-SEQUENCE-END
 * flow_sequence_entry   ::= flow_node | KEY flow_node? (VALUE flow_node?)?
 * flow_mapping      ::= FLOW-MAPPING-START
 *                       (flow_mapping_entry FLOW-ENTRY)*
 *                       flow_mapping_entry?
 *                       FLOW-MAPPING-END
 * flow_mapping_entry    ::= flow_node | KEY flow_node? (VALUE flow_node?)?
 * FIRST sets:
 * stream: { STREAM-START }
 * explicit_document: { DIRECTIVE DOCUMENT-START }
 * implicit_document: FIRST(block_node)
 * block_node: { ALIAS TAG ANCHOR SCALAR BLOCK-SEQUENCE-START BLOCK-MAPPING-START FLOW-SEQUENCE-START FLOW-MAPPING-START }
 * flow_node: { ALIAS ANCHOR TAG SCALAR FLOW-SEQUENCE-START FLOW-MAPPING-START }
 * block_content: { BLOCK-SEQUENCE-START BLOCK-MAPPING-START FLOW-SEQUENCE-START FLOW-MAPPING-START SCALAR }
 * flow_content: { FLOW-SEQUENCE-START FLOW-MAPPING-START SCALAR }
 * block_collection: { BLOCK-SEQUENCE-START BLOCK-MAPPING-START }
 * flow_collection: { FLOW-SEQUENCE-START FLOW-MAPPING-START }
 * block_sequence: { BLOCK-SEQUENCE-START }
 * block_mapping: { BLOCK-MAPPING-START }
 * block_node_or_indentless_sequence: { ALIAS ANCHOR TAG SCALAR BLOCK-SEQUENCE-START BLOCK-MAPPING-START FLOW-SEQUENCE-START FLOW-MAPPING-START BLOCK-ENTRY }
 * indentless_sequence: { ENTRY }
 * flow_collection: { FLOW-SEQUENCE-START FLOW-MAPPING-START }
 * flow_sequence: { FLOW-SEQUENCE-START }
 * flow_mapping: { FLOW-MAPPING-START }
 * flow_sequence_entry: { ALIAS ANCHOR TAG SCALAR FLOW-SEQUENCE-START FLOW-MAPPING-START KEY }
 * flow_mapping_entry: { ALIAS ANCHOR TAG SCALAR FLOW-SEQUENCE-START FLOW-MAPPING-START KEY }
 * </pre>
 * 
 * Since writing a recursive-descendant parser is a straightforward task, we do
 * not give many comments here.
 * 
 * @see PyYAML 3.06 for more information
 */
public class ParserImpl implements Parser {
    // Memnonics for the production table
    private final static int P_STREAM = 0;
    private final static int P_STREAM_START = 1; // TERMINAL
    private final static int P_STREAM_END = 2; // TERMINAL
    private final static int P_IMPLICIT_DOCUMENT = 3;
    private final static int P_EXPLICIT_DOCUMENT = 4;
    private final static int P_DOCUMENT_START = 5;
    private final static int P_DOCUMENT_START_IMPLICIT = 6;
    private final static int P_DOCUMENT_END = 7;
    private final static int P_BLOCK_NODE = 8;
    private final static int P_BLOCK_CONTENT = 9;
    private final static int P_PROPERTIES = 10;
    private final static int P_PROPERTIES_END = 11;
    private final static int P_FLOW_CONTENT = 12;
    private final static int P_BLOCK_SEQUENCE = 13;
    private final static int P_BLOCK_MAPPING = 14;
    private final static int P_FLOW_SEQUENCE = 15;
    private final static int P_FLOW_MAPPING = 16;
    private final static int P_SCALAR = 17;
    private final static int P_BLOCK_SEQUENCE_ENTRY = 18;
    private final static int P_BLOCK_MAPPING_ENTRY = 19;
    private final static int P_BLOCK_MAPPING_ENTRY_VALUE = 20;
    private final static int P_BLOCK_NODE_OR_INDENTLESS_SEQUENCE = 21;
    private final static int P_BLOCK_SEQUENCE_START = 22;
    private final static int P_BLOCK_SEQUENCE_END = 23;
    private final static int P_BLOCK_MAPPING_START = 24;
    private final static int P_BLOCK_MAPPING_END = 25;
    private final static int P_INDENTLESS_BLOCK_SEQUENCE = 26;
    private final static int P_BLOCK_INDENTLESS_SEQUENCE_START = 27;
    private final static int P_INDENTLESS_BLOCK_SEQUENCE_ENTRY = 28;
    private final static int P_BLOCK_INDENTLESS_SEQUENCE_END = 29;
    private final static int P_FLOW_SEQUENCE_START = 30;
    private final static int P_FLOW_SEQUENCE_ENTRY = 31;
    private final static int P_FLOW_SEQUENCE_END = 32;
    private final static int P_FLOW_MAPPING_START = 33;
    private final static int P_FLOW_MAPPING_ENTRY = 34;
    private final static int P_FLOW_MAPPING_END = 35;
    private final static int P_FLOW_INTERNAL_MAPPING_START = 36;
    private final static int P_FLOW_INTERNAL_CONTENT = 37;
    private final static int P_FLOW_INTERNAL_VALUE = 38;
    private final static int P_FLOW_INTERNAL_MAPPING_END = 39;
    private final static int P_FLOW_ENTRY_MARKER = 40;
    private final static int P_FLOW_NODE = 41;
    private final static int P_FLOW_MAPPING_INTERNAL_CONTENT = 42;
    private final static int P_FLOW_MAPPING_INTERNAL_VALUE = 43;
    private final static int P_ALIAS = 44;
    private final static int P_EMPTY_SCALAR = 45;

    private final static Event DOCUMENT_END_TRUE = new DocumentEndEvent(null, null, true);
    private final static Event DOCUMENT_END_FALSE = new DocumentEndEvent(null, null, false);
    private final static Event MAPPING_END = new MappingEndEvent();
    private final static Event SEQUENCE_END = new SequenceEndEvent();
    private final static Event STREAM_END = new StreamEndEvent();
    private final static Event STREAM_START = new StreamStartEvent(null, null);

    private static class ProductionEnvironment {
        private List tags;
        private List anchors;
        private Map tagHandles;
        private int[] yamlVersion;
        private int[] defaultYamlVersion;

        public ProductionEnvironment(final YamlConfig cfg) {
            this.tags = new LinkedList();
            this.anchors = new LinkedList();
            this.tagHandles = new HashMap();
            this.yamlVersion = null;
            this.defaultYamlVersion = new int[2];
            this.defaultYamlVersion[0] = Integer.parseInt(cfg.version().substring(0,
                    cfg.version().indexOf('.')));
            this.defaultYamlVersion[1] = Integer.parseInt(cfg.version().substring(
                    cfg.version().indexOf('.') + 1));
        }

        public List getTags() {
            return this.tags;
        }

        public List getAnchors() {
            return this.anchors;
        }

        public Map getTagHandles() {
            return this.tagHandles;
        }

        public int[] getYamlVersion() {
            return this.yamlVersion;
        }

        public int[] getFinalYamlVersion() {
            if (null == this.yamlVersion) {
                return this.defaultYamlVersion;
            }
            return this.yamlVersion;
        }

        public void setYamlVersion(final int[] yamlVersion) {
            this.yamlVersion = yamlVersion;
        }
    }

    private static interface Production {
        Event produce(final List parseStack, final ProductionEnvironment env, final Scanner scanner);
    }

    private final static Production[] P_TABLE = new Production[46];

    private final static Map DEFAULT_TAGS_1_0 = new HashMap();
    private final static Map DEFAULT_TAGS_1_1 = new HashMap();
    static {
        DEFAULT_TAGS_1_0.put("!", "tag:yaml.org,2002:");

        DEFAULT_TAGS_1_1.put("!", "!");
        DEFAULT_TAGS_1_1.put("!!", "tag:yaml.org,2002:");
    }

    static {
        P_TABLE[P_STREAM] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                parseStack.add(0, P_TABLE[P_STREAM_END]);
                parseStack.add(0, P_TABLE[P_EXPLICIT_DOCUMENT]);
                parseStack.add(0, P_TABLE[P_IMPLICIT_DOCUMENT]);
                parseStack.add(0, P_TABLE[P_STREAM_START]);
                return null;
            }
        };
        P_TABLE[P_STREAM_START] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                scanner.getToken();
                return STREAM_START;
            }
        };
        P_TABLE[P_STREAM_END] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                scanner.getToken();
                return STREAM_END;
            }
        };
        P_TABLE[P_IMPLICIT_DOCUMENT] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final Token curr = scanner.peekToken();
                if (!(curr instanceof DirectiveToken || curr instanceof DocumentStartToken || curr instanceof StreamEndToken)) {
                    parseStack.add(0, P_TABLE[P_DOCUMENT_END]);
                    parseStack.add(0, P_TABLE[P_BLOCK_NODE]);
                    parseStack.add(0, P_TABLE[P_DOCUMENT_START_IMPLICIT]);
                }
                return null;
            }
        };
        P_TABLE[P_EXPLICIT_DOCUMENT] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (!(scanner.peekToken() instanceof StreamEndToken)) {
                    parseStack.add(0, P_TABLE[P_EXPLICIT_DOCUMENT]);
                    parseStack.add(0, P_TABLE[P_DOCUMENT_END]);
                    parseStack.add(0, P_TABLE[P_BLOCK_NODE]);
                    parseStack.add(0, P_TABLE[P_DOCUMENT_START]);
                }
                return null;
            }
        };
        P_TABLE[P_DOCUMENT_START] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                Token tok = scanner.peekToken();
                final Object[] directives = processDirectives(env, scanner);
                if (!(scanner.peekToken() instanceof DocumentStartToken)) {
                    throw new ParserException(null, null, "expected '<document start>', but found "
                            + tok.getClass().getName(), null);
                }
                scanner.getToken();
                return new DocumentStartEvent(null, null, true, (int[]) directives[0],
                        (Map) directives[1]);
            }
        };
        P_TABLE[P_DOCUMENT_START_IMPLICIT] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final Object[] directives = processDirectives(env, scanner);
                return new DocumentStartEvent(null, null, false, (int[]) directives[0],
                        (Map) directives[1]);
            }
        };
        P_TABLE[P_DOCUMENT_END] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                Token tok = scanner.peekToken();
                boolean explicit = false;
                while (scanner.peekToken() instanceof DocumentEndToken) {
                    scanner.getToken();
                    explicit = true;
                }
                return explicit ? DOCUMENT_END_TRUE : DOCUMENT_END_FALSE;
            }
        };
        P_TABLE[P_BLOCK_NODE] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final Token curr = scanner.peekToken();
                if (curr instanceof DirectiveToken || curr instanceof DocumentStartToken
                        || curr instanceof DocumentEndToken || curr instanceof StreamEndToken) {
                    parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                } else {
                    if (curr instanceof AliasToken) {
                        parseStack.add(0, P_TABLE[P_ALIAS]);
                    } else {
                        parseStack.add(0, P_TABLE[P_PROPERTIES_END]);
                        parseStack.add(0, P_TABLE[P_BLOCK_CONTENT]);
                        parseStack.add(0, P_TABLE[P_PROPERTIES]);
                    }
                }
                return null;
            }
        };
        P_TABLE[P_BLOCK_CONTENT] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final Token tok = scanner.peekToken();
                if (tok instanceof BlockSequenceStartToken) {
                    parseStack.add(0, P_TABLE[P_BLOCK_SEQUENCE]);
                } else if (tok instanceof BlockMappingStartToken) {
                    parseStack.add(0, P_TABLE[P_BLOCK_MAPPING]);
                } else if (tok instanceof FlowSequenceStartToken) {
                    parseStack.add(0, P_TABLE[P_FLOW_SEQUENCE]);
                } else if (tok instanceof FlowMappingStartToken) {
                    parseStack.add(0, P_TABLE[P_FLOW_MAPPING]);
                } else if (tok instanceof ScalarToken) {
                    parseStack.add(0, P_TABLE[P_SCALAR]);
                } else {
                    throw new ParserException("while scanning a node", null,
                            "expected the node content, but found " + tok.getClass().getName(),
                            null);
                }
                return null;
            }
        };
        P_TABLE[P_PROPERTIES] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                String anchor = null;
                Object tag = null;
                if (scanner.peekToken() instanceof AnchorToken) {
                    anchor = ((AnchorToken) scanner.getToken()).getValue();
                    if (scanner.peekToken() instanceof TagToken) {
                        scanner.getToken();
                    }
                } else if (scanner.peekToken() instanceof TagToken) {
                    tag = ((TagToken) scanner.getToken()).getValue();
                    if (scanner.peekToken() instanceof AnchorToken) {
                        anchor = ((AnchorToken) scanner.getToken()).getValue();
                    }
                }
                if (tag != null && !tag.equals("!")) {
                    final String handle = ((String[]) tag)[0];
                    final String suffix = ((String[]) tag)[1];
                    if (handle != null) {
                        if (!env.getTagHandles().containsKey(handle)) {
                            throw new ParserException("while parsing a node", null,
                                    "found undefined tag handle " + handle, null);
                        }
                        tag = ((String) env.getTagHandles().get(handle)) + suffix;
                    } else {
                        tag = suffix;
                    }
                }
                env.getAnchors().add(0, anchor);
                env.getTags().add(0, tag);
                return null;
            }
        };
        P_TABLE[P_PROPERTIES_END] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                env.getAnchors().remove(0);
                env.getTags().remove(0);
                return null;
            }
        };
        P_TABLE[P_FLOW_CONTENT] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final Token tok = scanner.peekToken();
                if (tok instanceof FlowSequenceStartToken) {
                    parseStack.add(0, P_TABLE[P_FLOW_SEQUENCE]);
                } else if (tok instanceof FlowMappingStartToken) {
                    parseStack.add(0, P_TABLE[P_FLOW_MAPPING]);
                } else if (tok instanceof ScalarToken) {
                    parseStack.add(0, P_TABLE[P_SCALAR]);
                } else {
                    throw new ParserException("while scanning a flow node", null,
                            "expected the node content, but found " + tok.getClass().getName(),
                            null);
                }
                return null;
            }
        };
        P_TABLE[P_BLOCK_SEQUENCE] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                parseStack.add(0, P_TABLE[P_BLOCK_SEQUENCE_END]);
                parseStack.add(0, P_TABLE[P_BLOCK_SEQUENCE_ENTRY]);
                parseStack.add(0, P_TABLE[P_BLOCK_SEQUENCE_START]);
                return null;
            }
        };
        P_TABLE[P_BLOCK_MAPPING] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                parseStack.add(0, P_TABLE[P_BLOCK_MAPPING_END]);
                parseStack.add(0, P_TABLE[P_BLOCK_MAPPING_ENTRY]);
                parseStack.add(0, P_TABLE[P_BLOCK_MAPPING_START]);
                return null;
            }
        };
        P_TABLE[P_FLOW_SEQUENCE] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                parseStack.add(0, P_TABLE[P_FLOW_SEQUENCE_END]);
                parseStack.add(0, P_TABLE[P_FLOW_SEQUENCE_ENTRY]);
                parseStack.add(0, P_TABLE[P_FLOW_SEQUENCE_START]);
                return null;
            }
        };
        P_TABLE[P_FLOW_MAPPING] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                parseStack.add(0, P_TABLE[P_FLOW_MAPPING_END]);
                parseStack.add(0, P_TABLE[P_FLOW_MAPPING_ENTRY]);
                parseStack.add(0, P_TABLE[P_FLOW_MAPPING_START]);
                return null;
            }
        };
        P_TABLE[P_SCALAR] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final ScalarToken tok = (ScalarToken) scanner.getToken();
                boolean[] implicit = null;
                if ((tok.getPlain() && env.getTags().get(0) == null)
                        || "!".equals(env.getTags().get(0))) {
                    implicit = new boolean[] { true, false };
                } else if (env.getTags().get(0) == null) {
                    implicit = new boolean[] { false, true };
                } else {
                    implicit = new boolean[] { false, false };
                }
                return new ScalarEvent((String) env.getAnchors().get(0), (String) env.getTags()
                        .get(0), implicit, tok.getValue(), null, null, tok.getStyle());
            }
        };
        P_TABLE[P_BLOCK_SEQUENCE_ENTRY] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (scanner.peekToken() instanceof BlockEntryToken) {
                    scanner.getToken();
                    if (!(scanner.peekToken() instanceof BlockEntryToken || scanner.peekToken() instanceof BlockEndToken)) {
                        parseStack.add(0, P_TABLE[P_BLOCK_SEQUENCE_ENTRY]);
                        parseStack.add(0, P_TABLE[P_BLOCK_NODE]);
                    } else {
                        parseStack.add(0, P_TABLE[P_BLOCK_SEQUENCE_ENTRY]);
                        parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                    }
                }
                return null;
            }
        };
        P_TABLE[P_BLOCK_MAPPING_ENTRY] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (scanner.peekToken() instanceof KeyToken
                        || scanner.peekToken() instanceof ValueToken) {
                    if (scanner.peekToken() instanceof KeyToken) {
                        scanner.getToken();
                        final Token curr = scanner.peekToken();
                        if (!(curr instanceof KeyToken || curr instanceof ValueToken || curr instanceof BlockEndToken)) {
                            parseStack.add(0, P_TABLE[P_BLOCK_MAPPING_ENTRY]);
                            parseStack.add(0, P_TABLE[P_BLOCK_MAPPING_ENTRY_VALUE]);
                            parseStack.add(0, P_TABLE[P_BLOCK_NODE_OR_INDENTLESS_SEQUENCE]);
                        } else {
                            parseStack.add(0, P_TABLE[P_BLOCK_MAPPING_ENTRY]);
                            parseStack.add(0, P_TABLE[P_BLOCK_MAPPING_ENTRY_VALUE]);
                            parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                        }
                    } else {
                        parseStack.add(0, P_TABLE[P_BLOCK_MAPPING_ENTRY]);
                        parseStack.add(0, P_TABLE[P_BLOCK_MAPPING_ENTRY_VALUE]);
                        parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                    }
                }
                return null;
            }
        };
        P_TABLE[P_BLOCK_MAPPING_ENTRY_VALUE] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (scanner.peekToken() instanceof KeyToken
                        || scanner.peekToken() instanceof ValueToken) {
                    if (scanner.peekToken() instanceof ValueToken) {
                        scanner.getToken();
                        final Token curr = scanner.peekToken();
                        if (!(curr instanceof KeyToken || curr instanceof ValueToken || curr instanceof BlockEndToken)) {
                            parseStack.add(0, P_TABLE[P_BLOCK_NODE_OR_INDENTLESS_SEQUENCE]);
                        } else {
                            parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                        }
                    } else {
                        parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                    }
                }
                return null;
            }
        };
        P_TABLE[P_BLOCK_NODE_OR_INDENTLESS_SEQUENCE] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (scanner.peekToken() instanceof AliasToken) {
                    parseStack.add(0, P_TABLE[P_ALIAS]);
                } else {
                    if (scanner.peekToken() instanceof BlockEntryToken) {
                        parseStack.add(0, P_TABLE[P_INDENTLESS_BLOCK_SEQUENCE]);
                        parseStack.add(0, P_TABLE[P_PROPERTIES]);
                    } else {
                        parseStack.add(0, P_TABLE[P_BLOCK_CONTENT]);
                        parseStack.add(0, P_TABLE[P_PROPERTIES]);
                    }
                }
                return null;
            }
        };
        P_TABLE[P_BLOCK_SEQUENCE_START] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final boolean implicit = env.getTags().get(0) == null
                        || env.getTags().get(0).equals("!");
                scanner.getToken();
                return new SequenceStartEvent((String) env.getAnchors().get(0), (String) env
                        .getTags().get(0), implicit, null, null, false);
            }
        };
        P_TABLE[P_BLOCK_SEQUENCE_END] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                Token tok = null;
                if (!(scanner.peekToken() instanceof BlockEndToken)) {
                    tok = scanner.peekToken();
                    throw new ParserException("while scanning a block collection", null,
                            "expected <block end>, but found " + tok.getClass().getName(), null);
                }
                scanner.getToken();
                return SEQUENCE_END;
            }
        };
        P_TABLE[P_BLOCK_MAPPING_START] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final boolean implicit = env.getTags().get(0) == null
                        || env.getTags().get(0).equals("!");
                scanner.getToken();
                return new MappingStartEvent((String) env.getAnchors().get(0), (String) env
                        .getTags().get(0), implicit, null, null, false);
            }
        };
        P_TABLE[P_BLOCK_MAPPING_END] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                Token tok = null;
                if (!(scanner.peekToken() instanceof BlockEndToken)) {
                    tok = scanner.peekToken();
                    throw new ParserException("while scanning a block mapping", null,
                            "expected <block end>, but found " + tok.getClass().getName(), null);
                }
                scanner.getToken();
                return MAPPING_END;
            }
        };
        P_TABLE[P_INDENTLESS_BLOCK_SEQUENCE] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                parseStack.add(0, P_TABLE[P_BLOCK_INDENTLESS_SEQUENCE_END]);
                parseStack.add(0, P_TABLE[P_INDENTLESS_BLOCK_SEQUENCE_ENTRY]);
                parseStack.add(0, P_TABLE[P_BLOCK_INDENTLESS_SEQUENCE_START]);
                return null;
            }
        };
        P_TABLE[P_BLOCK_INDENTLESS_SEQUENCE_START] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final boolean implicit = env.getTags().get(0) == null
                        || env.getTags().get(0).equals("!");
                return new SequenceStartEvent((String) env.getAnchors().get(0), (String) env
                        .getTags().get(0), implicit, null, null, false);
            }
        };
        P_TABLE[P_INDENTLESS_BLOCK_SEQUENCE_ENTRY] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (scanner.peekToken() instanceof BlockEntryToken) {
                    scanner.getToken();
                    final Token curr = scanner.peekToken();
                    if (!(curr instanceof BlockEntryToken || curr instanceof KeyToken
                            || curr instanceof ValueToken || curr instanceof BlockEndToken)) {
                        parseStack.add(0, P_TABLE[P_INDENTLESS_BLOCK_SEQUENCE_ENTRY]);
                        parseStack.add(0, P_TABLE[P_BLOCK_NODE]);
                    } else {
                        parseStack.add(0, P_TABLE[P_INDENTLESS_BLOCK_SEQUENCE_ENTRY]);
                        parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                    }
                }
                return null;
            }
        };
        P_TABLE[P_BLOCK_INDENTLESS_SEQUENCE_END] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                return SEQUENCE_END;
            }
        };
        P_TABLE[P_FLOW_SEQUENCE_START] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final boolean implicit = env.getTags().get(0) == null
                        || env.getTags().get(0).equals("!");
                scanner.getToken();
                return new SequenceStartEvent((String) env.getAnchors().get(0), (String) env
                        .getTags().get(0), implicit, null, null, true);
            }
        };
        P_TABLE[P_FLOW_SEQUENCE_ENTRY] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (!(scanner.peekToken() instanceof FlowSequenceEndToken)) {
                    if (scanner.peekToken() instanceof KeyToken) {
                        parseStack.add(0, P_TABLE[P_FLOW_SEQUENCE_ENTRY]);
                        parseStack.add(0, P_TABLE[P_FLOW_ENTRY_MARKER]);
                        parseStack.add(0, P_TABLE[P_FLOW_INTERNAL_MAPPING_END]);
                        parseStack.add(0, P_TABLE[P_FLOW_INTERNAL_VALUE]);
                        parseStack.add(0, P_TABLE[P_FLOW_INTERNAL_CONTENT]);
                        parseStack.add(0, P_TABLE[P_FLOW_INTERNAL_MAPPING_START]);
                    } else {
                        parseStack.add(0, P_TABLE[P_FLOW_SEQUENCE_ENTRY]);
                        parseStack.add(0, P_TABLE[P_FLOW_NODE]);
                        parseStack.add(0, P_TABLE[P_FLOW_ENTRY_MARKER]);
                    }
                }
                return null;
            }
        };
        P_TABLE[P_FLOW_SEQUENCE_END] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                scanner.getToken();
                return SEQUENCE_END;
            }
        };
        P_TABLE[P_FLOW_MAPPING_START] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final boolean implicit = env.getTags().get(0) == null
                        || env.getTags().get(0).equals("!");
                scanner.getToken();
                return new MappingStartEvent((String) env.getAnchors().get(0), (String) env
                        .getTags().get(0), implicit, null, null, true);
            }
        };
        P_TABLE[P_FLOW_MAPPING_ENTRY] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (!(scanner.peekToken() instanceof FlowMappingEndToken)) {
                    if (scanner.peekToken() instanceof KeyToken) {
                        parseStack.add(0, P_TABLE[P_FLOW_MAPPING_ENTRY]);
                        parseStack.add(0, P_TABLE[P_FLOW_ENTRY_MARKER]);
                        parseStack.add(0, P_TABLE[P_FLOW_MAPPING_INTERNAL_VALUE]);
                        parseStack.add(0, P_TABLE[P_FLOW_MAPPING_INTERNAL_CONTENT]);
                    } else {
                        parseStack.add(0, P_TABLE[P_FLOW_MAPPING_ENTRY]);
                        parseStack.add(0, P_TABLE[P_FLOW_NODE]);
                        parseStack.add(0, P_TABLE[P_FLOW_ENTRY_MARKER]);
                    }
                }
                return null;
            }
        };
        P_TABLE[P_FLOW_MAPPING_END] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                scanner.getToken();
                return MAPPING_END;
            }
        };
        P_TABLE[P_FLOW_INTERNAL_MAPPING_START] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                scanner.getToken();
                return new MappingStartEvent(null, null, true, null, null, true);
            }
        };
        P_TABLE[P_FLOW_INTERNAL_CONTENT] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final Token curr = scanner.peekToken();
                if (!(curr instanceof ValueToken || curr instanceof FlowEntryToken || curr instanceof FlowSequenceEndToken)) {
                    parseStack.add(0, P_TABLE[P_FLOW_NODE]);
                } else {
                    parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                }
                return null;
            }
        };
        P_TABLE[P_FLOW_INTERNAL_VALUE] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (scanner.peekToken() instanceof ValueToken) {
                    scanner.getToken();
                    if (!((scanner.peekToken() instanceof FlowEntryToken) || (scanner.peekToken() instanceof FlowSequenceEndToken))) {
                        parseStack.add(0, P_TABLE[P_FLOW_NODE]);
                    } else {
                        parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                    }
                } else {
                    parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                }
                return null;
            }
        };
        P_TABLE[P_FLOW_INTERNAL_MAPPING_END] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                return MAPPING_END;
            }
        };
        P_TABLE[P_FLOW_ENTRY_MARKER] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (scanner.peekToken() instanceof FlowEntryToken) {
                    scanner.getToken();
                }
                return null;
            }
        };
        P_TABLE[P_FLOW_NODE] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (scanner.peekToken() instanceof AliasToken) {
                    parseStack.add(0, P_TABLE[P_ALIAS]);
                } else {
                    parseStack.add(0, P_TABLE[P_PROPERTIES_END]);
                    parseStack.add(0, P_TABLE[P_FLOW_CONTENT]);
                    parseStack.add(0, P_TABLE[P_PROPERTIES]);
                }
                return null;
            }
        };
        P_TABLE[P_FLOW_MAPPING_INTERNAL_CONTENT] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final Token curr = scanner.peekToken();
                if (!(curr instanceof ValueToken || curr instanceof FlowEntryToken || curr instanceof FlowMappingEndToken)) {
                    scanner.getToken();
                    parseStack.add(0, P_TABLE[P_FLOW_NODE]);
                } else {
                    parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                }
                return null;
            }
        };
        P_TABLE[P_FLOW_MAPPING_INTERNAL_VALUE] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                if (scanner.peekToken() instanceof ValueToken) {
                    scanner.getToken();
                    if (!(scanner.peekToken() instanceof FlowEntryToken || scanner.peekToken() instanceof FlowMappingEndToken)) {
                        parseStack.add(0, P_TABLE[P_FLOW_NODE]);
                    } else {
                        parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                    }
                } else {
                    parseStack.add(0, P_TABLE[P_EMPTY_SCALAR]);
                }
                return null;
            }
        };
        P_TABLE[P_ALIAS] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                final AliasToken tok = (AliasToken) scanner.getToken();
                return new AliasEvent(tok.getValue(), null, null);
            }
        };
        P_TABLE[P_EMPTY_SCALAR] = new Production() {
            public Event produce(final List parseStack, final ProductionEnvironment env,
                    final Scanner scanner) {
                return processEmptyScalar();
            }
        };
    }

    private static Event processEmptyScalar() {
        return new ScalarEvent(null, null, new boolean[] { true, false }, "", null, null, (char) 0);
    }

    private static Object[] processDirectives(final ProductionEnvironment env, final Scanner scanner) {
        while (scanner.peekToken() instanceof DirectiveToken) {
            final DirectiveToken tok = (DirectiveToken) scanner.getToken();
            if (tok.getName().equals("YAML")) {
                if (env.getYamlVersion() != null) {
                    throw new ParserException(null, null, "found duplicate YAML directive", null);
                }
                final int major = Integer.parseInt(tok.getValue()[0]);
                final int minor = Integer.parseInt(tok.getValue()[1]);
                if (major != 1) {
                    throw new ParserException(null, null,
                            "found incompatible YAML document (version 1.* is required)", null);
                }
                env.setYamlVersion(new int[] { major, minor });
            } else if (tok.getName().equals("TAG")) {
                final String handle = tok.getValue()[0];
                final String prefix = tok.getValue()[1];
                if (env.getTagHandles().containsKey(handle)) {
                    throw new ParserException(null, null, "duplicate tag handle " + handle, null);
                }
                env.getTagHandles().put(handle, prefix);
            }
        }
        Object[] value = new Object[2];
        value[0] = env.getFinalYamlVersion();

        if (!env.getTagHandles().isEmpty()) {
            value[1] = new HashMap(env.getTagHandles());
        }

        final Map baseTags = ((int[]) value[0])[1] == 0 ? DEFAULT_TAGS_1_0 : DEFAULT_TAGS_1_1;
        for (final Iterator iter = baseTags.keySet().iterator(); iter.hasNext();) {
            final Object key = iter.next();
            if (!env.getTagHandles().containsKey(key)) {
                env.getTagHandles().put(key, baseTags.get(key));
            }
        }
        return value;
    }

    private Scanner scanner = null;
    private YamlConfig cfg = null;

    public ParserImpl(final Scanner scanner, final YamlConfig cfg) {
        this.scanner = scanner;
        this.cfg = cfg;
    }

    private Event currentEvent = null;

    public boolean checkEvent(final Class[] choices) {
        parseStream();
        if (this.currentEvent == null) {
            this.currentEvent = parseStreamNext();
        }
        if (this.currentEvent != null) {
            if (choices.length == 0) {
                return true;
            }
            for (int i = 0, j = choices.length; i < j; i++) {
                if (choices[i].isInstance(this.currentEvent)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Event peekEvent() {
        parseStream();
        if (this.currentEvent == null) {
            this.currentEvent = parseStreamNext();
        }
        return this.currentEvent;
    }

    public Event getEvent() {
        parseStream();
        if (this.currentEvent == null) {
            this.currentEvent = parseStreamNext();
        }
        final Event value = this.currentEvent;
        this.currentEvent = null;
        return value;
    }

    private List parseStack = null;
    private ProductionEnvironment pEnv = null;

    public void parseStream() {
        if (null == parseStack) {
            this.parseStack = new LinkedList();
            this.parseStack.add(0, P_TABLE[P_STREAM]);
            this.pEnv = new ProductionEnvironment(cfg);
        }
    }

    public Event parseStreamNext() {
        while (!parseStack.isEmpty()) {
            final Event value = ((Production) (this.parseStack.remove(0))).produce(this.parseStack,
                    this.pEnv, this.scanner);
            if (null != value) {
                return value;
            }
        }
        this.pEnv = null;
        return null;
    }

}
