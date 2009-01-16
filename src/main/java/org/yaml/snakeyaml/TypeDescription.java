package org.yaml.snakeyaml;

import java.util.HashMap;
import java.util.Map;

public class TypeDescription {
    private Class<? extends Object> clazz;
    private String tag;
    private boolean root;
    private Map<String, Class<? extends Object>> listProperties;
    private Map<String, Class<? extends Object>> keyProperties;
    private Map<String, Class<? extends Object>> valueProperties;

    public TypeDescription(Class<? extends Object> clazz, String tag) {
        this.clazz = clazz;
        this.tag = tag;
        listProperties = new HashMap<String, Class<? extends Object>>();
        keyProperties = new HashMap<String, Class<? extends Object>>();
        valueProperties = new HashMap<String, Class<? extends Object>>();
    }

    public TypeDescription(Class<? extends Object> clazz) {
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

    public void putMapPropertyType(String property, Class<? extends Object> key,
            Class<? extends Object> value) {
        keyProperties.put(property, key);
        valueProperties.put(property, value);
    }

    public Class<? extends Object> getMapKeyType(String property) {
        return keyProperties.get(property);
    }

    public Class<? extends Object> getMapValueType(String property) {
        return valueProperties.get(property);
    }
}
