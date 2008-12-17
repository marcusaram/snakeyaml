/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import org.yaml.snakeyaml.error.YAMLException;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class RepresenterException extends YAMLException {
    private static final long serialVersionUID = -7594852637893412485L;

    public RepresenterException(final String msg) {
        super(msg);
    }
}
