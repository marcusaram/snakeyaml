/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.util.Map;

import junit.framework.TestCase;

import org.jvyaml.YAML;

public abstract class AbstractTest extends TestCase {
    @SuppressWarnings("unchecked")
    protected Map<String, Object> getMap(String data) {
        Map<String, Object> nativeData = (Map<String, Object>) YAML.load(data);
        return nativeData;
    }

    protected String dump(Object data) {
        return YAML.dump(data);
    }
}
