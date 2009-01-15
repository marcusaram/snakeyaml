package org.yaml.snakeyaml;

import java.util.HashMap;
import java.util.Map;

public class ClassDescription {
    private Class<? extends Object> clazz;
    private String tag;
    private boolean root;
    private Map<String, Class<? extends Object>> listProperties;

    public ClassDescription(Class<? extends Object> clazz, String tag) {
        this.clazz = clazz;
        this.tag = tag;
        listProperties = new HashMap<String, Class<? extends Object>>();
    }

    public ClassDescription(Class<? extends Object> clazz) {
        this(clazz, null);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Class<? extends Object> getClazz() {
        return clazz;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public void putListPropertyType(String property, Class<? extends Object> type) {
        listProperties.put(property, type);
    }

    public Class<? extends Object> getListPropertyType(String property) {
        return listProperties.get(property);
    }
}
