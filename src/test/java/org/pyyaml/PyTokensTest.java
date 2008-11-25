package org.pyyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEndToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.DocumentEndToken;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.ValueToken;

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
    public void testTokens() throws FileNotFoundException {
        Map<Class, String> replaces = new HashMap<Class, String>();
        replaces.put(DirectiveToken.class, "%");
        replaces.put(DocumentStartToken.class, "---");
        replaces.put(DocumentEndToken.class, "...");
        replaces.put(AliasToken.class, "*");
        replaces.put(AnchorToken.class, "&");
        replaces.put(TagToken.class, "!");
        replaces.put(ScalarToken.class, "_");
        replaces.put(BlockSequenceStartToken.class, "[[");
        replaces.put(BlockMappingStartToken.class, "{{");
        replaces.put(BlockEndToken.class, "]}");
        replaces.put(FlowSequenceStartToken.class, "[");
        replaces.put(FlowSequenceEndToken.class, "]");
        replaces.put(FlowMappingStartToken.class, "{");
        replaces.put(FlowMappingEndToken.class, "}");
        replaces.put(BlockEntryToken.class, ",");
        replaces.put(FlowEntryToken.class, ",");
        replaces.put(KeyToken.class, "?");
        replaces.put(ValueToken.class, ":");
        //
        File[] tokensFiles = getStreamsByExtension(".tokens");
        assertTrue("No test files found.", tokensFiles.length > 0);
        for (int i = 0; i < tokensFiles.length; i++) {
            String name = tokensFiles[i].getName();
            int position = name.lastIndexOf('.');
            String dataName = name.substring(0, position) + ".data";
            if (skip(tokensFiles[i].getName())) {
                continue;
            }
            //
            String tokenFileData = getResource(name);
            String[] split = tokenFileData.split("\\s+");
            List<String> tokens2 = new LinkedList<String>();
            for (int j = 0; j < split.length; j++) {
                tokens2.add(split[j]);
            }
            //
            List<String> tokens1 = new LinkedList<String>();
            Reader reader = new Reader(new FileInputStream(getFileByName(dataName)));
            Scanner scanner = new ScannerImpl(reader);
            try {
                while (scanner.checkToken(new ArrayList<Class>())) {
                    Token token = scanner.getToken();
                    if (!(token instanceof StreamStartToken || token instanceof StreamEndToken)) {
                        String replacement = replaces.get(token.getClass());
                        tokens1.add(replacement);
                    }
                }
                // System.out.println("File name: \n" +
                // tokensFiles[i].getName());
                // Iterator iter = tokens2.iterator();
                // for (String string : tokens1) {
                // String str2 = (String) iter.next();
                // System.out.println(string + "=" + str2);
                // }
                assertEquals(tokenFileData, tokens1.size(), tokens2.size());
                assertEquals(tokens1, tokens2);
            } catch (RuntimeException e) {
                System.out.println("File name: \n" + tokensFiles[i].getName());
                String data = getResource(tokensFiles[i].getName());
                System.out.println("Data: \n" + data);
                System.out.println("Tokens:");
                for (String token : tokens1) {
                    System.out.println(token);
                }
                fail("Cannot scan: " + tokensFiles[i]);
            }
        }
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

    public void testSlopy() throws IOException {
        List<String> tokens = new LinkedList<String>();
        Reader reader = new Reader(getResource("sloppy-indentation-!!!!!!!!!.data"));
        Scanner scanner = new ScannerImpl(reader);
        try {
            while (scanner.checkToken(new ArrayList<Class>())) {
                Token token = scanner.getToken();
                tokens.add(token.getClass().getName());
            }
            System.out.println("Tokens:");
            for (String token : tokens) {
                System.out.println(token);
            }
        } catch (RuntimeException e) {
            String data = getResource("sloppy-indentation.data");
            System.out.println("Data: \n" + data);
            System.out.println("Tokens:");
            for (String token : tokens) {
                System.out.println(token);
            }
            fail("Cannot scan: " + e.getMessage());
        }
    }

    public void testSlopy2() throws IOException {
        List<String> tokens = new LinkedList<String>();
        Reader reader = new Reader(getResource("sloppy-indentation-!!!!!!!!!.data"));
        Scanner scanner = new ScannerImpl(reader);
        while (scanner.checkToken(new ArrayList<Class>())) {
            Token token = scanner.getToken();
            tokens.add(token.getClass().getName());
        }
    }
}
