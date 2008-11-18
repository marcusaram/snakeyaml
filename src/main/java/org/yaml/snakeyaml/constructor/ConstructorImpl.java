/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jvyaml.SafeConstructorImpl;
import org.yaml.snakeyaml.composer.Composer;

/**
 * @see PyYAML 3.06 for more information
 */
public class ConstructorImpl extends SafeConstructorImpl {
    private final static Map yamlConstructors = new HashMap();
    private final static Map yamlMultiConstructors = new HashMap();
    private final static Map yamlMultiRegexps = new HashMap();

    public YamlConstructor getYamlConstructor(final Object key) {
        YamlConstructor mine = (YamlConstructor) yamlConstructors.get(key);
        if (mine == null) {
            mine = super.getYamlConstructor(key);
        }
        return mine;
    }

    public YamlMultiConstructor getYamlMultiConstructor(final Object key) {
        YamlMultiConstructor mine = (YamlMultiConstructor) yamlMultiConstructors.get(key);
        if (mine == null) {
            mine = super.getYamlMultiConstructor(key);
        }
        return mine;
    }

    public Pattern getYamlMultiRegexp(final Object key) {
        Pattern mine = (Pattern) yamlMultiRegexps.get(key);
        if (mine == null) {
            mine = super.getYamlMultiRegexp(key);
        }
        return mine;
    }

    public Set getYamlMultiRegexps() {
        final Set all = new HashSet(super.getYamlMultiRegexps());
        all.addAll(yamlMultiRegexps.keySet());
        return all;
    }

    public static void addConstructor(final String tag, final YamlConstructor ctor) {
        yamlConstructors.put(tag, ctor);
    }

    public static void addMultiConstructor(final String tagPrefix, final YamlMultiConstructor ctor) {
        yamlMultiConstructors.put(tagPrefix, ctor);
        yamlMultiRegexps.put(tagPrefix, Pattern.compile("^" + tagPrefix));
    }

    public ConstructorImpl(final Composer composer) {
        super(composer);
    }

}
