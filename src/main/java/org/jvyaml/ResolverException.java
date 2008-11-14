/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import org.yaml.snakeyaml.error.YAMLException;

/**
 * @see PyYAML 3.06 for more information
 */
public class ResolverException extends YAMLException {
    public ResolverException(final String msg) {
        super(msg);
    }

    public ResolverException(final Throwable thr) {
        super(thr);
    }
}
