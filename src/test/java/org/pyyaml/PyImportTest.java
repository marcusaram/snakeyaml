package org.pyyaml;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.jvyaml.Yaml;
import org.yaml.snakeyaml.Util;

public abstract class PyImportTest extends TestCase {
    public static final String PATH = "pyyaml";

    protected Object load(String data) {
        Yaml yaml = new Yaml();
        Object obj = yaml.load(data);
        return obj;
    }

    protected Iterable<Object> loadAll(InputStream data) {
        Yaml yaml = new Yaml();
        return yaml.loadAll(data);
    }

    protected Iterable<Object> loadAll(String data) {
        Yaml yaml = new Yaml();
        return yaml.loadAll(data);
    }

    protected String getResource(String theName) {
        try {
            String content;
            content = Util.getLocalResource(PATH + File.separator + theName);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File[] getStreamsByExtension(String extention) {
        File file = new File("src/test/resources/pyyaml");
        assertTrue("Folder not found: " + file.getAbsolutePath(), file.exists());
        assertTrue(file.isDirectory());
        File[] files = file.listFiles(new PyFilenameFilter(extention));
        return files;
    }

    private class PyFilenameFilter implements FilenameFilter {
        private String extension;

        public PyFilenameFilter(String extension) {
            this.extension = extension;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(extension);
        }
    }
}
