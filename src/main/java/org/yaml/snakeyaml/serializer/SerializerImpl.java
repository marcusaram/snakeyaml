/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.serializer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.YamlConfig;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.resolver.Resolver;

/**
 * @see PyYAML 3.06 for more information
 */
public class SerializerImpl implements Serializer {
    private static final String ANCHOR_TEMPLATE = "id{0,number,####}";
    private Emitter emitter;
    private Resolver resolver;
    private YamlConfig options;
    private boolean explicitStart;
    private boolean explicitEnd;
    private Integer[] useVersion;
    private Map<String, String> useTags;
    private String anchorTemplate;
    private Set<Node> serializedNodes;
    private Map<Node, String> anchors;
    private int lastAnchorId;
    private Boolean closed;

    public SerializerImpl(Emitter emitter, Resolver resolver, YamlConfig opts) {
        this.emitter = emitter;
        this.resolver = resolver;
        this.options = opts;
        this.explicitStart = opts.explicitStart();
        this.explicitEnd = opts.explicitEnd();
        this.useVersion = opts.version();
        this.useTags = opts.tags();
        this.anchorTemplate = opts.anchorFormat() == null ? ANCHOR_TEMPLATE : opts.anchorFormat();
        this.serializedNodes = new HashSet<Node>();
        this.anchors = new HashMap<Node, String>();
        this.lastAnchorId = 0;
        this.closed = null;
    }

    public void open() throws IOException {
        if (closed == null) {
            this.emitter.emit(new StreamStartEvent(null, null));
            this.closed = Boolean.FALSE;
        } else if (Boolean.TRUE.equals(closed)) {
            throw new SerializerException("serializer is closed");
        } else {
            throw new SerializerException("serializer is already opened");
        }
    }

    public void close() throws IOException {
        if (closed == null) {
            throw new SerializerException("serializer is not opened");
        } else if (!Boolean.TRUE.equals(closed)) {
            this.emitter.emit(new StreamEndEvent(null, null));
            this.closed = Boolean.TRUE;
        }
    }

    public void serialize(Node node) throws IOException {
        if (closed == null) {
            throw new SerializerException("serializer is not opened");
        } else if (closed) {
            throw new SerializerException("serializer is closed");
        }
        this.emitter.emit(new DocumentStartEvent(null, null, this.explicitStart, this.useVersion,
                useTags));
        anchorNode(node);
        serializeNode(node, null, null);
        this.emitter.emit(new DocumentEndEvent(null, null, this.explicitEnd));
        this.serializedNodes.clear();
        this.anchors.clear();
        this.lastAnchorId = 0;
    }

    private void anchorNode(final Node node) {
        if (this.anchors.containsKey(node)) {
            String anchor = (String) this.anchors.get(node);
            if (null == anchor) {
                anchor = generateAnchor(node);
                this.anchors.put(node, anchor);
            }
        } else {
            this.anchors.put(node, null);
            if (node instanceof SequenceNode) {
                for (final Iterator iter = ((List) node.getValue()).iterator(); iter.hasNext();) {
                    anchorNode((Node) iter.next());
                }
            } else if (node instanceof MappingNode) {
                final Map value = (Map) node.getValue();
                for (final Iterator iter = value.keySet().iterator(); iter.hasNext();) {
                    final Node key = (Node) iter.next();
                    anchorNode(key);
                    anchorNode((Node) value.get(key));
                }
            }
        }
    }

    private String generateAnchor(final Node node) {
        this.lastAnchorId++;
        return new MessageFormat(this.anchorTemplate).format(new Object[] { new Integer(
                this.lastAnchorId) });
    }

    private void serializeNode(final Node node, final Node parent, final Object index)
            throws IOException {
        final String tAlias = (String) this.anchors.get(node);
        if (this.serializedNodes.contains(node) && tAlias != null) {
            this.emitter.emit(new AliasEvent(tAlias, null, null));
        } else {
            this.serializedNodes.add(node);
            this.resolver.descendResolver(parent, index);
            if (node instanceof ScalarNode) {
                final String detectedTag = this.resolver.resolve(ScalarNode.class, (String) node
                        .getValue(), true);
                final String defaultTag = this.resolver.resolve(ScalarNode.class, (String) node
                        .getValue(), false);
                final boolean[] implicit = new boolean[] { false, false };
                if (!options.explicitTypes()) {
                    implicit[0] = node.getTag().equals(detectedTag);
                    implicit[1] = node.getTag().equals(defaultTag);
                }
                ScalarEvent event = new ScalarEvent(tAlias, node.getTag(), implicit, (String) node
                        .getValue(), null, null, ((ScalarNode) node).getStyle());
                this.emitter.emit(event);
            } else if (node instanceof SequenceNode) {
                final boolean implicit = !options.explicitTypes()
                        && (node.getTag().equals(this.resolver.resolve(SequenceNode.class, null,
                                true)));
                this.emitter.emit(new SequenceStartEvent(tAlias, node.getTag(), implicit, null,
                        null, ((CollectionNode) node).getFlowStyle()));
                int ix = 0;
                for (final Iterator iter = ((List) node.getValue()).iterator(); iter.hasNext();) {
                    serializeNode((Node) iter.next(), node, new Integer(ix++));
                }
                this.emitter.emit(new SequenceEndEvent(null, null));
            } else if (node instanceof MappingNode) {
                final boolean implicit = !options.explicitTypes()
                        && (node.getTag().equals(this.resolver.resolve(MappingNode.class, null,
                                true)));
                this.emitter.emit(new MappingStartEvent(tAlias, node.getTag(), implicit, null,
                        null, ((CollectionNode) node).getFlowStyle()));
                final Map value = (Map) node.getValue();
                for (final Iterator iter = value.keySet().iterator(); iter.hasNext();) {
                    final Node key = (Node) iter.next();
                    serializeNode(key, node, null);
                    serializeNode((Node) value.get(key), node, key);
                }
                this.emitter.emit(new MappingEndEvent(null, null));
            }
        }
    }
}
