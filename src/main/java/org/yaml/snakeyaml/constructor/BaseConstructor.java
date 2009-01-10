/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML< /a> for more information
 */
public class BaseConstructor {
    protected Map<String, Construct> yamlConstructors = new HashMap<String, Construct>();

    private Composer composer;
    private Map<Node, Object> constructedObjects;
    private Map<Node, Object> recursiveObjects;

    protected Class<? extends Object> rootClass;

    /*
     * because Java does not have generators 'deep' is dropped. Multi
     * constructors are not supported.
     */
    public BaseConstructor() {
        constructedObjects = new HashMap<Node, Object>();
        recursiveObjects = new HashMap<Node, Object>();
        rootClass = Object.class;
    }

    public void setComposer(Composer composer) {
        this.composer = composer;
    }

    public boolean checkData() {
        // If there are more documents available?
        return composer.checkNode();
    }

    public Object getData() {
        // Construct and return the next document.
        composer.checkNode();
        Node node = composer.getNode();
        return constructDocument(rootClass, node);
    }

    public Object getSingleData() {
        // Ensure that the stream contains a single document and construct it
        Node node = composer.getSingleNode();
        if (node != null) {
            return constructDocument(rootClass, node);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T constructDocument(Class<T> clazz, Node node) {
        Object data = constructObject(clazz, node);
        constructedObjects.clear();
        recursiveObjects.clear();
        return (T) data;
    }

    @SuppressWarnings("unchecked")
    protected <T> T constructObject(Class<T> clazz, Node node) {
        if (constructedObjects.containsKey(node)) {
            return (T) constructedObjects.get(node);
        }
        if (recursiveObjects.containsKey(node)) {
            throw new ConstructorException(null, null, "found unconstructable recursive node", node
                    .getStartMark());
        }
        recursiveObjects.put(node, null);
        Object data = callConstructor(clazz, node);
        constructedObjects.put(node, data);
        recursiveObjects.remove(node);
        return (T) data;
    }

    @SuppressWarnings("unchecked")
    protected <T> T callConstructor(Class<T> clazz, Node node) {
        Object data = null;
        Construct constructor = null;
        if (Object.class.equals(clazz)) {// TODO do we need to check it ?
            constructor = yamlConstructors.get(node.getTag());
            if (constructor == null) {
                if (yamlConstructors.containsKey(null)) {
                    constructor = yamlConstructors.get(null);
                    data = constructor.construct(clazz, node);
                } else if (node instanceof ScalarNode) {
                    data = constructScalar((ScalarNode) node);
                } else if (node instanceof SequenceNode) {
                    data = constructSequence((SequenceNode) node);
                } else if (node instanceof MappingNode) {
                    data = constructMapping((MappingNode) node);
                } else {
                    throw new YAMLException("Unknown node: " + node);
                }
            } else {
                data = constructor.construct(clazz, node);
            }
        } else {
            throw new YAMLException("BeanConstructor must be used when class is known for node: "
                    + node);
        }
        return (T) data;
    }

    protected Object constructScalar(ScalarNode node) {
        return node.getValue();
    }

    protected List<Object> createDefaultList(int initSize) {
        return new LinkedList<Object>();
    }

    protected List<? extends Object> constructSequence(SequenceNode node) {
        List<Node> nodeValue = (List<Node>) node.getValue();
        List<Object> result = createDefaultList(nodeValue.size());
        for (Iterator<Node> iter = nodeValue.iterator(); iter.hasNext();) {
            Node child = iter.next();
            result.add(constructObject(Object.class, child));
        }
        return result;
    }

    protected Map<Object, Object> createDefaultMap() {
        // respect order from YAML document
        return new LinkedHashMap<Object, Object>();
    }

    protected Map<Object, Object> constructMapping(MappingNode node) {
        Map<Object, Object> mapping = createDefaultMap();
        List<Node[]> nodeValue = (List<Node[]>) node.getValue();
        for (Iterator<Node[]> iter = nodeValue.iterator(); iter.hasNext();) {
            Node[] tuple = iter.next();
            Node keyNode = tuple[0];
            Node valueNode = tuple[1];
            Object key = constructObject(Object.class, keyNode);
            if (key != null) {
                try {
                    key.hashCode();// check circular dependencies
                } catch (Exception e) {
                    throw new ConstructorException("while constructing a mapping", node
                            .getStartMark(), "found unacceptable key " + key, tuple[0]
                            .getStartMark());
                }
            }
            Object value = constructObject(Object.class, valueNode);
            mapping.put(key, value);
        }
        return mapping;
    }

    protected List<Object[]> constructPairs(MappingNode node) {
        List<Object[]> pairs = new LinkedList<Object[]>();
        List<Node[]> nodeValue = (List<Node[]>) node.getValue();
        for (Iterator<Node[]> iter = nodeValue.iterator(); iter.hasNext();) {
            Node[] tuple = iter.next();
            Object key = constructObject(Object.class, tuple[0]);
            Object value = constructObject(Object.class, tuple[1]);
            pairs.add(new Object[] { key, value });
        }
        return pairs;
    }
}
