/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class SerializerException extends YAMLException {
    public SerializerException(final String msg) {
        super(msg);
    }

    public SerializerException(final Throwable thr) {
        super(thr);
    }
}// SerializerException
