/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
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
}// PrivateType
