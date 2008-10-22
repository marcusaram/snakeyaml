/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.jvyaml.YAML;

public class YamlDocument {
    public static final String ROOT = "specification/";
    private String source;
    private String presentation;
    private Object nativeData;

    public YamlDocument(String sourceName) {
        try {
            InputStream input = YamlDocument.class.getClassLoader().getResourceAsStream(
                    ROOT + sourceName);
            nativeData = YAML.load(new InputStreamReader(input));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            YAML.dump(nativeData, new OutputStreamWriter(output));
            presentation = output.toString("UTF-8");
            source = getLocalResource(sourceName);
            // try to read generated presentation to prove that the presentation
            // is identical to the source
            Object result = YAML.load(presentation);
            if (!nativeData.equals(result)) {
                throw new RuntimeException("Generated presentation is not valid: " + presentation);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLocalResource(String theName) throws IOException {
        InputStream input;
        input = YamlDocument.class.getClassLoader().getResourceAsStream(ROOT + theName);
        if (input == null) {
            throw new RuntimeException("Can not find " + theName);
        }
        BufferedInputStream is = new BufferedInputStream(input);
        StringBuffer buf = new StringBuffer(3000);
        int i;
        try {
            while ((i = is.read()) != -1) {
                buf.append((char) i);
            }
        } finally {
            is.close();
        }
        String resource = buf.toString();
        // convert EOLs
        String[] lines = resource.split("\\r?\\n");
        StringBuffer buffer = new StringBuffer();
        for (int j = 0; j < lines.length; j++) {
            buffer.append(lines[j]);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public String getSource() {
        return source;
    }

    public String getPresentation() {
        return presentation;
    }

    public Object getNativeData() {
        return nativeData;
    }
}
