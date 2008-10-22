/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.tokens;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class TagToken extends Token {
    private String[] value;
    public TagToken(final String[] value) {
        this.value = value;
    }
    
    public String[] getValue() {
        return this.value;
    }
}// TagToken
