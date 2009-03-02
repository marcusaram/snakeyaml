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

}
