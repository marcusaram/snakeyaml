/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Represent;

/**
 * Public YAML interface
 */
public class Yaml {
    private Dumper dumper;
    private Loader loader;

    public Yaml(DumperOptions options) {
        this.dumper = new Dumper(options);
    }

    public Yaml(Dumper dumper) {
        this(new Loader(new Constructor()), dumper);
    }

    public Yaml(Loader loader) {
        this(loader, new Dumper(new DumperOptions()));
    }

    public Yaml(Loader loader, Dumper dumper) {
        this.loader = loader;
        this.dumper = dumper;
    }

    public Yaml() {
        this(new Loader(new Constructor()), new Dumper(new DumperOptions()));
    }

    /**
     * Serialize a Java object into a YAML String.
     * 
     * @param data
     *            - Java object to be Serialized to YAML
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
     * @param data
     *            - Iterator with Objects
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
     * @param data
     *            - Java object to be Serialized to YAML
     * @param output
     *            - stream to write to
     */
    public void dump(final Object data, final Writer output) {
        final List<Object> lst = new ArrayList<Object>(1);
        lst.add(data);
        dumpAll(lst, output);
    }

    /**
     * Serialize a sequence of Java objects into a YAML stream.
     * 
     * @param data
     *            - Iterator with Objects
     * @param output
     *            - stream to write to
     */
    public void dumpAll(final Iterable<Object> data, final Writer output) {
        dumper.dump(data, output);
    }

    /**
     * Parse the first YAML document in a String and produce the corresponding
     * Java object. (Because the encoding in known BOM is not respected.)
     * 
     * @param yaml
     *            - YAML data to load from (BOM must not be present)
     * @return parsed object
     */
    public Object load(String yaml) {
        return loader.load(yaml);
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * Java object.
     * 
     * @param io
     *            - data to load from (BOM is respected and ignored)
     * @return parsed object
     */
    public Object load(InputStream io) {
        return loader.load(io);
    }

    /**
     * Parse all YAML documents in a String and produce corresponding Java
     * objects. (Because the encoding in known BOM is not respected.)
     * 
     * @param yaml
     *            - YAML data to load from (BOM must not be present)
     * @return an iterator over the parsed Java objects in this String in proper
     *         sequence
     */
    public Iterable<Object> loadAll(String yaml) {
        return loader.loadAll(yaml);
    }

    /**
     * Parse all YAML documents in a stream and produce corresponding Java
     * objects.
     * 
     * @param yaml
     *            - YAML data to load from (BOM is respected and ignored)
     * @return an iterator over the parsed Java objects in this stream in proper
     *         sequence
     */
    public Iterable<Object> loadAll(InputStream yaml) {
        return loader.loadAll(yaml);
    }

    // Customisers

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
     * @param tag
     *            - tag to assign to the node
     * @param regexp
     *            - regular expression to match against
     * @param first
     *            - a sequence of possible initial characters or None
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
