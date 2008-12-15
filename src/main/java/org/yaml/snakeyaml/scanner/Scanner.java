/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.scanner;

import java.util.List;

import org.yaml.snakeyaml.tokens.Token;

/**
 * Produce <code>Token<code>s.
 * 
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public interface Scanner {
    /**
     * Check if the next token is one of the given types.
     */
    @SuppressWarnings("unchecked")
    boolean checkToken(final List<Class> choices);

    /**
     * Convenience method to avoid List creation
     */
    @SuppressWarnings("unchecked")
    boolean checkToken(Class choice);

    /**
     * Return the next token, but do not delete if from the queue.
     */
    Token peekToken();

    /**
     * Return the next token.
     */
    Token getToken();

}
