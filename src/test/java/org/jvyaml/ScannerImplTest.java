package org.jvyaml;

import java.util.Iterator;

import junit.framework.TestCase;

import org.jvyaml.tokens.Token;

public abstract class ScannerImplTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCheckToken() {
        fail("Not yet implemented");
    }

    public void testPeekToken() {
        fail("Not yet implemented");
    }

    public void testGetToken() {
        fail("Not yet implemented");
    }

    @SuppressWarnings("unchecked")
    public void testEachToken() {
        String test1 = "--- \n\"\\xfc\"\n";
        Scanner sce2 = new ScannerImpl(test1);
        for (Iterator<Token> iter = sce2.eachToken(); iter.hasNext();) {
            Token token = iter.next();
            System.out.println(token);
        }
    }

    public void testIterator() {
        fail("Not yet implemented");
    }

}
