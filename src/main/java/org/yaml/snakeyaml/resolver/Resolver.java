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
    private RagelMachine ragelScanner = new RagelMachine();

    public Resolver() {
        yamlImplicitResolvers = new HashMap<String, List<ResolverTuple>>();
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

    /*
     * Use Ragel instead of Regular Expressions as in PyYAML to improve
     * performance
     */
    public String resolve(NodeId kind, String value, boolean implicit) {
        if (kind == NodeId.scalar && implicit) {
            if (value.length() > 0) {
                List<ResolverTuple> resolvers = yamlImplicitResolvers.get(String.valueOf(value
                        .charAt(0)));
                if (resolvers != null) {
                    for (Iterator<ResolverTuple> iter = resolvers.iterator(); iter.hasNext();) {
                        ResolverTuple v = iter.next();
                        String tag = v.getTag();
                        Pattern regexp = v.getRegexp();
                        if (regexp.matcher(value).matches()) {
                            return tag;
                        }
                    }
                }
            }
            String tag = ragelScanner.scan(value);
            if (tag != null) {
                return tag;
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
