/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

/**
 * @see PyYAML 3.06 for more information
 */
public class PrivateType {
    private String tag;
    private Object value;

    public PrivateType(final String tag, final Object value) {
        this.tag = tag;
        this.value = value;
    }

    public String toString() {
        return "#<PrivateType tag: " + tag + " value: " + value + ">";
    }
}
