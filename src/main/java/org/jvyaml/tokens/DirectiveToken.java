/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.tokens;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class DirectiveToken extends Token {
    private String name;
    private String[] value;

    public DirectiveToken(final String name, final String[] value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String[] getValue() {
        return this.value;
    }
}// DirectiveToken
