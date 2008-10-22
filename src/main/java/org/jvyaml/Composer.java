/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.Iterator;

import org.jvyaml.nodes.Node;

/**
 * @see PyYAML for more information
 */
public interface Composer {
    boolean checkNode();
    Node getNode();
    Iterator eachNode();
    Iterator iterator();
}// Composer
