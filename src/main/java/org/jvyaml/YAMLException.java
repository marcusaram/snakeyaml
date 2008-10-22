/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class YAMLException extends RuntimeException {
    public YAMLException(final String message) {
        super(message);
    }

    public YAMLException(final Throwable cause) {
        super(cause);
    }
}// YAMLException
