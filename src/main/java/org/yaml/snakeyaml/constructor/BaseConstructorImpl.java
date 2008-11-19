/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jvyaml.PrivateType;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * @see PyYAML 3.06 for more information
 */
public class BaseConstructorImpl implements Constructor {
    private final static Map<String, YamlConstructor> yamlConstructors = new HashMap<String, YamlConstructor>();
    private final static Map<String, YamlMultiConstructor> yamlMultiConstructors = new HashMap<String, YamlMultiConstructor>();
    private final static Map<String, Pattern> yamlMultiRegexps = new HashMap<String, Pattern>();

    private Composer composer;
    private Map<Node, Object> constructedObjects;
    private Map<Node, Object> recursiveObjects;
    private boolean deepConstruct;

    public BaseConstructorImpl(final Composer composer) {
        this.composer = composer;
        constructedObjects = new HashMap<Node, Object>();
        recursiveObjects = new HashMap<Node, Object>();
        deepConstruct = false;
    }

    public boolean checkData() {
        // If there are more documents available?
        return composer.checkNode();
    }

    public Object getData() {
        // Construct and return the next document.
        if (composer.checkNode()) {
            Node node = composer.getNode();
            return constructDocument(node);
        }
        return null;
    }

    public Object getSingleData() {
        // Ensure that the stream contains a single document and construct it
        Node node = composer.getSingleNode();
        if (node != null) {
            return constructDocument(node);
        }
        return null;
    }

    private Object constructDocument(Node node) {
        Object data = constructObject(node, false);
        constructedObjects.clear();
        recursiveObjects.clear();
        deepConstruct = false;
        return data;
    }

    private Object constructObject(final Node node, boolean deep) {
        boolean oldDeep;
        if (deep) {
            oldDeep = deepConstruct;
            deepConstruct = true;
        }
        if (constructedObjects.containsKey(node)) {
            return constructedObjects.get(node);
        }
        if (recursiveObjects.containsKey(node)) {
            throw new ConstructorException(null, null, "found unconstructable recursive node", node
                    .getStartMark());
        }
        recursiveObjects.put(node, null);
        YamlConstructor ctor = getYamlConstructor(node.getTag());
        if (ctor == null) {
            boolean through = true;
            for (final Iterator iter = getYamlMultiRegexps().iterator(); iter.hasNext();) {
                final String tagPrefix = (String) iter.next();
                final Pattern reg = getYamlMultiRegexp(tagPrefix);
                if (reg.matcher(node.getTag()).find()) {
                    final String tagSuffix = node.getTag().substring(tagPrefix.length());
                    ctor = new YamlMultiAdapter(getYamlMultiConstructor(tagPrefix), tagSuffix);
                    through = false;
                    break;
                }
            }
            if (through) {
                final YamlMultiConstructor xctor = getYamlMultiConstructor(null);
                if (null != xctor) {
                    ctor = new YamlMultiAdapter(xctor, node.getTag());
                } else {
                    ctor = getYamlConstructor(null);
                    if (ctor == null) {
                        ctor = CONSTRUCT_PRIMITIVE;
                    }
                }
            }
        }
        final Object data = ctor.call(this, node);
        constructedObjects.put(node, data);
        recursiveObjects.remove(node);
        return data;
    }

    public Object constructScalar(final Node node) {
        if (!(node instanceof ScalarNode)) {
            throw new ConstructorException(null, null, "expected a scalar node, but found "
                    + node.getNodeId(), node.getStartMark());
        }
        return node.getValue();
    }

    @SuppressWarnings("unchecked")
    public Object constructSequence(final Node node, boolean deep) {
        if (!(node instanceof SequenceNode)) {
            throw new ConstructorException(null, null, "expected a sequence node, but found "
                    + node.getNodeId(), node.getStartMark());
        }
        final List<Node> internal = (List<Node>) node.getValue();
        final List<Object> result = new ArrayList<Object>(internal.size());
        for (final Iterator<Node> iter = internal.iterator(); iter.hasNext();) {
            Node child = iter.next();
            result.add(constructObject(child, deep));
        }
        return result;
    }

    public static void addMultiConstructor(final String tagPrefix, final YamlMultiConstructor ctor) {
        yamlMultiConstructors.put(tagPrefix, ctor);
        yamlMultiRegexps.put(tagPrefix, Pattern.compile("^" + tagPrefix));
    }

    public static class YamlMultiAdapter implements YamlConstructor {
        private YamlMultiConstructor ctor;
        private String prefix;

        public YamlMultiAdapter(final YamlMultiConstructor ctor, final String prefix) {
            this.ctor = ctor;
            this.prefix = prefix;
        }

        public Object call(final Constructor self, final Node node) {
            return ctor.call(self, this.prefix, node);
        }
    }

    public Object constructPrimitive(final Node node) {
        if (node instanceof ScalarNode) {
            return constructScalar(node);
        } else if (node instanceof SequenceNode) {
            return constructSequence(node, false);
        } else if (node instanceof MappingNode) {
            return constructMapping(node);
        } else {
            System.err.println(node.getTag());
        }
        return null;
    }

