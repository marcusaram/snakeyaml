/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @see PyYAML for more information
 */
public class EmitterException extends YAMLException {
    public EmitterException(final String msg) {
        super(msg);
    }

    public EmitterException(final Throwable thr) {
        super(thr);
    }
}
