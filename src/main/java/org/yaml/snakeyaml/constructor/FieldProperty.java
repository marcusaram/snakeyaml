/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.lang.reflect.Field;

class FieldProperty extends Property {
    private final Field field;

    public FieldProperty(String name, Field field) {
        super(name, field.getType());
        this.field = field;
    }

    public void set(Object object, Object value) throws Exception {
        field.set(object, value);
    }
}