/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * @see PyYAML 3.06 for more information
 */
public class ResolverImpl implements Resolver {
    private static final String DEFAULT_SCALAR_TAG = "tag:yaml.org,2002:str";
    private static final String DEFAULT_SEQUENCE_TAG = "tag:yaml.org,2002:seq";
    private static final String DEFAULT_MAPPING_TAG = "tag:yaml.org,2002:map";

    private final static Map<String, List<ResolverTuple>> yamlImplicitResolvers = new HashMap<String, List<ResolverTuple>>();
    private final static Map yamlPathResolvers = new HashMap();

    private LinkedList<Map<? extends Node, String>> resolverExactPaths = new LinkedList<Map<? extends Node, String>>();
    private LinkedList resolverPrefixPaths = new LinkedList();

    public static void addImplicitResolver(final String tag, final Pattern regexp,
            final String first) {
        if (first == null) {
            List<ResolverTuple> curr = yamlImplicitResolvers.get(null);
            if (curr == null) {
                curr = new LinkedList<ResolverTuple>();
                yamlImplicitResolvers.put(null, curr);
            }
            curr.add(new ResolverTuple(tag, regexp));
        } else {
            final char[] chrs = first.toCharArray();
            for (int i = 0, j = chrs.length; i < j; i++) {
                final Character theC = new Character(chrs[i]);
                String key;
                if (theC == 0) {
                    // special case: for null
                    key = "";
                } else {
                    key = String.valueOf(theC);
                }
                List<ResolverTuple> curr = yamlImplicitResolvers.get(key);
                if (curr == null) {
                    curr = new LinkedList<ResolverTuple>();
                    yamlImplicitResolvers.put(key, curr);
                }
                curr.add(new ResolverTuple(tag, regexp));
            }
        }
    }

    /**
     * <pre>
     * Note: `add_path_resolver` is experimental.  The API could be changed.
     * `new_path` is a pattern that is matched against the path from the
     * root to the node that is being considered.  `node_path` elements are
     * tuples `(node_check, index_check)`.  `node_check` is a node class:
     * `ScalarNode`, `SequenceNode`, `MappingNode` or `None`.  `None`
     * matches any kind of a node.  `index_check` could be `None`, a boolean
     * value, a string value, or a number.  `None` and `False` match against
     * any _value_ of sequence and mapping nodes.  `True` matches against
     * any _key_ of a mapping node.  A string `index_check` matches against
     * a mapping value that corresponds to a scalar key which content is
     * equal to the `index_check` value.  An integer `index_check` matches
     * against a sequence value with the index equal to `index_check`.
     * </pre>
     */
    public static void addPathResolver(final String tag, final List path, final Class kind) {
        // TODO addPathResolver is not implemented
        throw new UnsupportedOperationException();
    }

    // TODO this method was not properly imported from PyYAML
    public void descendResolver(final Node currentNode, final Object currentIndex) {
        if (yamlPathResolvers.isEmpty()) {
            return;
        }
        Map exactPaths = new HashMap();
        List prefixPaths = new LinkedList();
        if (currentNode != null) {
            final int depth = resolverPrefixPaths.size();
            for (final Iterator iter = ((List) resolverPrefixPaths.getFirst()).iterator(); iter
                    .hasNext();) {
                final Object[] obj = (Object[]) iter.next();
                List<PathResolverTuple> path = (List<PathResolverTuple>) obj[0];
                if (checkResolverPrefix(depth, path, currentNode, currentIndex)) {
                    if (path.size() > depth) {
                        prefixPaths.add(new Object[] { path, obj[1] });
                    } else {
                        final List resPath = new ArrayList(2);
                        resPath.add(path);
                        resPath.add(obj[1]);
                        exactPaths.put(obj[1], yamlPathResolvers.get(resPath));
                    }
                }
            }
        } else {
            for (final Iterator iter = yamlPathResolvers.keySet().iterator(); iter.hasNext();) {
                final List key = (List) iter.next();
                final List path = (List) key.get(0);
                final Class kind = (Class) key.get(1);
                if (null == path) {
                    exactPaths.put(kind, yamlPathResolvers.get(key));
                } else {
                    prefixPaths.add(key);
                }
            }
        }
        resolverExactPaths.push(exactPaths);
        resolverPrefixPaths.push(prefixPaths);
    }

    public void ascendResolver() {
        if (yamlPathResolvers.isEmpty()) {
            return;
        }
        resolverExactPaths.pop();
        resolverPrefixPaths.pop();
    }

