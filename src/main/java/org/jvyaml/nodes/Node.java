/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

import org.jvyaml.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public abstract class Node {
    private String tag;
    private Object value;
    private int hash = -1;
    private Mark startMark;
    private Mark endMark;

    public Node(final String tag, final Object value, final Mark startMark, final Mark endMark) {
        this.tag = tag;
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

    public int hashCode() {
        if (hash == -1) {
            hash = 3;
            hash += (null == tag) ? 0 : 3 * tag.hashCode();
            hash += (null == value) ? 0 : 3 * value.hashCode();
        }
        return hash;
    }

    public boolean equals(final Object oth) {
        if (oth instanceof Node) {
            final Node nod = (Node) oth;
            return ((this.tag != null) ? this.tag.equals(nod.tag) : nod.tag == null)
                    && ((this.value != null) ? this.value.equals(nod.value) : nod.value == null);
        }
        return false;
    }

    public String toString() {
        return "#<" + this.getClass().getName() + " (tag=" + getTag() + ", value=" + getValue()
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

    public Mark getEndMark() {
        return endMark;
    }
}
