/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.Iterator;

import org.jvyaml.nodes.Node;

/**
 * @see PyYAML for more information
 */
public interface Constructor {
    boolean checkData();

    Object getData();

    Iterator eachDocument();

    Iterator iterator();

    Object constructDocument(final Node node);

    Object constructObject(final Node node);

    Object constructPrimitive(final Node node);

    Object constructScalar(final Node node);

    Object constructPrivateType(final Node node);

    Object constructSequence(final Node node);

    Object constructMapping(final Node node);

    Object constructPairs(final Node node);

    interface YamlConstructor {
        Object call(final Constructor self, final Node node);
    }

    interface YamlMultiConstructor {
        Object call(final Constructor self, final String pref, final Node node);
    }
}
