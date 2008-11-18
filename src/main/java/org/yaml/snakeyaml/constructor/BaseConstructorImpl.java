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
    private final static Map yamlConstructors = new HashMap();
    private final static Map yamlMultiConstructors = new HashMap();
    private final static Map yamlMultiRegexps = new HashMap();

    public YamlConstructor getYamlConstructor(final Object key) {
        return (YamlConstructor) yamlConstructors.get(key);
    }

    public YamlMultiConstructor getYamlMultiConstructor(final Object key) {
        return (YamlMultiConstructor) yamlMultiConstructors.get(key);
    }

    public Pattern getYamlMultiRegexp(final Object key) {
        return (Pattern) yamlMultiRegexps.get(key);
    }

    public Set getYamlMultiRegexps() {
        return yamlMultiRegexps.keySet();
    }

    public static void addConstructor(final String tag, final YamlConstructor ctor) {
        yamlConstructors.put(tag, ctor);
    }

    public static void addMultiConstructor(final String tagPrefix, final YamlMultiConstructor ctor) {
        yamlMultiConstructors.put(tagPrefix, ctor);
        yamlMultiRegexps.put(tagPrefix, Pattern.compile("^" + tagPrefix));
    }

    private Composer composer;
    private Map constructedObjects = new HashMap();
    private Map recursiveObjects = new HashMap();

    public BaseConstructorImpl(final Composer composer) {
        this.composer = composer;
    }

    public boolean checkData() {
        return composer.checkNode();
    }

    public Object getData() {
        if (composer.checkNode()) {
            Node node = composer.getNode();
            if (null != node) {
                return constructDocument(node);
            }
        }
        return null;
    }

    public Object getSingleData() {
        // Ensure that the stream contains a single document and construct it
        Node node = composer.getSingleNode();
        if (null != node) {
            return constructDocument(node);
        }
        return null;
    }

    public Object constructDocument(final Node node) {
        final Object data = constructObject(node);
        constructedObjects.clear();
        recursiveObjects.clear();
        return data;
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

    public Object constructObject(final Node node) {
        if (constructedObjects.containsKey(node)) {
            return constructedObjects.get(node);
        }
        if (recursiveObjects.containsKey(node)) {
            throw new ConstructorException(null, "found recursive node", null);
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

    public Object constructPrimitive(final Node node) {
        if (node instanceof ScalarNode) {
            return constructScalar(node);
        } else if (node instanceof SequenceNode) {
            return constructSequence(node);
        } else if (node instanceof MappingNode) {
            return constructMapping(node);
        } else {
            System.err.println(node.getTag());
        }
        return null;
    }

    public Object constructScalar(final Node node) {
        if (!(node instanceof ScalarNode)) {
            if (node instanceof MappingNode) {
                final Map vals = ((Map) node.getValue());
                for (final Iterator iter = vals.keySet().iterator(); iter.hasNext();) {
                    final Node key = (Node) iter.next();
                    if ("tag:yaml.org,2002:value".equals(key.getTag())) {
                        return constructScalar((Node) vals.get(key));
                    }
                }
            }
            throw new ConstructorException(null, "expected a scalar node, but found "
                    + node.getClass().getName(), null);
        }
        return node.getValue();
    }

    public Object constructPrivateType(final Node node) {
        Object val = null;
        if (node.getValue() instanceof Map) {
            val = constructMapping(node);
        } else if (node.getValue() instanceof List) {
            val = constructSequence(node);
        } else {
            val = node.getValue().toString();
        }
        return new PrivateType(node.getTag(), val);
    }

    public Object constructSequence(final Node node) {
        if (!(node instanceof SequenceNode)) {
            throw new ConstructorException(null, "expected a sequence node, but found "
                    + node.getClass().getName(), null);
        }
        final List internal = (List) node.getValue();
        final List val = new ArrayList(internal.size());
        for (final Iterator iter = internal.iterator(); iter.hasNext();) {
            val.add(constructObject((Node) iter.next()));
        }
        return val;
    }

    public Object constructMapping(final Node node) {
        if (!(node instanceof MappingNode)) {
            throw new ConstructorException(null, "expected a mapping node, but found "
                    + node.getClass().getName(), null);
        }
        Map mapping = new HashMap();
        List merge = null;
        final Map val = (Map) node.getValue();
        for (final Iterator iter = val.keySet().iterator(); iter.hasNext();) {
            final Node key_v = (Node) iter.next();
            final Node value_v = (Node) val.get(key_v);
            if (key_v.getTag().equals("tag:yaml.org,2002:merge")) {
                if (merge != null) {
                    throw new ConstructorException("while constructing a mapping",
                            "found duplicate merge key", null);
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
                            throw new ConstructorException("while constructing a mapping",
                                    "expected a mapping for merging, but found "
                                            + subnode.getClass().getName(), null);
                        }
                        merge.add(0, constructMapping(subnode));
                    }
                } else {
                    throw new ConstructorException("while constructing a mapping",
                            "expected a mapping or list of mappings for merging, but found "
                                    + value_v.getClass().getName(), null);
                }
            } else if (key_v.getTag().equals("tag:yaml.org,2002:value")) {
                if (mapping.containsKey("=")) {
                    throw new ConstructorException("while construction a mapping",
                            "found duplicate value key", null);
                }
                mapping.put("=", constructObject(value_v));
            } else {
                mapping.put(constructObject(key_v), constructObject(value_v));
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

    public Object constructPairs(final Node node) {
        if (!(node instanceof MappingNode)) {
            throw new ConstructorException(null, "expected a mapping node, but found "
                    + node.getClass().getName(), null);
        }
        final List value = new LinkedList();
        final Map vals = (Map) node.getValue();
        for (final Iterator iter = vals.keySet().iterator(); iter.hasNext();) {
            final Node key = (Node) iter.next();
            final Node val = (Node) vals.get(key);
            value.add(new Object[] { constructObject(key), constructObject(val) });
        }
        return value;
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
            return self.constructSequence(node);
        }
    };
    public final static YamlConstructor CONSTRUCT_MAPPING = new YamlConstructor() {
        public Object call(final Constructor self, final Node node) {
            return self.constructMapping(node);
        }
    };

}
