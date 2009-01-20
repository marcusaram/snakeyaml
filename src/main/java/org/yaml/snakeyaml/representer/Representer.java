/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class Representer extends SafeRepresenter {
    private Map<Class<? extends Object>, String> classTags;
    private Map<Class<? extends Object>, TypeDescription> classDefinitions;

    public Representer(Character default_style, Boolean default_flow_style) {
        super(default_style, default_flow_style);
        classTags = new HashMap<Class<? extends Object>, String>();
        classDefinitions = new HashMap<Class<? extends Object>, TypeDescription>();
        this.representers.put(null, new RepresentJavaBean());
    }

    public Representer() {
        this(null, null);
    }

    /**
     * Make YAML aware how to represent a custom Class. If there is no root
     * Class assigned in constructor then the 'root' property of this definition
     * is respected.
     * 
     * @param definition
     *            to be added to the Constructor
     * @return the previous value associated with <tt>definition</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>definition</tt>.
     */
    public TypeDescription addClassDefinition(TypeDescription definition) {
        if (definition == null) {
            throw new NullPointerException("ClassDescription is required.");
        }
        String tag = definition.getTag();
        classTags.put(definition.getType(), tag);
        return classDefinitions.put(definition.getType(), definition);
    }

    private class RepresentJavaBean implements Represent {
        public Node representData(Object data) {
            // sort names
            Map<String, Object> values = new TreeMap<String, Object>();
            Method[] ems = data.getClass().getMethods();
            for (int i = 0, j = ems.length; i < j; i++) {
                if (ems[i].getParameterTypes().length == 0) {
                    String name = ems[i].getName();
                    if (name.equals("getClass")) {
                        continue;
                    }
                    String pname = null;
                    if (name.startsWith("get")) {
                        pname = "" + Character.toLowerCase(name.charAt(3)) + name.substring(4);
                    } else if (name.startsWith("is")) {
                        pname = "" + Character.toLowerCase(name.charAt(2)) + name.substring(3);
                    }
                    if (pname != null) {
                        try {
                            values.put(pname, ems[i].invoke(data, (Object[]) null));
                        } catch (Exception exe) {
                            values.put(pname, null);
                        }
                    }
                }
            }
            String tag;
            String customTag = classTags.get(data.getClass());
            if (customTag == null) {
                tag = "tag:yaml.org,2002:" + data.getClass().getName();
            } else {
                tag = customTag;
            }
            // flow style will be chosen by BaseRepresenter
            Node node = representMapping(tag, values, null);
            return node;
        }
    }
}
