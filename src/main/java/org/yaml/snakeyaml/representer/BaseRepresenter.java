/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Represent;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.serializer.Serializer;

public class BaseRepresenter implements Represent {
    @SuppressWarnings("unchecked")
    protected Map<Class, Represent> representers = new HashMap<Class, Represent>();
    /**
     * in Java 'null' is not a type. So we have keep the null representer
     * separately otherwise it will coincide with the default representer which
     * is stored with the key null.
     */
    protected Represent nullRepresenter;
    @SuppressWarnings("unchecked")
    protected Map<Class, Represent> multiRepresenters = new HashMap<Class, Represent>();
    protected Represent nullMultiRepresenter;
    protected Character defaultStyle;
    protected Boolean defaultFlowStyle;
    protected Map<Integer, Node> representedObjects = new HashMap<Integer, Node>();
    protected Set<Object> objectKeeper = new HashSet<Object>();
    private Serializer serializer;
    private Integer aliasKey;// internal memory address

    @SuppressWarnings("unchecked")
    public BaseRepresenter(Serializer serializer, Map<Class, Represent> representers,
            Character default_style, Boolean default_flow_style) {
        this.serializer = serializer;
        this.defaultStyle = default_style;
        this.defaultFlowStyle = default_flow_style;
        this.representers.putAll(representers);
    }

    public void represent(Object data) throws IOException {
        Node node = representData(data);
        serializer.serialize(node);
        representedObjects.clear();
        objectKeeper.clear();
    }

    @SuppressWarnings("unchecked")
    public Node representData(Object data) {
        aliasKey = System.identityHashCode(data);// take memory address
        if (!ignoreAliases(data)) {
            // check for identity
            if (representedObjects.containsKey(aliasKey)) {
                Node node = representedObjects.get(aliasKey);
                return node;
            }
        }
        // check for null first
        if (data == null) {
            if (nullRepresenter != null) {
                Node node = nullRepresenter.representData(data);
                return node;
            } else if (nullMultiRepresenter != null) {
                Node node = nullMultiRepresenter.representData(data);
                return node;
            }
        }
        // check the same class
        Node node;
        Class clazz = data.getClass();
        if (representers.containsKey(clazz)) {
            Represent representer = representers.get(clazz);
            node = representer.representData(data);
        } else {
            // check the parents
            for (Class repr : multiRepresenters.keySet()) {
                if (repr.isInstance(data)) {
                    Represent representer = multiRepresenters.get(repr);
                    node = representer.representData(data);
                    return node;
                }
            }
            // check array of primitives
            if (clazz.getName().startsWith("[")) {
                throw new YAMLException("Arrays of primitives are not supported.");
            }
            // check defaults
            if (multiRepresenters.containsKey(null)) {
                Represent representer = multiRepresenters.get(null);
                node = representer.representData(data);
            } else {
                if (representers.containsKey(null)) {
                    Represent representer = representers.get(null);
                    node = representer.representData(data);
                } else {
                    String value = (data == null ? "" : data.toString());
                    node = new ScalarNode(null, value);
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

    protected Node representScalar(String tag, String value) {
        return representScalar(tag, value, null);
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
            if (!((nodeItem instanceof ScalarNode && ((ScalarNode) nodeItem).getStyle() == null))) {
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
            if (!((nodeKey instanceof ScalarNode && ((ScalarNode) nodeKey).getStyle() == null))) {
                bestStyle = false;
            }
            if (!((nodeValue instanceof ScalarNode && ((ScalarNode) nodeValue).getStyle() == null))) {
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

    protected boolean ignoreAliases(Object data) {
        return false;
    }
}
