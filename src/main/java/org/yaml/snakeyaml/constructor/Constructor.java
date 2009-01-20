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

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML< /a> for more information
 */
public class Constructor extends SafeConstructor {
    private Map<String, Class<? extends Object>> typeTags;
    private Map<Class<? extends Object>, TypeDescription> typeDefinitions;

    public Constructor() {
        this(Object.class);
    }

    public Constructor(Class<? extends Object> theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        this.yamlConstructors.put(null, new ConstuctYamlObject());
        rootType = theRoot;
        typeTags = new HashMap<String, Class<? extends Object>>();
        typeDefinitions = new HashMap<Class<? extends Object>, TypeDescription>();
    }

    /**
     * Make YAML aware how to parse a custom Class. If there is no root Class
     * assigned in constructor then the 'root' property of this definition is
     * respected.
     * 
     * @param definition
     *            to be added to the Constructor
     * @return the previous value associated with <tt>definition</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>definition</tt>.
     */
    public TypeDescription addTypeDefinition(TypeDescription definition) {
        if (definition == null) {
            throw new NullPointerException("TypeDescription is required.");
        }
        if (rootType == Object.class && definition.isRoot()) {
            rootType = definition.getType();
        }
        String tag = definition.getTag();
        typeTags.put(tag, definition.getType());
        return typeDefinitions.put(definition.getType(), definition);
    }

