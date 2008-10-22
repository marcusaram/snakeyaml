/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.Reader;
import java.io.Writer;

/**
 * @see PyYAML for more information
 */
public class DefaultYAMLFactory implements YAMLFactory {
    public Scanner createScanner(final String io) {
        return new ScannerImpl(io);
    }
    public Scanner createScanner(final Reader io) {
        return new ScannerImpl(io);
    }
    public Parser createParser(final Scanner scanner) {
        return new ParserImpl(scanner);
    }
    public Parser createParser(final Scanner scanner, final YAMLConfig cfg) {
        return new ParserImpl(scanner,cfg);
    }
    public Resolver createResolver() {
        return new ResolverImpl();
    }
    public Composer createComposer(final Parser parser, final Resolver resolver) {
        return new ComposerImpl(parser,resolver);
    }
    public Constructor createConstructor(final Composer composer) {
        return new ConstructorImpl(composer);
    }
    public Emitter createEmitter(final Writer output, final YAMLConfig cfg) {
        return new EmitterImpl(output,cfg);
    }
    public Serializer createSerializer(final Emitter emitter, final Resolver resolver, final YAMLConfig cfg) {
        return new SerializerImpl(emitter,resolver,cfg);
    }
    public Representer createRepresenter(final Serializer serializer, final YAMLConfig cfg) {
        return new RepresenterImpl(serializer,cfg);
    }
}
