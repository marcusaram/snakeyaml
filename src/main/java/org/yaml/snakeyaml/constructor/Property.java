/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

abstract class Property {
    private final Class<? extends Object> type;

    public Property(Class<? extends Object> type) {
        this.type = type;
    }

    public Class<? extends Object> getType() {
        return type;
    }

    abstract public void set(Object object, Object value) throws Exception;

    abstract public Object get(Object object) throws Exception;
}