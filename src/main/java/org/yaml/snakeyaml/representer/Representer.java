/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.yaml.snakeyaml.nodes.Node;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class Representer extends SafeRepresenter {
    private Map<Class<? extends Object>, String> classTags;

    public Representer(Character default_style, Boolean default_flow_style) {
        super(default_style, default_flow_style);
        classTags = new HashMap<Class<? extends Object>, String>();
        this.representers.put(null, new RepresentJavaBean());
    }

    public Representer() {
        this(null, null);
    }

    public boolean addClassTag(Class<? extends Object> clazz, String tag) {
        return classTags.put(clazz, tag) == null;
    }

    private class RepresentJavaBean implements Represent {
        @SuppressWarnings("unchecked")
        public Node representData(Object data) {
            Map values = new TreeMap();// sort names
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
            return representMapping(tag, values, null);
        }
    }

}
