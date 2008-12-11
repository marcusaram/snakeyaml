/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.emitter;

import org.yaml.snakeyaml.error.YAMLException;

/**
 * @see PyYAML for more information
 */
public class EmitterException extends YAMLException {
    private static final long serialVersionUID = -8280070025452995908L;

    public EmitterException(final String msg) {
        super(msg);
    }

}