    public Object constructPrivateType(final Node node) {
        Object val = null;
        if (node.getValue() instanceof Map) {
            val = constructMapping(node);
        } else if (node.getValue() instanceof List) {
            val = constructSequence(node, false);
        } else {
            val = node.getValue().toString();
        }
        return new PrivateType(node.getTag(), val);
    }

    public Object constructMapping(final Node node) {
        if (!(node instanceof MappingNode)) {
            throw new ConstructorException(null, null, "expected a mapping node, but found "
                    + node.getNodeId(), node.getStartMark());
        }
        Map mapping = new HashMap();
        List merge = null;
        final Map val = (Map) node.getValue();
        for (final Iterator iter = val.keySet().iterator(); iter.hasNext();) {
            final Node key_v = (Node) iter.next();
            final Node value_v = (Node) val.get(key_v);
            if (key_v.getTag().equals("tag:yaml.org,2002:merge")) {
                if (merge != null) {
                    throw new ConstructorException("while constructing a mapping", node
                            .getStartMark(), "found duplicate merge key", key_v.getStartMark());
                }
                if (value_v instanceof MappingNode) {
                    merge = new LinkedList();
                    merge.add(constructMapping(value_v));
                } else if (value_v instanceof SequenceNode) {
                    merge = new LinkedList();
                    final List vals = (List) value_v.getValue();
                    for (final Iterator sub = vals.iterator(); sub.hasNext();) {
                        final Node subnode = (Node) sub.next();
                        if (!(subnode instanceof MappingNode)) {
                            throw new ConstructorException("while constructing a mapping", node
                                    .getStartMark(), "expected a mapping for merging, but found "
                                    + subnode.getNodeId(), subnode.getStartMark());
                        }
                        merge.add(0, constructMapping(subnode));
                    }
                } else {
                    throw new ConstructorException("while constructing a mapping", node
                            .getStartMark(),
                            "expected a mapping or list of mappings for merging, but found "
                                    + value_v.getNodeId(), value_v.getStartMark());
                }
            } else if (key_v.getTag().equals("tag:yaml.org,2002:value")) {
                if (mapping.containsKey("=")) {
                    throw new ConstructorException("while construction a mapping", node
                            .getStartMark(), "found duplicate value key", key_v.getStartMark());
                }
                mapping.put("=", constructObject(value_v, false));
            } else {
                mapping.put(constructObject(key_v, false), constructObject(value_v, false));
            }
        }
        if (null != merge) {
            merge.add(mapping);
            mapping = new HashMap();
            for (final Iterator iter = merge.iterator(); iter.hasNext();) {
                mapping.putAll((Map) iter.next());
            }
        }
        return mapping;
    }

    public Object constructPairs(final Node node, boolean deep) {
        if (!(node instanceof MappingNode)) {
            throw new ConstructorException(null, null, "expected a mapping node, but found "
                    + node.getNodeId(), node.getStartMark());
        }
        final List<Object> pairs = new LinkedList<Object>();
        final Map<Node, Node> vals = (Map<Node, Node>) node.getValue();
        for (final Iterator<Node> iter = vals.keySet().iterator(); iter.hasNext();) {
            final Node keyNode = iter.next();
            final Node valueNode = vals.get(keyNode);
            Object key = constructObject(keyNode, deep);
            Object value = constructObject(valueNode, deep);
            pairs.add(new Object[] { key, value });
        }
        return pairs;
    }

    public final static YamlConstructor CONSTRUCT_PRIMITIVE = new YamlConstructor() {
        public Object call(final Constructor self, final Node node) {
            return self.constructPrimitive(node);
        }
    };
    public final static YamlConstructor CONSTRUCT_SCALAR = new YamlConstructor() {
        public Object call(final Constructor self, final Node node) {
            return self.constructScalar(node);
        }
    };
    public final static YamlConstructor CONSTRUCT_PRIVATE = new YamlConstructor() {
        public Object call(final Constructor self, final Node node) {
            return self.constructPrivateType(node);
        }
    };
    public final static YamlConstructor CONSTRUCT_SEQUENCE = new YamlConstructor() {
        public Object call(final Constructor self, final Node node) {
            return self.constructSequence(node, false);
        }
    };
    public final static YamlConstructor CONSTRUCT_MAPPING = new YamlConstructor() {
        public Object call(final Constructor self, final Node node) {
            return self.constructMapping(node);
        }
    };

    public YamlConstructor getYamlConstructor(final Object key) {
        return yamlConstructors.get(key);
    }

    public YamlMultiConstructor getYamlMultiConstructor(final Object key) {
        return yamlMultiConstructors.get(key);
    }

    public Pattern getYamlMultiRegexp(final Object key) {
        return yamlMultiRegexps.get(key);
    }

    public Set<String> getYamlMultiRegexps() {
        return yamlMultiRegexps.keySet();
    }

    public static void addConstructor(final String tag, final YamlConstructor ctor) {
        yamlConstructors.put(tag, ctor);
    }

}
