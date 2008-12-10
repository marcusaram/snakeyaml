/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.error;

/**
 * @see PyYAML for more information
 */
public class YAMLException extends RuntimeException {
    private static final long serialVersionUID = -4738336175050337570L;

    public YAMLException(final String message) {
        super(message);
    }

    public YAMLException(final Throwable cause) {
        super(cause);
    }
}
