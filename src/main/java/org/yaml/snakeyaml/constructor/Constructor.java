/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.nodes.Node;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Constructor {
    boolean checkData();

    Object getData();

    Object getSingleData();

    Object constructPrimitive(final Node node);

    Object constructScalar(final Node node);

    Object constructPrivateType(final Node node);

    Object constructSequence(final Node node);

    Object constructMapping(final Node node);

    Object constructPairs(final Node node);
}
