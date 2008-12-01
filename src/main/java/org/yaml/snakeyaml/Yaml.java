/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorImpl;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.serializer.Serializer;

/**
 * Public YAML interface
 */
public class Yaml {
    private DumperOptions dumperOptions;
    @SuppressWarnings("unchecked")
    private Map<Class, AbstractRepresenter> representers = new HashMap<Class, AbstractRepresenter>();

    public Yaml(DumperOptions options) {
        this.dumperOptions = options;
    }

    public Yaml() {
        this(new DumperOptions());
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
        Serializer s = new Serializer(new Emitter(output, dumperOptions), new Resolver(),
                dumperOptions);
        try {
            s.open();
            Representer r = new Representer(s, representers, dumperOptions.getDefaultStyle(), null);
            for (AbstractRepresenter customRepresenter : representers.values()) {
                customRepresenter.setParent(r);
            }
            for (Iterator<Object> iter = data.iterator(); iter.hasNext();) {
                r.represent(iter.next());
            }
        } catch (java.io.IOException e) {
            throw new YAMLException(e);
        }
        try {
            s.close();
        } catch (java.io.IOException e) {
            throw new YAMLException(e);
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
        Constructor ctor = new ConstructorImpl(new Composer(new ParserImpl(new ScannerImpl(
                new Reader(yaml))), new Resolver()));
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
        Constructor ctor = new ConstructorImpl(new Composer(new ParserImpl(new ScannerImpl(
                new Reader(io))), new Resolver()));
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
    public Iterable<Object> loadAll(final String yaml) {
        final Constructor ctor = new ConstructorImpl(new Composer(new ParserImpl(new ScannerImpl(
                new Reader(yaml))), new Resolver()));
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
    public Iterable<Object> loadAll(final InputStream yaml) {
        final Constructor ctor = new ConstructorImpl(new Composer(new ParserImpl(new ScannerImpl(
                new Reader(yaml))), new Resolver()));
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

    // Customisers

    /**
     * Add a representer for the given type. Representer is accepting an
     * instance of the given data type and producing the corresponding
     * representation node.
     * 
     * @param clazz
     * @param representer
     */
    @SuppressWarnings("unchecked")
    public void addRepresenter(Class clazz, AbstractRepresenter representer) {
        representers.put(clazz, representer);
    }

    /**
     * Add a representer for the given type. Multi-representer is accepting an
     * instance of the given data type or subtype and producing the
     * corresponding representation node.
     * 
     * @param clazz
     * @param representer
     */
    @SuppressWarnings("unchecked")
    public void addMultiRepresenter(Class clazz, Represent representer) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add an implicit scalar detector. If an implicit scalar value matches the
     * given regexp, the corresponding tag is assigned to the scalar. first is a
     * sequence of possible initial characters or None.
     * 
     * @param tag -
     *            tag to assign to the node
     * @param regexp -
     *            regular expression to match against
     * @param first -
     *            a sequence of possible initial characters or None
     */
    public void addImplicitResolver(String tag, Pattern regexp, String first) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add a path based resolver for the given tag. A path is a list of keys
     * that forms a path to a node in the representation tree. Keys can be
     * string values, integers, or None.
     * 
     * @param tag
     * @param path
     * @param kind
     */
    public void addPathResolver(String tag, List<Object> path, Object kind) {
        throw new UnsupportedOperationException();
    }
}
