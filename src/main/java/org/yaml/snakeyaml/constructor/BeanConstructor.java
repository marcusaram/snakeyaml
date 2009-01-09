/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

/**
 * Create an instance when the root Java class is provided
 * 
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class BeanConstructor extends Constructor {
    protected Map<Class<? extends Object>, Construct> classConstructors = new HashMap<Class<? extends Object>, Construct>();

    protected Class<? extends Object> nextClass;

    public BeanConstructor(Class<? extends Object> theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root class must be provided.");
        }
        nextClass = theRoot;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object callConstructor(Node node) {
        Class beanClass = nextClass;
        nextClass = Object.class;
        Object object;
        try {
            object = nextClass.newInstance();
        } catch (InstantiationException e) {
            throw new YAMLException(e);
        } catch (IllegalAccessException e) {
            throw new YAMLException(e);
        }
        List<Node[]> nodeValue = (List<Node[]>) node.getValue();
        for (Iterator<Node[]> iter = nodeValue.iterator(); iter.hasNext();) {
            Node[] tuple = iter.next();
            ScalarNode keyNode;
            if (tuple[0] instanceof ScalarNode) {
                keyNode = (ScalarNode) tuple[0];// key must be scalar
            } else {
                throw new YAMLException("Keys must be scalars but found: " + tuple[0]);
            }
            Node valueNode = tuple[1];
            nextClass = String.class;// keys can only be Strings
            Object key = constructObject(keyNode);
            if (key != null && key instanceof String) {
                try {
                    Property property = getProperty(beanClass, (String) key);
                    if (property == null)
                        throw new YAMLException("Unable to find property '" + key + "' on class: "
                                + beanClass.getName());
                    nextClass = property.getType();
                    Object value = constructObject(valueNode);
                    // TODO Class propertyElementType =
                    // config.propertyToElementType.get(property);
                    property.set(object, value);
                } catch (Exception e) {
                    throw new YAMLException(e);
                }
            } else {
                throw new YAMLException("Invalid key name: " + key);
            }
        }
        return object;
    }

    protected Property getProperty(Class<? extends Object> type, String name)
            throws IntrospectionException {
        if (type == null)
            throw new IllegalArgumentException("type cannot be null.");
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("name cannot be null or empty.");
        for (PropertyDescriptor property : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
            if (property.getName().equals(name)) {
                if (property.getReadMethod() != null && property.getWriteMethod() != null)
                    return new MethodProperty(property);
                break;
            }
        }
        for (Field field : type.getFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)
                    || Modifier.isTransient(modifiers))
                continue;
            if (field.getName().equals(name))
                return new FieldProperty(field);
        }
        return null;
    }
}
