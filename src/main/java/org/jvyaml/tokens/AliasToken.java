/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.tokens;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class AliasToken extends Token {
    private String value;

    public AliasToken() {
        super();
    }
    public AliasToken(final String value) {
        this.value = value;
    }

    public void setValue(final Object value) {
        this.value = (String)value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "#<" + this.getClass().getName() + " value=\"" + value + "\">";
    }
}// AliasToken
