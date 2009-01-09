/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

public class TestBean1 {
    private String text;
    private String id;
    private Integer integer;
    private int intPrimitive;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public int getIntPrimitive() {
        return intPrimitive;
    }

    public void setIntPrimitive(int intPrimitive) {
        this.intPrimitive = intPrimitive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}