/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.Iterator;

import org.jvyaml.tokens.Token;

/**
 * @see PyYAML for more information
 */
public interface Scanner {
    boolean checkToken(final Class[] choices);
    Token peekToken();
    Token getToken();
    Iterator eachToken();
    Iterator iterator();
}
