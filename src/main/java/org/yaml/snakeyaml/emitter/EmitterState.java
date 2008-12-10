/**
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.emitter;

import java.io.IOException;

/**
 * Python's methods are first class object. Java needs a class.
 * 
 * @see PyYAML for more information
 */
interface EmitterState {
    void expect() throws IOException;
}