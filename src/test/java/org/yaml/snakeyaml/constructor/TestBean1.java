/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

public class TestBean1 {
    private String text;
    private String id;
    private Byte byteClass;
    private byte bytePrimitive;
    private Short shortClass;
    private short shortPrimitive;
    private Integer integer;
    private int intPrimitive;
    private Long longClass;
    private long longPrimitive;

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

    public Byte getByteClass() {
        return byteClass;
    }

    public void setByteClass(Byte byteClass) {
        this.byteClass = byteClass;
    }

    public byte getBytePrimitive() {
        return bytePrimitive;
    }

    public void setBytePrimitive(byte bytePrimitive) {
        this.bytePrimitive = bytePrimitive;
    }

    public Short getShortClass() {
        return shortClass;
    }

    public void setShortClass(Short shortClass) {
        this.shortClass = shortClass;
    }

    public short getShortPrimitive() {
        return shortPrimitive;
    }

    public void setShortPrimitive(short shortPrimitive) {
        this.shortPrimitive = shortPrimitive;
    }

    public Long getLongClass() {
        return longClass;
    }

    public void setLongClass(Long longClass) {
        this.longClass = longClass;
    }

    public long getLongPrimitive() {
        return longPrimitive;
    }

    public void setLongPrimitive(long longPrimitive) {
        this.longPrimitive = longPrimitive;
    }
}