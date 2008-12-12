package org.yaml.snakeyaml;

import java.io.Writer;
import java.util.Iterator;

import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

public class Dumper {
    private Representer representer;
    private DumperOptions options;

    public Dumper(Representer representer, DumperOptions options) {
        this.representer = representer;
        this.options = options;
    }

    public Dumper(DumperOptions options) {
        this(new Representer(options.getDefaultStyle(), options.isDefaultFlowStyle()), options);
    }

    public void dump(Iterable<Object> data, Writer output, Resolver resolver) {
        Serializer s = new Serializer(new Emitter(output, options), resolver, options);
        try {
            s.open();
            for (Iterator<Object> iter = data.iterator(); iter.hasNext();) {
                representer.represent(s, iter.next());
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
}
