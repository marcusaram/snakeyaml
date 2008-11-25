/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.InputStream;

import org.yaml.snakeyaml.YamlConfig;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorImpl;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.representer.RepresenterImpl;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.serializer.Serializer;

/**
 * @see PyYAML 3.06 for more information
 */
public class YAMLFactory {
    public Scanner createScanner(final String io) {
        return new ScannerImpl(new org.yaml.snakeyaml.reader.Reader(io));
    }

    public Scanner createScanner(final InputStream io) {
        return new ScannerImpl(new org.yaml.snakeyaml.reader.Reader(io));
    }

    public Parser createParser(final Scanner scanner) {
        return new ParserImpl(scanner);
    }

    public Constructor createConstructor(final Composer composer) {
        return new ConstructorImpl(composer);
    }

    public Representer createRepresenter(final Serializer serializer, final YamlConfig cfg) {
        return new RepresenterImpl(serializer, cfg);
    }
}
