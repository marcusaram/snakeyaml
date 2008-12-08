/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.nodes.Node;

/**
 * @see PyYAML 3.06 for more information
 */
public class Constructor extends SafeConstructor {

    public Constructor() {
        this.yamlConstructors.put("tag:yaml.org,2002:java/object", new ConstuctYamlObject());
    }

    private class ConstuctYamlObject implements Construct {
        public Object construct(Node node) {
            // Object outp = null;
            // try {
            // final Class cl = Class.forName(pref);
            // outp = cl.newInstance();
            // final Map values = (Map) ctor.constructMapping(node);
            // java.lang.reflect.Method[] ems = cl.getMethods();
            // for (final Iterator iter = values.keySet().iterator();
            // iter.hasNext();) {
            // final Object key = iter.next();
            // final Object value = values.get(key);
            // final String keyName = key.toString();
            // final String mName = "set" +
            // Character.toUpperCase(keyName.charAt(0))
            // + keyName.substring(1);
            // for (int i = 0, j = ems.length; i < j; i++) {
            // if (ems[i].getName().equals(mName)
            // && ems[i].getParameterTypes().length == 1) {
            // ems[i].invoke(outp, new Object[] { fixValue(value, ems[i]
            // .getParameterTypes()[0]) });
            // break;
            // }
            // }
            // }
            // } catch (final Exception e) {
            // throw new
            // YAMLException("Can't construct a java object from class " + pref
            // + ": "
            // + e.toString());
            // }
            // return outp;
            throw new UnsupportedOperationException("GGGGGGGGGGGGGGG RRRRRRRRRRRRR");
        }
    }
}
