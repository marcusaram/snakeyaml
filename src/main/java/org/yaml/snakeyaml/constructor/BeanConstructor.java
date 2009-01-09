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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * Create an instance when the root Java class is provided
 * 
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class BeanConstructor extends Constructor {
    protected Map<Class<? extends Object>, Construct> classConstructors = new HashMap<Class<? extends Object>, Construct>();

    private Class<? extends Object> rootClass;
    private Class<? extends Object> nextClass;

    public BeanConstructor(Class<? extends Object> theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root class must be provided.");
        }
        rootClass = theRoot;

    }

    @Override
    protected Object constructDocument(Node node) {
        nextClass = rootClass;
        return super.constructDocument(node);
    }

    @Override
    protected Object callConstructor(Node node) {
        Object result;
        if (node instanceof ScalarNode) {
            result = constructScalarNode((ScalarNode) node);
        } else if (node instanceof SequenceNode) {
            result = constructSequenceNode((SequenceNode) node);
        } else {
            result = constructMappingNode((MappingNode) node);
        }
        return result;
    }

    private Object constructScalarNode(ScalarNode node) {
        Class<? extends Object> c = nextClass;
        nextClass = Object.class;
        Object result;
        if (c.isPrimitive() || c == String.class || Number.class.isAssignableFrom(c)
                || c == Boolean.class || c == Date.class || c == Character.class
                || c == BigInteger.class) {
            if (c == String.class) {
                Construct stringContructor = yamlConstructors.get("tag:yaml.org,2002:str");
                result = stringContructor.construct((ScalarNode) node);
            } else if (c == Boolean.class || c == Boolean.TYPE) {
                Construct boolContructor = yamlConstructors.get("tag:yaml.org,2002:bool");
                result = boolContructor.construct((ScalarNode) node);
            } else if (c == Character.class || c == Character.TYPE) {
                Construct charContructor = yamlConstructors.get("tag:yaml.org,2002:str");
                String ch = (String) charContructor.construct((ScalarNode) node);
                if (ch.length() != 1) {
                    throw new YAMLException("Invalid node Character: '" + ch + "'; length: "
                            + ch.length());
                }
                result = new Character(ch.charAt(0));
            } else if (c == Date.class) {
                Construct dateContructor = yamlConstructors.get("tag:yaml.org,2002:timestamp");
                result = dateContructor.construct((ScalarNode) node);
            } else if (c == Float.class || c == Double.class || c == Float.TYPE || c == Double.TYPE) {
                Construct doubleContructor = yamlConstructors.get("tag:yaml.org,2002:float");
                result = doubleContructor.construct(node);
                if (c == Float.class || c == Float.TYPE) {
                    result = new Float((Double) result);
                }
            } else if (Number.class.isAssignableFrom(c) || c == Byte.TYPE || c == Short.TYPE
                    || c == Integer.TYPE || c == Long.TYPE) {
                Construct intContructor = yamlConstructors.get("tag:yaml.org,2002:int");
                result = intContructor.construct(node);
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
            // TODO call constructor with scalar content as the only argument
            result = constructScalar(node);
        }
        return result;
    }

    private Object constructSequenceNode(SequenceNode node) {
        // TODO Auto-generated method stub
        return null;
    }

    private Object constructMappingNode(MappingNode node) {
        Class<? extends Object> beanClass = nextClass;
        nextClass = Object.class;
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
                    System.out.println("Class: " + nextClass);
                    System.out.println("Object: " + object);
                    System.out.println("Value: " + value);
                    System.out.println("property: " + property);
                    System.out.println("value's class: " + value.getClass());
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
