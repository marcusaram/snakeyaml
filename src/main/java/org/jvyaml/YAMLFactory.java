/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.InputStream;
import java.io.Writer;

import org.yaml.snakeyaml.YamlConfig;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.serializer.Serializer;

/**
 * @see PyYAML 3.06 for more information
 */
public interface YAMLFactory {
    Scanner createScanner(final String io);

    Scanner createScanner(final InputStream io);

    Parser createParser(final Scanner scanner);

    Resolver createResolver();

    Composer createComposer(final Parser parser, final Resolver resolver);

    Constructor createConstructor(final Composer composer);

    Emitter createEmitter(final Writer output, final YamlConfig cfg);

    Serializer createSerializer(final Emitter emitter, final Resolver resolver, final YamlConfig cfg);

    Representer createRepresenter(final Serializer serializer, final YamlConfig cfg);
}