    @SuppressWarnings("unchecked")
    private boolean checkResolverPrefix(final int depth, final List<PathResolverTuple> path,
            final Node currentNode, final Object currentIndex) {
        final PathResolverTuple check = path.get(depth - 1);
        final Object nodeCheck = check.getNodeCheck();
        final Object indexCheck = check.getIndexCheck();
        if (nodeCheck instanceof String) {
            if (!currentNode.getTag().equals(nodeCheck)) {
                return false;
            }
        } else if (nodeCheck != null) {
            if (!((Class) nodeCheck).isInstance(currentNode)) {
                return false;
            }
        }
        if (Boolean.TRUE.equals(indexCheck) && currentIndex != null) {
            return false;
        }
        if ((Boolean.FALSE.equals(indexCheck) || indexCheck == null) && currentIndex == null) {
            return false;
        }
        if (indexCheck instanceof String) {
            if (!(currentIndex instanceof ScalarNode && indexCheck
                    .equals(((ScalarNode) currentIndex).getValue()))) {
                return false;
            }
        } else if (indexCheck instanceof Integer) {
            if (!indexCheck.equals(currentIndex)) {
                return false;
            }
        }
        return true;
    }

    public String resolve(final Class<? extends Node> kind, final String value,
            final boolean implicit) {
        List<ResolverTuple> resolvers = null;
        if (kind.equals(ScalarNode.class) && implicit) {
            if ("".equals(value)) {
                resolvers = yamlImplicitResolvers.get("");
            } else {
                resolvers = yamlImplicitResolvers.get(String.valueOf(value.charAt(0)));
            }
            if (resolvers == null) {
                resolvers = new LinkedList<ResolverTuple>();
            }
            if (yamlImplicitResolvers.containsKey(null)) {
                resolvers.addAll(yamlImplicitResolvers.get(null));
            }
            for (final Iterator<ResolverTuple> iter = resolvers.iterator(); iter.hasNext();) {
                ResolverTuple v = iter.next();
                String tag = v.getTag();
                Pattern regexp = v.getRegexp();
                if (regexp.matcher(value).matches()) {
                    return tag;
                }
            }
        }
        if (!yamlPathResolvers.isEmpty()) {
            final Map<? extends Node, String> exactPaths = resolverExactPaths.getFirst();
            if (exactPaths.containsKey(kind)) {
                return exactPaths.get(kind);
            }
            if (exactPaths.containsKey(null)) {
                return exactPaths.get(null);
            }
        }
        if (kind.equals(ScalarNode.class)) {
            return DEFAULT_SCALAR_TAG;
        } else if (kind.equals(SequenceNode.class)) {
            return DEFAULT_SEQUENCE_TAG;
        } else if (kind.equals(MappingNode.class)) {
            return DEFAULT_MAPPING_TAG;
        }
        return null;
    }

    static {
        addImplicitResolver(
                "tag:yaml.org,2002:bool",
                Pattern
                        .compile("^(?:yes|Yes|YES|no|No|NO|true|True|TRUE|false|False|FALSE|on|On|ON|off|Off|OFF)$"),
                "yYnNtTfFoO");
        addImplicitResolver(
                "tag:yaml.org,2002:float",
                Pattern
                        .compile("^(?:[-+]?(?:[0-9][0-9_]*)\\.[0-9_]*(?:[eE][-+][0-9]+)?|[-+]?(?:[0-9][0-9_]*)?\\.[0-9_]+(?:[eE][-+][0-9]+)?|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$"),
                "-+0123456789.");
        addImplicitResolver(
                "tag:yaml.org,2002:int",
                Pattern
                        .compile("^(?:[-+]?0b[0-1_]+|[-+]?0[0-7_]+|[-+]?(?:0|[1-9][0-9_]*)|[-+]?0x[0-9a-fA-F_]+|[-+]?[1-9][0-9_]*(?::[0-5]?[0-9])+)$"),
                "-+0123456789");
        addImplicitResolver("tag:yaml.org,2002:merge", Pattern.compile("^(?:<<)$"), "<");
        addImplicitResolver("tag:yaml.org,2002:null", Pattern.compile("^(?:~|null|Null|NULL| )$"),
                "~nN\0");
        addImplicitResolver("tag:yaml.org,2002:null", Pattern.compile("^$"), null);
        addImplicitResolver(
                "tag:yaml.org,2002:timestamp",
                Pattern
                        .compile("^(?:[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]|[0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]?(?:[Tt]|[ \t]+)[0-9][0-9]?:[0-9][0-9]:[0-9][0-9](?:\\.[0-9]*)?(?:[ \t]*(?:Z|[-+][0-9][0-9]?(?::[0-9][0-9])?))?)$"),
                "0123456789");
        addImplicitResolver("tag:yaml.org,2002:value", Pattern.compile("^(?:=)$"), "=");
        // The following implicit resolver is only for documentation purposes.
        // It cannot work
        // because plain scalars cannot start with '!', '&', or '*'.
        addImplicitResolver("tag:yaml.org,2002:yaml", Pattern.compile("^(?:!|&|\\*)$"), "!&*");
    }
}
