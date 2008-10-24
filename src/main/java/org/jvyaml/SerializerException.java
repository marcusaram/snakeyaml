/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @see PyYAML 3.06 for more information
 */
public class SerializerException extends YAMLException {
    public SerializerException(final String msg) {
        super(msg);
    }

    public SerializerException(final Throwable thr) {
        super(thr);
    }
}
