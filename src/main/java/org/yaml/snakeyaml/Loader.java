package org.yaml.snakeyaml;

import java.io.InputStream;
import java.util.Iterator;

import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.scanner.ScannerImpl;

public class Loader {
    protected Constructor constructor;

    public Loader(Constructor constructor) {
        super();
        this.constructor = constructor;
    }

    public Loader() {
        this(new Constructor());
    }

    public Object load(InputStream io) {
        Composer composer = new Composer(new ParserImpl(new ScannerImpl(new Reader(io))),
                new Resolver());
        constructor.setComposer(composer);
        return constructor.getSingleData();
    }

    public Object load(String yaml) {
        Composer composer = new Composer(new ParserImpl(new ScannerImpl(new Reader(yaml))),
                new Resolver());
        constructor.setComposer(composer);
        return constructor.getSingleData();
    }

    public Iterable<Object> loadAll(final InputStream yaml) {
        Composer composer = new Composer(new ParserImpl(new ScannerImpl(new Reader(yaml))),
                new Resolver());
        this.constructor.setComposer(composer);
        Iterator<Object> result = new Iterator<Object>() {
            public boolean hasNext() {
                return constructor.checkData();
            }

            public Object next() {
                return constructor.getData();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new YamlIterable(result);
    }

    public Iterable<Object> loadAll(final String yaml) {
        Composer composer = new Composer(new ParserImpl(new ScannerImpl(new Reader(yaml))),
                new Resolver());
        constructor.setComposer(composer);
        Iterator<Object> result = new Iterator<Object>() {
            public boolean hasNext() {
                return constructor.checkData();
            }

            public Object next() {
                return constructor.getData();
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
