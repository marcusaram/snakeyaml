/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @see PyYAML for more information
 */
public class ResolverException extends YAMLException {
    public ResolverException(final String msg) {
        super(msg);
    }

    public ResolverException(final Throwable thr) {
        super(thr);
    }
}
