/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.Reader;
import java.io.Writer;

/**
 * @see PyYAML for more information
 */
public interface YAMLFactory {
    Scanner createScanner(final String io);
    Scanner createScanner(final Reader io);
    Parser createParser(final Scanner scanner);
    Parser createParser(final Scanner scanner, final YAMLConfig cfg);
    Resolver createResolver();
    Composer createComposer(final Parser parser, final Resolver resolver);
    Constructor createConstructor(final Composer composer);
    Emitter createEmitter(final Writer output, final YAMLConfig cfg);
    Serializer createSerializer(final Emitter emitter, final Resolver resolver, final YAMLConfig cfg);
    Representer createRepresenter(final Serializer serializer, final YAMLConfig cfg);
}// YAMLFactory
