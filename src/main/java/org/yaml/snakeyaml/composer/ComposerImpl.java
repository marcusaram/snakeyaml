/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.composer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jvyaml.Resolver;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.parser.Parser;

/**
 * @see PyYAML 3.06 for more information
 */
public class ComposerImpl implements Composer {
    private Parser parser;
    private Resolver resolver;
    private Map anchors;

    public ComposerImpl(final Parser parser, final Resolver resolver) {
        this.parser = parser;
        this.resolver = resolver;
        this.anchors = new HashMap();
    }

    public boolean checkNode() {
        return !(parser.peekEvent() instanceof StreamEndEvent);
    }

    public Node getNode() {
        return checkNode() ? composeDocument() : (Node) null;
    }

    public Node composeDocument() {
        if (parser.peekEvent() instanceof StreamStartEvent) {
            // Drop STREAM-START event
            parser.getEvent();
        }
        // Drop DOCUMENT-START event
        parser.getEvent();
        final Node node = composeNode(null, null);
        // Drop DOCUMENT-END event
        parser.getEvent();
        this.anchors.clear();
        return node;
    }

    private final static boolean[] FALS = new boolean[] { false };
    private final static boolean[] TRU = new boolean[] { true };

    public Node composeNode(final Node parent, final Object index) {
        if (parser.peekEvent() instanceof AliasEvent) {
            final AliasEvent event = (AliasEvent) parser.getEvent();
            final String anchor = event.getAnchor();
            if (!anchors.containsKey(anchor)) {
                throw new ComposerException(null, "found undefined alias " + anchor, null);
            }
            return (Node) anchors.get(anchor);
        }
        final Event event = parser.peekEvent();
        String anchor = null;
        if (event instanceof NodeEvent) {
            anchor = ((NodeEvent) event).getAnchor();
        }
        if (null != anchor) {
            if (anchors.containsKey(anchor)) {
                throw new ComposerException("found duplicate anchor " + anchor
                        + "; first occurence", null, null);
            }
        }
        resolver.descendResolver(parent, index);
        Node node = null;
        if (event instanceof ScalarEvent) {
            final ScalarEvent ev = (ScalarEvent) parser.getEvent();
            String tag = ev.getTag();
            if (tag == null || tag.equals("!")) {
                tag = resolver.resolve(ScalarNode.class, ev.getValue(), ev.getImplicit());
            }
            node = new ScalarNode(tag, ev.getValue(), ev.getStartMark(), ev.getEndMark(), ev
                    .getStyle());
            if (null != anchor) {
                anchors.put(anchor, node);
            }
        } else if (event instanceof SequenceStartEvent) {
            final SequenceStartEvent start = (SequenceStartEvent) parser.getEvent();
            String tag = start.getTag();
            if (tag == null || tag.equals("!")) {
                tag = resolver.resolve(SequenceNode.class, null, start.getImplicit() ? TRU : FALS);
            }
            node = new SequenceNode(tag, new ArrayList(), event.getStartMark(), event.getEndMark(),
                    start.getFlowStyle());
            if (null != anchor) {
                anchors.put(anchor, node);
            }
            int ix = 0;
            while (!(parser.peekEvent() instanceof SequenceEndEvent)) {
                ((List) node.getValue()).add(composeNode(node, new Integer(ix++)));
            }
            parser.getEvent();
        } else if (event instanceof MappingStartEvent) {
            final MappingStartEvent start = (MappingStartEvent) parser.getEvent();
            String tag = start.getTag();
            if (tag == null || tag.equals("!")) {
                tag = resolver.resolve(MappingNode.class, null, start.getImplicit() ? TRU : FALS);
            }
            node = new MappingNode(tag, new HashMap(), event.getStartMark(), event.getEndMark(),
                    start.getFlowStyle());
            if (null != anchor) {
                anchors.put(anchor, node);
            }
            while (!(parser.peekEvent() instanceof MappingEndEvent)) {
                final Event key = parser.peekEvent();
                final Node itemKey = composeNode(node, null);
                if (((Map) node.getValue()).containsKey(itemKey)) {
                    composeNode(node, itemKey);
                } else {
                    ((Map) node.getValue()).put(itemKey, composeNode(node, itemKey));
                }
            }
            parser.getEvent();
        }
        resolver.ascendResolver();
        return node;
    }

}
