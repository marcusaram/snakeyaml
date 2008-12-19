/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.nodes;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public abstract class Node {
    private String tag;
    protected Object value;
    private Mark startMark;
    protected Mark endMark;

    public Node(String tag, Object value, Mark startMark, Mark endMark) {
        setTag(tag);
        if (value == null) {
            throw new NullPointerException("value in a Node is required.");
        }
        this.value = value;
        this.startMark = startMark;
        this.endMark = endMark;
    }

    public String getTag() {
        return this.tag;
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return "<" + this.getClass().getName() + " (tag=" + getTag() + ", value=" + getValue()
                + ")>";
    }

    /**
     * For error reporting.
     * 
     * @see class variable 'id' in PyYAML
     * @return scalar, sequence, mapping
     */
    public abstract String getNodeId();

    public Mark getStartMark() {
        return startMark;
    }

    public void setTag(String tag) {
        if (tag == null) {
            throw new NullPointerException("tag in a Node is required.");
        }
        this.tag = tag;
    }
}
