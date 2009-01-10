/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML< /a> for more information
 */
public class Constructor extends SafeConstructor {

    public Constructor() {
        this(Object.class);
    }

    public Constructor(Class<? extends Object> theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root class must be provided.");
        }
        this.yamlConstructors.put(null, new ConstuctYamlObject());
        rootClass = theRoot;
    }

    private class ConstuctYamlObject implements Construct {
        @SuppressWarnings("unchecked")
        public <T> T construct(Class<T> clazz, Node node) {
            Object result = null;
            String pref = node.getTag().substring("tag:yaml.org,2002:".length());
            try {
                if (node instanceof MappingNode) {
                    MappingNode mnode = (MappingNode) node;
                    Class cl = Class.forName(pref);
                    result = constructMappingNode(cl, mnode);
                } else if (node instanceof SequenceNode) {
                    SequenceNode snode = (SequenceNode) node;
                    Class cl = Class.forName(pref);
                    List<Object> values = (List<Object>) constructSequence(snode);
                    Class[] parameterTypes = new Class[values.size()];
                    int index = 0;
                    for (Object parameter : values) {
                        parameterTypes[index] = parameter.getClass();
                        index++;
                    }
                    java.lang.reflect.Constructor javaConstructor = cl
                            .getConstructor(parameterTypes);
                    Object[] initargs = values.toArray();
                    result = javaConstructor.newInstance(initargs);
                } else {
                    ScalarNode snode = (ScalarNode) node;
                    Class cl = Class.forName(pref);
                    Object value = constructScalar(snode);
                    java.lang.reflect.Constructor javaConstructor = cl.getConstructor(value
                            .getClass());
                    // TODO which way to leave ?
                    result = javaConstructor.newInstance(value);
                }
            } catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a java object for "
                        + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark());
            }
            return (T) result;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T callConstructor(Class<T> clazz, Node node) {
        if (Object.class.equals(clazz)) {
            return super.callConstructor(clazz, node);
        }
        Object result;
        if (node instanceof ScalarNode) {
            result = constructScalarNode(clazz, (ScalarNode) node);
        } else if (node instanceof SequenceNode) {
            result = constructSequenceNode((SequenceNode) node);
        } else {
            result = constructMappingNode(clazz, (MappingNode) node);
        }
        return (T) result;
    }

    @SuppressWarnings("unchecked")
    private <T> T constructScalarNode(Class<T> clazz, ScalarNode node) {
        Class<? extends Object> c = clazz;
        Object result;
        if (c.isPrimitive() || c == String.class || Number.class.isAssignableFrom(c)
                || c == Boolean.class || c == Date.class || c == Character.class
                || c == BigInteger.class) {
            if (c == String.class) {
                Construct stringContructor = yamlConstructors.get("tag:yaml.org,2002:str");
                result = stringContructor.construct(Object.class, (ScalarNode) node);
            } else if (c == Boolean.class || c == Boolean.TYPE) {
                Construct boolContructor = yamlConstructors.get("tag:yaml.org,2002:bool");
                result = boolContructor.construct(Object.class, (ScalarNode) node);
            } else if (c == Character.class || c == Character.TYPE) {
                Construct charContructor = yamlConstructors.get("tag:yaml.org,2002:str");
                String ch = (String) charContructor.construct(Object.class, (ScalarNode) node);
                if (ch.length() != 1) {
                    throw new YAMLException("Invalid node Character: '" + ch + "'; length: "
                            + ch.length());
                }
                result = new Character(ch.charAt(0));
            } else if (c == Date.class) {
                Construct dateContructor = yamlConstructors.get("tag:yaml.org,2002:timestamp");
                result = dateContructor.construct(Object.class, (ScalarNode) node);
            } else if (c == Float.class || c == Double.class || c == Float.TYPE || c == Double.TYPE) {
                Construct doubleContructor = yamlConstructors.get("tag:yaml.org,2002:float");
                result = doubleContructor.construct(Object.class, node);
                if (c == Float.class || c == Float.TYPE) {
                    result = new Float((Double) result);
                }
            } else if (Number.class.isAssignableFrom(c) || c == Byte.TYPE || c == Short.TYPE
                    || c == Integer.TYPE || c == Long.TYPE) {
                Construct intContructor = yamlConstructors.get("tag:yaml.org,2002:int");
                result = intContructor.construct(Object.class, node);
                if (c == Byte.class || c == Byte.TYPE) {
                    result = new Byte(result.toString());
                } else if (c == Short.class || c == Short.TYPE) {
                    result = new Short(result.toString());
                } else if (c == Integer.class || c == Integer.TYPE) {
                    result = new Integer(result.toString());
                } else if (c == Long.class || c == Long.TYPE) {
                    result = new Long(result.toString());
                } else if (c == BigInteger.class) {
                    result = new BigInteger(result.toString());
                } else {
                    throw new YAMLException("Unsupported Number class: " + c);
                }
            } else {
                throw new YAMLException("Unsupported class: " + c);
            }
        } else {
            try {
                // get value by BaseConstructor
                Object value = super.callConstructor(Object.class, node);
                java.lang.reflect.Constructor<? extends Object> javaConstructor = c
                        .getConstructor(value.getClass());
                result = javaConstructor.newInstance(value);
            } catch (Exception e) {
                throw new YAMLException(e);
            }
        }
        return (T) result;
    }

    private Object constructSequenceNode(SequenceNode node) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T constructMappingNode(Class<T> clazz, MappingNode node) {
        Class<? extends Object> beanClass = clazz;
        Object object;
        try {
            object = beanClass.newInstance();
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
            // keys can only be Strings
            Object key = constructObject(String.class, keyNode);
            try {
                Property property = getProperty(beanClass, (String) key);
                if (property == null)
                    throw new YAMLException("Unable to find property '" + key + "' on class: "
                            + beanClass.getName());
                Object value = constructObject(property.getType(), valueNode);
                // TODO Class propertyElementType =
                // config.propertyToElementType.get(property);
                property.set(object, value);
            } catch (Exception e) {
                throw new YAMLException(e);
            }
        }
        return (T) object;
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
