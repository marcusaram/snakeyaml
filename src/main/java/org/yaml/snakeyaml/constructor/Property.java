/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

abstract class Property {
    private final String name;
    private final Class<? extends Object> type;

    public Property(String name, Class<? extends Object> type) {
        this.name = name;
        this.type = type;
    }

    public Class<? extends Object> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName() + " in class " + getType();
    }

    abstract public void set(Object object, Object value) throws Exception;
}