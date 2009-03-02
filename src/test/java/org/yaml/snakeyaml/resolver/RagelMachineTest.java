package org.yaml.snakeyaml.resolver;

import junit.framework.TestCase;

public class RagelMachineTest extends TestCase {
    private RagelMachine machine = new RagelMachine();

    public void testScan() {
        assertNull(machine.scan("abc"));
    }

    public void testNullPointerException() {
        try {
            machine.scan(null);
            fail("null must not be accepted.");
        } catch (NullPointerException e) {
            assertEquals("Scalar must be provided", e.getMessage());
        }
    }

    public void testScanBoolean() {
        assertEquals("tag:yaml.org,2002:bool", machine.scan("true"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("True"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("TRUE"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("false"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("False"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("FALSE"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("on"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("ON"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("On"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("off"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("Off"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("OFF"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("on"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("ON"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("On"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("off"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("Off"));
        assertEquals("tag:yaml.org,2002:bool", machine.scan("OFF"));
    }

    public void testScanNull() {
        assertEquals("tag:yaml.org,2002:null", machine.scan("null"));
        assertEquals("tag:yaml.org,2002:null", machine.scan("Null"));
        assertEquals("tag:yaml.org,2002:null", machine.scan("NULL"));
        assertEquals("tag:yaml.org,2002:null", machine.scan("~"));
        assertEquals("tag:yaml.org,2002:null", machine.scan(" "));
    }

    public void testScanMerge() {
        assertEquals("tag:yaml.org,2002:merge", machine.scan("<<"));
    }

    public void testScanValue() {
        assertEquals("tag:yaml.org,2002:value", machine.scan("="));
    }

    public void testScanInt() {
        assertEquals("tag:yaml.org,2002:int", machine.scan("0"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("1"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("-0"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("-9"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("0b0011"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("0x12ef"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("0123"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("1_000"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("1_000_000"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("+0"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("+10"));
        assertEquals("tag:yaml.org,2002:int", machine.scan("1__000"));
    }
}
