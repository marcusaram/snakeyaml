package org.pyyaml;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Util;

public abstract class PyImportTest extends TestCase {
    public static final String PATH = "pyyaml";

    protected String getResource(String theName) {
        try {
            String content;
            content = Util.getLocalResource(PATH + File.separator + theName);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
