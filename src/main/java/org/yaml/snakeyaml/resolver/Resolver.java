/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.resolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.nodes.NodeId;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class Resolver {
    private static final String DEFAULT_SCALAR_TAG = "tag:yaml.org,2002:str";
    private static final String DEFAULT_SEQUENCE_TAG = "tag:yaml.org,2002:seq";
    private static final String DEFAULT_MAPPING_TAG = "tag:yaml.org,2002:map";

    private Map<String, List<ResolverTuple>> yamlImplicitResolvers;
    private boolean useRE = true;

    public Resolver() {
        this(true);
    }

    /**
     * @param useRE
     *            - if useRE is false then all the scalars are Strings
     */
    public Resolver(boolean useRE) {
        this.useRE = useRE;
        if (useRE) {
            yamlImplicitResolvers = new HashMap<String, List<ResolverTuple>>();
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
            addImplicitResolver("tag:yaml.org,2002:null", Pattern
                    .compile("^(?:~|null|Null|NULL| )$"), "~nN\0");
            addImplicitResolver("tag:yaml.org,2002:null", Pattern.compile("^$"), null);
            addImplicitResolver(
                    "tag:yaml.org,2002:timestamp",
                    Pattern
                            .compile("^(?:[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]|[0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]?(?:[Tt]|[ \t]+)[0-9][0-9]?:[0-9][0-9]:[0-9][0-9](?:\\.[0-9]*)?(?:[ \t]*(?:Z|[-+][0-9][0-9]?(?::[0-9][0-9])?))?)$"),
                    "0123456789");
            addImplicitResolver("tag:yaml.org,2002:value", Pattern.compile("^(?:=)$"), "=");
            // The following implicit resolver is only for documentation
            // purposes.
            // It cannot work
            // because plain scalars cannot start with '!', '&', or '*'.
            addImplicitResolver("tag:yaml.org,2002:yaml", Pattern.compile("^(?:!|&|\\*)$"), "!&*");
        }
    }

    public void addImplicitResolver(String tag, Pattern regexp, String first) {
        if (first == null) {
            List<ResolverTuple> curr = yamlImplicitResolvers.get(null);
            if (curr == null) {
                curr = new LinkedList<ResolverTuple>();
                yamlImplicitResolvers.put(null, curr);
            }
            curr.add(new ResolverTuple(tag, regexp));
        } else {
            char[] chrs = first.toCharArray();
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

    public String resolve(NodeId kind, String value, boolean implicit) {
        List<ResolverTuple> resolvers = null;
        if (useRE && kind == NodeId.scalar && implicit) {
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
        // if (!yamlPathResolvers.isEmpty()) {
        // final Map<? extends Node, String> exactPaths =
        // resolverExactPaths.getFirst();
        // if (exactPaths.containsKey(kind)) {
        // return exactPaths.get(kind);
        // }
        // if (exactPaths.containsKey(null)) {
        // return exactPaths.get(null);
        // }
        // }
        switch (kind) {
        case scalar:
            return DEFAULT_SCALAR_TAG;
        case sequence:
            return DEFAULT_SEQUENCE_TAG;
        default:
            return DEFAULT_MAPPING_TAG;
        }
    }
}
