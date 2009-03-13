/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.InputStream;
import java.io.StringReader;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.resolver.Resolver;

/**
 * Public YAML interface. Each Thread must have its own instance.
 */
public class GenericsYaml {
    private String name;

    private GenericsYaml() {
        this.name = "GenericsYaml:" + System.identityHashCode(this);
    }

    /**
     * Parse the first YAML document in a String and produce the corresponding
     * Java object. (Because the encoding in known BOM is not respected.)
     * 
     * @param yaml
     *            - YAML data to load from (BOM must not be present)
     * @return parsed object
     */
    @SuppressWarnings("unchecked")
    public static <T> T load(String yaml, Class<T> clazz) {
        Loader loader = createLoader(clazz);
        return (T) loader.load(new StringReader(yaml));
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * Java object.
     * 
     * @param io
     *            - data to load from (BOM is respected and removed)
     * @return parsed object
     */
    @SuppressWarnings("unchecked")
    public static <T> T load(InputStream io, Class<T> clazz) {
        Loader loader = createLoader(clazz);
        return (T) loader.load(new UnicodeReader(io));
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * Java object.
     * 
     * @param io
     *            - data to load from (BOM must not be present)
     * @return parsed object
     */
    @SuppressWarnings("unchecked")
    public static <T> T load(java.io.Reader io, Class<T> clazz) {
        Loader loader = createLoader(clazz);
        return (T) loader.load(io);
    }

    @Override
    public String toString() {
        return name;
    }

    private static Loader createLoader(Class<? extends Object> clazz) {
        Loader loader = new Loader(new Constructor(clazz));
        Resolver resolver = new Resolver();
        loader.setResolver(resolver);
        return loader;
    }
}