    private class ConstuctYamlObject implements Construct {
        @SuppressWarnings("unchecked")
        public Object construct(Node node) {
            Object result = null;
            Class<? extends Object> customTag = typeTags.get(node.getTag());
            try {
                Class cl;
                if (customTag == null) {
                    if (node.getTag().length() < "tag:yaml.org,2002:".length()) {
                        throw new YAMLException("Unknown tag: " + node.getTag());
                    }
                    String name = node.getTag().substring("tag:yaml.org,2002:".length());
                    cl = Class.forName(name);
                } else {
                    cl = customTag;
                }
                if (node instanceof MappingNode) {
                    MappingNode mnode = (MappingNode) node;
                    mnode.setType(cl);
                    result = constructMappingNode(mnode);
                } else if (node instanceof SequenceNode) {
                    SequenceNode snode = (SequenceNode) node;
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
                    Object value = constructScalar(snode);
                    java.lang.reflect.Constructor javaConstructor = cl.getConstructor(value
                            .getClass());
                    result = javaConstructor.newInstance(value);
                }
            } catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a java object for "
                        + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark());
            }
            return result;
        }
    }

    @Override
    protected Object callConstructor(Node node) {
        if (Object.class.equals(node.getType())) {
            return super.callConstructor(node);
        }
        Object result;
        if (node instanceof ScalarNode) {
            result = constructScalarNode((ScalarNode) node);
        } else if (node instanceof SequenceNode) {
            result = constructSequence((SequenceNode) node);
        } else {
            if (Map.class.isAssignableFrom(node.getType())) {
                result = super.constructMapping((MappingNode) node);
            } else {
                result = constructMappingNode((MappingNode) node);
            }
        }
        return result;
    }

    private Object constructScalarNode(ScalarNode node) {
        Class<? extends Object> type = node.getType();
        Object result;
        if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type)
                || type == Boolean.class || type == Date.class || type == Character.class
                || type == BigInteger.class) {
            if (type == String.class) {
                Construct stringContructor = yamlConstructors.get("tag:yaml.org,2002:str");
                result = stringContructor.construct((ScalarNode) node);
            } else if (type == Boolean.class || type == Boolean.TYPE) {
                Construct boolContructor = yamlConstructors.get("tag:yaml.org,2002:bool");
                result = boolContructor.construct((ScalarNode) node);
            } else if (type == Character.class || type == Character.TYPE) {
                Construct charContructor = yamlConstructors.get("tag:yaml.org,2002:str");
                String ch = (String) charContructor.construct((ScalarNode) node);
                if (ch.length() != 1) {
                    throw new YAMLException("Invalid node Character: '" + ch + "'; length: "
                            + ch.length());
                }
                result = new Character(ch.charAt(0));
            } else if (type == Date.class) {
                Construct dateContructor = yamlConstructors.get("tag:yaml.org,2002:timestamp");
                result = dateContructor.construct((ScalarNode) node);
            } else if (type == Float.class || type == Double.class || type == Float.TYPE
                    || type == Double.TYPE) {
                Construct doubleContructor = yamlConstructors.get("tag:yaml.org,2002:float");
                result = doubleContructor.construct(node);
                if (type == Float.class || type == Float.TYPE) {
                    result = new Float((Double) result);
                }
            } else if (Number.class.isAssignableFrom(type) || type == Byte.TYPE
                    || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE) {
                Construct intContructor = yamlConstructors.get("tag:yaml.org,2002:int");
                result = intContructor.construct(node);
                if (type == Byte.class || type == Byte.TYPE) {
                    result = new Byte(result.toString());
                } else if (type == Short.class || type == Short.TYPE) {
                    result = new Short(result.toString());
                } else if (type == Integer.class || type == Integer.TYPE) {
                    result = new Integer(result.toString());
                } else if (type == Long.class || type == Long.TYPE) {
                    result = new Long(result.toString());
                } else if (type == BigInteger.class) {
                    result = new BigInteger(result.toString());
                } else {
                    throw new YAMLException("Unsupported Number class: " + type);
                }
            } else {
                throw new YAMLException("Unsupported class: " + type);
            }
        } else {
            try {
                // get value by BaseConstructor
                Object value = super.callConstructor(node);
                java.lang.reflect.Constructor<? extends Object> javaConstructor = type
                        .getConstructor(value.getClass());
                result = javaConstructor.newInstance(value);
            } catch (Exception e) {
                throw new YAMLException(e);
            }
        }
        return result;
    }

    /**
     * Construct JavaBean. If type safe collections are used please look at
     * <code>ClassDescription</code>.
     * 
     * @param node
     *            - node where the keys are property names (they can only be
     *            <code>String</code>s) and values are objects to be created
     * @return constructed JavaBean
     */
    private Object constructMappingNode(MappingNode node) {
        Class<? extends Object> beanType = node.getType();
        Object object;
        try {
            object = beanType.newInstance();
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
            keyNode.setType(String.class);
            String key = (String) constructObject(keyNode);
            try {
                Property property = getProperty(beanType, key);
                if (property == null)
                    throw new YAMLException("Unable to find property '" + key + "' on class: "
                            + beanType.getName());
                valueNode.setType(property.getType());
                TypeDescription memberDescription = typeDefinitions.get(beanType);
                if (memberDescription != null) {
                    if (valueNode instanceof SequenceNode) {
                        SequenceNode snode = (SequenceNode) valueNode;
                        Class<? extends Object> memberType = memberDescription
                                .getListPropertyType(key);
                        if (memberType != null) {
                            snode.setListType(memberType);
                        }
                    } else if (valueNode instanceof MappingNode) {
                        MappingNode mnode = (MappingNode) valueNode;
                        Class<? extends Object> keyType = memberDescription.getMapKeyType(key);
                        if (keyType != null) {
                            mnode.setKeyType(keyType);
                            mnode.setValueType(memberDescription.getMapValueType(key));
                        }
                    }
                }
                Object value = constructObject(valueNode);
                property.set(object, value);
            } catch (Exception e) {
                throw new YAMLException(e);
            }
        }
        return object;
    }

    protected Property getProperty(Class<? extends Object> type, String name)
            throws IntrospectionException {
        for (PropertyDescriptor property : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
            if (property.getName().equals(name)) {
                if (property.getReadMethod() != null && property.getWriteMethod() != null)
                    return new MethodProperty(name, property);
                break;
            }
        }
        for (Field field : type.getFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)
                    || Modifier.isTransient(modifiers))
                continue;
            if (field.getName().equals(name))
                return new FieldProperty(name, field);
        }
        return null;
    }
}
