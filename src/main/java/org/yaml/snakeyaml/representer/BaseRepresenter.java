package org.yaml.snakeyaml.representer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Represent;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.serializer.Serializer;

public class BaseRepresenter implements Represent {
    @SuppressWarnings("unchecked")
    protected Map<Class, Represent> representers = new HashMap<Class, Represent>();
    @SuppressWarnings("unchecked")
    protected Map<Class, Represent> multiRepresenters = new HashMap<Class, Represent>();
    protected char defaultStyle;
    protected Boolean defaultFlowStyle;
    protected Map<Object, Node> representedObjects = new HashMap<Object, Node>();
    protected Set<Object> objectKeeper = new HashSet<Object>();
    private Serializer serializer;
    private Object aliasKey;

    @SuppressWarnings("unchecked")
    public BaseRepresenter(Serializer serializer, Map<Class, Represent> representers,
            char default_style, Boolean default_flow_style) {
        this.serializer = serializer;
        this.defaultStyle = default_style;
        this.defaultFlowStyle = default_flow_style;
        this.representers.putAll(representers);
    }

    protected void represent(Object data) throws IOException {
        Node node = representData(data);
        serializer.serialize(node);
        representedObjects.clear();
        objectKeeper.clear();
    }

    @SuppressWarnings("unchecked")
    public Node representData(Object data) {
        aliasKey = data;
        if (!ignoreAliases(data)) {
            // check for identity
            Object obj = representedObjects.get(aliasKey);
            if (obj != null && aliasKey == obj) {
                Node node = representedObjects.get(aliasKey);
                return node;
            }
        }
        Class clazz = data.getClass();
        // check the same class
        Node node;
        if (representers.containsKey(clazz)) {
            Represent representer = representers.get(clazz);
            node = representer.representData(data);
        } else {
            // check the parents
            if (multiRepresenters.containsKey(clazz)) {
                Represent representer = multiRepresenters.get(clazz);
                node = representer.representData(data);
            } else {
                if (multiRepresenters.containsKey(null)) {
                    Represent representer = multiRepresenters.get(null);
                    node = representer.representData(data);
                } else {
                    if (representers.containsKey(null)) {
                        Represent representer = representers.get(null);
                        node = representer.representData(data);
                    } else {
                        node = new ScalarNode(null, data.toString());
                    }
                }
            }
        }
        return node;
    }

    protected Node representScalar(String tag, String value, Character style) {
        if (style == null) {
            style = this.defaultStyle;
        }
        Node node = new ScalarNode(tag, value, null, null, style);
        if (aliasKey != null) {
            representedObjects.put(aliasKey, node);
        }
        return node;
    }

    protected Node representSequence(String tag, List<Object> sequence, Boolean flowStyle) {
        List<Object> value = new LinkedList<Object>();
        SequenceNode node = new SequenceNode(tag, value, flowStyle);
        if (aliasKey != null) {
            representedObjects.put(aliasKey, node);
        }
        boolean bestStyle = true;
        for (Object item : sequence) {
            Node nodeItem = representData(item);
            if (!((nodeItem instanceof ScalarNode && ((ScalarNode) nodeItem).getStyle() == 0))) {
                bestStyle = false;
            }
            value.add(nodeItem);
        }
        if (flowStyle == null) {
            if (defaultFlowStyle != null) {
                node.setFlowStyle(defaultFlowStyle);
            } else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }

    protected Node representMapping(String tag, Map<Object, Object> sequence, Boolean flowStyle) {
        List<Object[]> value = new LinkedList<Object[]>();
        MappingNode node = new MappingNode(tag, value, flowStyle);
        if (aliasKey != null) {
            representedObjects.put(aliasKey, node);
        }
        boolean bestStyle = true;
        for (Object itemKey : sequence.keySet()) {
            Object itemValue = sequence.get(itemKey);
            Node nodeKey = representData(itemKey);
            Node nodeValue = representData(itemValue);
            if (!((nodeKey instanceof ScalarNode && ((ScalarNode) nodeKey).getStyle() == 0))) {
                bestStyle = false;
            }
            if (!((nodeValue instanceof ScalarNode && ((ScalarNode) nodeValue).getStyle() == 0))) {
                bestStyle = false;
            }
            value.add(new Object[] { nodeKey, nodeValue });
        }
        if (flowStyle == null) {
            if (defaultFlowStyle != null) {
                node.setFlowStyle(defaultFlowStyle);
            } else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }

    private boolean ignoreAliases(Object data) {
        return false;
    }
}
