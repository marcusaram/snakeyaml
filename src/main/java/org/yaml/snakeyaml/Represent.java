package org.yaml.snakeyaml;

import org.yaml.snakeyaml.nodes.Node;

public interface Represent {
    public Node representData(Object data);
}
