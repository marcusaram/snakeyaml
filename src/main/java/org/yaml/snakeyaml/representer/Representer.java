/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import java.io.IOException;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Representer {
    void represent(final Object data) throws IOException;
}
