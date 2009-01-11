/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.lang.reflect.Field;

class FieldProperty extends Property {
    private final Field field;

    public FieldProperty(Field field) {
        super(field.getType());
        this.field = field;
    }

    public void set(Object object, Object value) throws Exception {
        field.set(object, value);
    }

    public Object get(Object object) throws Exception {
        return field.get(object);
    }
}