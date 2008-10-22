/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class EmitterException extends YAMLException {
    public EmitterException(final String msg) {
        super(msg);
    }

    public EmitterException(final Throwable thr) {
        super(thr);
    }
}// EmitterException
