package org.yaml.snakeyaml.emitter;

import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;

public class EventConstructor extends Constructor {

    public EventConstructor() {
        this.yamlConstructors.put(null, new ConstructEvent());
    }

    private class ConstructEvent implements Construct {

        public Object construct(Node node) {
            String className = node.getTag().substring(1) + "Event";
            System.out.println("Class: " + className);
            return null;
        }
    }
}
