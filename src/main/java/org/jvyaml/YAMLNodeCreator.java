/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.IOException;

import org.jvyaml.nodes.Node;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public interface YAMLNodeCreator {
    String taguri();
    Node toYamlNode(Representer representer) throws IOException;
}// YAMLNodeCreator
