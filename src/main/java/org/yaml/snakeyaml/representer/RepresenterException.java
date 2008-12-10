/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import org.yaml.snakeyaml.error.YAMLException;

/**
 * @see PyYAML for more information
 */
public class RepresenterException extends YAMLException {
    private static final long serialVersionUID = -7594852637893412485L;

    public RepresenterException(final String msg) {
        super(msg);
    }
}
