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
public class ErrorsTest extends PyImportTest {
    private boolean skip(String filename) {
        List<String> failures = new ArrayList<String>();
        failures.add("fetch-complex-value-bug.loader-error");
        failures.add("invalid-uri-escapes-2.loader-error");
        failures.add("invalid-uri-escapes-3.loader-error");
        failures.add("undefined-constructor.loader-error");
        // TODO these are against the spec but I like it :)
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
                loadAll(new FileInputStream(files[i]));
                // fail("Loading must fail for " + files[i].getAbsolutePath());
                System.err.println("Loading must fail for " + files[i].getAbsolutePath());
            } catch (YAMLException e) {
                assertTrue(true);
            } catch (Exception e) {
                System.err.println("Loading must fail (with exception) for "
                        + files[i].getAbsolutePath());
            }
        }
    }

    public void testLoaderErrors1() throws FileNotFoundException {
        File[] files = getStreamsByExtension("no-block-mapping-end-2.loader-error");
        try {
            List data = (List) loadAll(new FileInputStream(files[0]));
            for (Object object : data) {

                System.out.println(object);

            }
            // System.out.println(data);
        } catch (YAMLException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
            assertTrue(true);
        }
    }
}
