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

    protected Object load(String data) {
        Object obj = YAML.load(data);
        return obj;
    }

    protected String dump(Object data) {
        return YAML.dump(data);
    }

    @SuppressWarnings("unchecked")
    protected Object getMapValue(String data, String key) {
        Map nativeData = getMap(data);
        return nativeData.get(key);
    }
}
