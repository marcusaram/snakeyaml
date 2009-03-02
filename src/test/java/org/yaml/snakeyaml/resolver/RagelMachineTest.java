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
}
