/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.nodes;

import java.util.List;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class MappingNode extends CollectionNode {
    public static final String id = "mapping";
    private Class<? extends Object> keyType;
    private Class<? extends Object> valueType;

    public MappingNode(String tag, List<Node[]> value, Mark startMark, Mark endMark,
            Boolean flowStyle) {
        super(tag, value, startMark, endMark, flowStyle);
        keyType = Object.class;
        valueType = Object.class;
    }

    public MappingNode(String tag, List<Node[]> value, Boolean flowStyle) {
        super(tag, value, null, null, flowStyle);
    }

    @Override
    public String getNodeId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public List<Node[]> getValue() {
        List<Node[]> mapping = (List<Node[]>) super.getValue();
        for (Node[] nodes : mapping) {
            nodes[0].setType(keyType);
            nodes[1].setType(valueType);
        }
        return mapping;
    }

    public void setValue(List<Node[]> merge) {
        value = merge;
    }

    public Class<? extends Object> getKeyType() {
        return keyType;
    }

    public void setKeyType(Class<? extends Object> keyType) {
        this.keyType = keyType;
    }

    public Class<? extends Object> getValueType() {
        return valueType;
    }

    public void setValueType(Class<? extends Object> valueType) {
        this.valueType = valueType;
    }
}
