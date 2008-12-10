/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * @see PyYAML for more information
 */
public class Constructor extends SafeConstructor {

    public Constructor() {
        this.yamlConstructors.put(null, new ConstuctYamlObject());
    }

    private class ConstuctYamlObject implements Construct {
        @SuppressWarnings("unchecked")
        public Object construct(Node node) {
            Object result = null;
            String pref = node.getTag().substring(1);
            try {
                if (node instanceof MappingNode) {
                    MappingNode mnode = (MappingNode) node;
                    Class cl = Class.forName(pref);
                    result = cl.newInstance();
                    Map values = (Map) constructMapping(mnode);
                    java.lang.reflect.Method[] ems = cl.getMethods();
                    for (Iterator iter = values.keySet().iterator(); iter.hasNext();) {
                        Object key = iter.next();
                        Object value = values.get(key);
                        String keyName = key.toString();
                        String mName = "set" + Character.toUpperCase(keyName.charAt(0))
                                + keyName.substring(1);
                        for (int i = 0, j = ems.length; i < j; i++) {
                            if (ems[i].getName().equals(mName)
                                    && ems[i].getParameterTypes().length == 1) {
                                ems[i].invoke(result, new Object[] { fixValue(value, ems[i]
                                        .getParameterTypes()[0]) });
                                break;
                            }
                        }
                    }
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
                } else if (node instanceof ScalarNode) {
                    ScalarNode snode = (ScalarNode) node;
                    Class cl = Class.forName(pref);
                    Object value = constructScalar(snode);
                    java.lang.reflect.Constructor javaConstructor = cl.getConstructor(value
                            .getClass());
                    result = javaConstructor.newInstance(value);
                }
            } catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a java object for "
                        + node.getTag(), node.getStartMark());
            }
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    private Object fixValue(Object inp, Class outp) {
        if (inp == null) {
            return null;
        }
        Class inClass = inp.getClass();
        if (outp.isAssignableFrom(inClass)) {
            return inp;
        }
        if (inClass == Long.class && (outp == Integer.class || outp == Integer.TYPE)) {
            return new Integer(((Long) inp).intValue());
        }
        if (inClass == Long.class && (outp == Short.class || outp == Short.TYPE)) {
            return new Short((short) ((Long) inp).intValue());
        }
        if (inClass == Long.class && (outp == Character.class || outp == Character.TYPE)) {
            return new Character((char) ((Long) inp).intValue());
        }
        if (inClass == Double.class && (outp == Float.class || outp == Float.TYPE)) {
            return new Float(((Double) inp).floatValue());
        }
        return inp;
    }
}
