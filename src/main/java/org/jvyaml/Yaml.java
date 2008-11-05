/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The combinatorial explosion class.
 * 
 * 
 * 
 */
public class Yaml {
    public static final String DEFAULT_SCALAR_TAG = "tag:yaml.org,2002:str";
    public static final String DEFAULT_SEQUENCE_TAG = "tag:yaml.org,2002:seq";
    public static final String DEFAULT_MAPPING_TAG = "tag:yaml.org,2002:map";

    public static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();

    static {
        ESCAPE_REPLACEMENTS.put(new Character('\0'), "0");
        ESCAPE_REPLACEMENTS.put(new Character('\u0007'), "a");
        ESCAPE_REPLACEMENTS.put(new Character('\u0008'), "b");
        ESCAPE_REPLACEMENTS.put(new Character('\u0009'), "t");
        ESCAPE_REPLACEMENTS.put(new Character('\n'), "n");
        ESCAPE_REPLACEMENTS.put(new Character('\u000B'), "v");
        ESCAPE_REPLACEMENTS.put(new Character('\u000C'), "f");
        ESCAPE_REPLACEMENTS.put(new Character('\r'), "r");
        ESCAPE_REPLACEMENTS.put(new Character('\u001B'), "e");
        ESCAPE_REPLACEMENTS.put(new Character('"'), "\"");
        ESCAPE_REPLACEMENTS.put(new Character('\\'), "\\");
        ESCAPE_REPLACEMENTS.put(new Character('\u0085'), "N");
        ESCAPE_REPLACEMENTS.put(new Character('\u00A0'), "_");
    }

    private YAMLConfig config;
    private YAMLFactory factory;

    public Yaml(YAMLConfig config, YAMLFactory factory) {
        this.config = config;
        this.factory = factory;
    }

    public Yaml(YAMLConfig config) {
        this(config, new DefaultYAMLFactory());
    }

    public Yaml(YAMLFactory factory) {
        this(new DefaultYAMLConfig(), factory);
    }

    public Yaml() {
        this(new DefaultYAMLConfig(), new DefaultYAMLFactory());
    }

    public YAMLConfig getConfig() {
        return config;
    }

    public String dump(final Object data) {
        final List<Object> lst = new ArrayList<Object>(1);
        lst.add(data);
        return dumpAll(lst);
    }

    public String dumpAll(final List<Object> data) {
        final StringWriter swe = new StringWriter();
        dumpAll(data, swe);
        return swe.toString();
    }

    public void dump(final Object data, final Writer output) {
        final List<Object> lst = new ArrayList<Object>(1);
        lst.add(data);
        dumpAll(lst, output);
    }

    public void dumpAll(final List<Object> data, final Writer output) {
        final Serializer s = factory.createSerializer(factory.createEmitter(output, config),
                factory.createResolver(), config);
        try {
            s.open();
            final Representer r = factory.createRepresenter(s, config);
            for (final Iterator<Object> iter = data.iterator(); iter.hasNext();) {
                r.represent(iter.next());
            }
        } catch (final java.io.IOException e) {
            throw new YAMLException(e);
        } finally {
            try {
                s.close();
            } catch (final java.io.IOException e) {
                // Nothing to do in this situation
            }
        }
    }

    public Object load(final String io) {
        final Constructor ctor = factory.createConstructor(factory.createComposer(factory
                .createParser(factory.createScanner(io), config), factory.createResolver()));
        if (ctor.checkData()) {
            return ctor.getData();
        } else {
            return null;
        }
    }

    public Object load(final Reader io) {
        final Constructor ctor = factory.createConstructor(factory.createComposer(factory
                .createParser(factory.createScanner(io), config), factory.createResolver()));
        if (ctor.checkData()) {
            return ctor.getData();
        } else {
            return null;
        }
    }

    public List<Object> loadAll(final String io) {
        final List<Object> result = new ArrayList<Object>();
        final Constructor ctor = factory.createConstructor(factory.createComposer(factory
                .createParser(factory.createScanner(io), config), factory.createResolver()));
        while (ctor.checkData()) {
            result.add(ctor.getData());
        }
        return result;
    }

    public List<Object> loadAll(final Reader io) {
        final List<Object> result = new ArrayList<Object>();
        final Constructor ctor = factory.createConstructor(factory.createComposer(factory
                .createParser(factory.createScanner(io), config), factory.createResolver()));
        while (ctor.checkData()) {
            result.add(ctor.getData());
        }
        return result;
    }
}
