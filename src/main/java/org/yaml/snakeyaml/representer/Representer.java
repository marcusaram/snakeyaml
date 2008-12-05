/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Represent;
import org.yaml.snakeyaml.nodes.Node;

/**
 * @see PyYAML 3.06 for more information
 */
public class Representer extends SafeRepresenter {
    public Representer(Character default_style, Boolean default_flow_style) {
        super(default_style, default_flow_style);
        this.representers.put(null, new RepresentJavaBean());
    }

    public Representer() {
        super(null, null);
    }

    private class RepresentJavaBean implements Represent {
        @SuppressWarnings("unchecked")
        public Node representData(Object data) {
            Map values = new HashMap();
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
                    if (null != pname) {
                        try {
                            values.put(pname, ems[i].invoke(data, (Object[]) null));
                        } catch (Exception exe) {
                            values.put(pname, null);
                        }
                    }
                }
            }
            String tag = data.getClass().getName();
            return representMapping(tag, values, null);
        }
    }

}
