/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.beans.PropertyDescriptor;

class MethodProperty extends Property {
    private final PropertyDescriptor property;

    public MethodProperty(PropertyDescriptor property) {
        super(property.getName(), property.getPropertyType());
        this.property = property;
    }

    public void set(Object object, Object value) throws Exception {
        property.getWriteMethod().invoke(object, value);
    }

    public Object get(Object object) throws Exception {
        return property.getReadMethod().invoke(object);
    }
}