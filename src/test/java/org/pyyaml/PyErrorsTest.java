package org.pyyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.error.YAMLException;

/**
 * @see imported from PyYAML
 */
public class PyErrorsTest extends PyImportTest {
    // TODO these exceptions must be fixed
    private boolean skip(String filename) {
        List<String> failures = new ArrayList<String>();
        failures.add("invalid-uri-escapes-2.loader-error");
        failures.add("invalid-uri-escapes-3.loader-error");
        // TODO what to do with unknoun tag ?
        failures.add("undefined-constructor.loader-error");
        // in python list cannot be a key in a dictionary.
        failures.add("unacceptable-key.loader-error");
        // TODO these are against the specification but I like it :)
        failures.add("invalid-omap-1.loader-error");
        failures.add("invalid-pairs-1.loader-error");
        for (String name : failures) {
            if (name.equals(filename)) {
                return true;
            }
        }
        return false;
    }

    public void testLoaderErrors() throws FileNotFoundException {
        File[] files = getStreamsByExtension(".loader-error");
        assertTrue("No test files found.", files.length > 0);
        for (int i = 0; i < files.length; i++) {
            if (skip(files[i].getName())) {
                continue;
            }
            try {
                for (Object document : loadAll(new FileInputStream(files[i]))) {
                    assertNotNull(document);
                }
                fail("Loading must fail for " + files[i].getAbsolutePath());
                // System.err.println("Loading must fail for " +
                // files[i].getAbsolutePath());
            } catch (YAMLException e) {
                assertTrue(true);
            }
        }
    }

    public void testLoaderStringErrors() throws FileNotFoundException {
        File[] files = getStreamsByExtension(".loader-error");
        assertTrue("No test files found.", files.length > 0);
        for (int i = 0; i < files.length; i++) {
            if (skip(files[i].getName())) {
                continue;
            }
            try {
                String content = getResource(files[i].getName());
                for (Object document : loadAll(content.trim())) {
                    assertNotNull(document);
                }
                fail("Loading must fail for " + files[i].getAbsolutePath());
                // System.err.println("Loading must fail for " +
                // files[i].getAbsolutePath());
            } catch (YAMLException e) {
                assertTrue(true);
            }
        }
    }

    public void testLoaderSingleErrors() throws FileNotFoundException {
        File[] files = getStreamsByExtension(".single-loader-error");
        assertTrue("No test files found.", files.length > 0);
        for (int i = 0; i < files.length; i++) {
            try {
                String content = getResource(files[i].getName());
                load(content.trim());
                fail("Loading must fail for " + files[i].getAbsolutePath());
                // multiple documents must not be accepted
                System.err.println("Loading must fail for " + files[i].getAbsolutePath());
            } catch (YAMLException e) {
                assertTrue(true);
            }
        }
    }

    // TODO not imported: public void testEmitterErrors()
    // testDumperErrors() is implemented in SerializerTest.java

    @SuppressWarnings("unchecked")
    public void testLoaderErrors1() throws FileNotFoundException {
        // TODO this test for debugging only
        File[] files = getStreamsByExtension("invalid-uri-escapes-2.loader-error");
        try {
            for (Object document : loadAll(new FileInputStream(files[0]))) {
                assertNotNull(document);
                System.out.println(document);
            }
            // System.out.println(data);
        } catch (YAMLException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
            assertTrue(true);
        }
    }

}
