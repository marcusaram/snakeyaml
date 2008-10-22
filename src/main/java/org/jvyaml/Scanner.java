/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.Iterator;

import org.jvyaml.tokens.Token;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public interface Scanner {
    boolean checkToken(final Class[] choices);
    Token peekToken();
    Token getToken();
    Iterator eachToken();
    Iterator iterator();
}// RbYAMLScanner
