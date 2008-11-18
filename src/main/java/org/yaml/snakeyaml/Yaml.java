/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jvyaml.Constructor;
import org.jvyaml.DefaultYAMLConfig;
import org.jvyaml.DefaultYAMLFactory;
import org.jvyaml.Representer;
import org.jvyaml.Serializer;
import org.jvyaml.YAMLFactory;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Public YAML interface
 */
public class Yaml {

    private YamlConfig config;
    private YAMLFactory factory;

    public Yaml(YamlConfig config, YAMLFactory factory) {
        this.config = config;
        this.factory = factory;
    }

    public Yaml(YamlConfig config) {
        this(config, new DefaultYAMLFactory());
    }

    public Yaml(YAMLFactory factory) {
        this(new DefaultYAMLConfig(), factory);
    }

    public Yaml() {
        this(new DefaultYAMLConfig(), new DefaultYAMLFactory());
    }

    public YamlConfig getConfig() {
        return config;
    }

    /**
     * Serialize a Java object into a YAML String.
     * 
     * @param data -
     *            Java object to be Serialized to YAML
     * @return YAML String
     */
    public String dump(final Object data) {
        final List<Object> lst = new ArrayList<Object>(1);
        lst.add(data);
        return dumpAll(lst);
    }

    /**
     * Serialize a sequence of Java objects into a YAML String.
     * 
     * @param data -
     *            Iterator with Objects
     * @return - YAML String with all the objects in proper sequence
     */
    public String dumpAll(final Iterable<Object> data) {
        final StringWriter swe = new StringWriter();
        dumpAll(data, swe);
        return swe.toString();
    }

    /**
     * Serialize a Java object into a YAML stream.
     * 
     * @param data -
     *            Java object to be Serialized to YAML
     * @param output -
     *            stream to write to
     */
    public void dump(final Object data, final Writer output) {
        final List<Object> lst = new ArrayList<Object>(1);
        lst.add(data);
        dumpAll(lst, output);
    }

    /**
     * Serialize a sequence of Java objects into a YAML stream.
     * 
     * @param data -
     *            Iterator with Objects
     * @param output -
     *            stream to write to
     */
    public void dumpAll(final Iterable<Object> data, final Writer output) {
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

    /**
     * Parse the first YAML document in a String and produce the corresponding
     * Java object. (Because the encoding in known BOM is not respected.)
     * 
     * @param yaml -
     *            YAML data to load from (BOM must not be present)
     * @return parsed object
     */
    public Object load(final String yaml) {
        final Constructor ctor = factory.createConstructor(factory.createComposer(factory
                .createParser(factory.createScanner(yaml)), factory.createResolver()));
        return ctor.getSingleData();
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * Java object.
     * 
     * @param io -
     *            data to load from (BOM is respected and ignored)
     * @return parsed object
     */
    public Object load(final InputStream io) {
        final Constructor ctor = factory.createConstructor(factory.createComposer(factory
                .createParser(factory.createScanner(io)), factory.createResolver()));
        return ctor.getSingleData();
    }

    /**
     * Parse all YAML documents in a String and produce corresponding Java
     * objects. (Because the encoding in known BOM is not respected.)
     * 
     * @param yaml -
     *            YAML data to load from (BOM must not be present)
     * @return an iterator over the parsed Java objects in this String in proper
     *         sequence
     */
    @SuppressWarnings("unchecked")
    public Iterable<Object> loadAll(final String yaml) {
        final Constructor ctor = factory.createConstructor(factory.createComposer(factory
                .createParser(factory.createScanner(yaml)), factory.createResolver()));
        Iterator<Object> result = new Iterator<Object>() {
            public boolean hasNext() {
                return ctor.checkData();
            }

            public Object next() {
                return ctor.getData();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new YamlIterable(result);
    }

    /**
     * Parse all YAML documents in a stream and produce corresponding Java
     * objects.
     * 
     * @param yaml -
     *            YAML data to load from (BOM is respected and ignored)
     * @return an iterator over the parsed Java objects in this stream in proper
     *         sequence
     */
    @SuppressWarnings("unchecked")
    public Iterable<Object> loadAll(final InputStream yaml) {
        final Constructor ctor = factory.createConstructor(factory.createComposer(factory
                .createParser(factory.createScanner(yaml)), factory.createResolver()));
        Iterator<Object> result = new Iterator<Object>() {
            public boolean hasNext() {
                return ctor.checkData();
            }

            public Object next() {
                return ctor.getData();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new YamlIterable(result);
    }

    private class YamlIterable implements Iterable<Object> {
        private Iterator<Object> iterator;

        public YamlIterable(Iterator<Object> iterator) {
            this.iterator = iterator;
        }

        public Iterator<Object> iterator() {
            return iterator;
        }

    }
}
