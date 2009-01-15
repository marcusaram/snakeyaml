package org.yaml.snakeyaml;

public class ClassDescription {
    private Class<? extends Object> clazz;
    private String tag;
    private boolean root;

    public ClassDescription(Class<? extends Object> clazz, String tag) {
        this.clazz = clazz;
        this.tag = tag;
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
}
