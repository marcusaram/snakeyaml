package org.yaml.snakeyaml;

import org.yaml.snakeyaml.representer.BaseRepresenter;

public abstract class AbstractRepresenter implements Represent {
    protected BaseRepresenter parent;

    public void setParent(BaseRepresenter parent) {
        this.parent = parent;
    }
}
