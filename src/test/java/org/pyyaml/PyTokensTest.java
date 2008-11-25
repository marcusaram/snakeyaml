package org.pyyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.Token;

/**
 * @see imported from PyYAML
 */
public class PyTokensTest extends PyImportTest {
    // TODO these exceptions must be fixed
    private boolean skip(String filename) {
        List<String> failures = new ArrayList<String>();
        failures.add("sloppy-indentation.data");
        failures.add("spec-05-02-utf16be.data");
        failures.add("spec-05-02-utf16le.data");
        failures.add("spec-05-10.data");
        failures.add("spec-05-12.data");
        failures.add("spec-05-14.data");
        failures.add("spec-05-15.data");
        failures.add("spec-07-01.data");
        failures.add("spec-08-06.data");
        failures.add("spec-08-13.data");
        failures.add("spec-09-02.data");
        failures.add("spec-09-14.data");
        for (String name : failures) {
            if (name.equals(filename)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void testScannerData() throws FileNotFoundException {
        File[] files = getStreamsByExtension(".data");
        assertTrue("No test files found.", files.length > 0);
        for (int i = 0; i < files.length; i++) {
            if (skip(files[i].getName())) {
                continue;
            }
            List<String> tokens = new LinkedList<String>();
            Reader reader = new Reader(new FileInputStream(files[i]));
            Scanner scanner = new ScannerImpl(reader);
            try {
                while (scanner.checkToken(new ArrayList<Class>())) {
                    Token token = scanner.getToken();
                    tokens.add(token.getClass().getName());
                }
            } catch (RuntimeException e) {
                System.out.println("File name: \n" + files[i].getName());
                String data = getResource(files[i].getName());
                System.out.println("Data: \n" + data);
                System.out.println("Tokens:");
                for (String token : tokens) {
                    System.out.println(token);
                }
                fail("Cannot scan: " + files[i]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void testScannerCanonical() throws FileNotFoundException {
        File[] files = getStreamsByExtension(".canonical");
        assertTrue("No test files found.", files.length > 0);
        for (int i = 0; i < files.length; i++) {
            List<String> tokens = new LinkedList<String>();
            Reader reader = new Reader(new FileInputStream(files[i]));
            Scanner scanner = new ScannerImpl(reader);
            try {
                while (scanner.checkToken(new ArrayList<Class>())) {
                    Token token = scanner.getToken();
                    tokens.add(token.getClass().getName());
                }
            } catch (RuntimeException e) {
                System.out.println("File name: \n" + files[i].getName());
                String data = getResource(files[i].getName());
                System.out.println("Data: \n" + data);
                System.out.println("Tokens:");
                for (String token : tokens) {
                    System.out.println(token);
                }
                fail("Cannot scan: " + files[i]);
            }
        }
    }
}
