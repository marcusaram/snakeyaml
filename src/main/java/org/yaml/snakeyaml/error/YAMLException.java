/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.error;

/**
 * @see PyYAML 3.06 for more information
 */
public class YAMLException extends RuntimeException {
    public YAMLException(final String message) {
        super(message);
    }

    public YAMLException(final Throwable cause) {
        super(cause);
    }
}
