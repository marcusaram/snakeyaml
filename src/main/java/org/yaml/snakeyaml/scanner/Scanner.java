/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.tokens.Token;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Scanner {
    /**
     * Check if the next token is one of the given types.
     */
    boolean checkToken(final Class<Token>[] choices);

    /**
     * Return the next token, but do not delete if from the queue.
     */
    Token peekToken();

    /**
     * Return the next token.
     */
    Token getToken();

}
