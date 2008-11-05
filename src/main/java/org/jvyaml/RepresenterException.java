/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import org.yaml.snakeyaml.error.YAMLException;

/**
 * @see PyYAML 3.06 for more information
 */
public class RepresenterException extends YAMLException {
    public RepresenterException(final String msg) {
        super(msg);
    }

    public RepresenterException(final Throwable thr) {
        super(thr);
    }
}
