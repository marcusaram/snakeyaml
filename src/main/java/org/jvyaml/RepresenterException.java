/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class RepresenterException extends YAMLException {
    public RepresenterException(final String msg) {
        super(msg);
    }

    public RepresenterException(final Throwable thr) {
        super(thr);
    }
}// RepresenterException
