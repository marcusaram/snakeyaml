/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.ByteArrayOutputStream;
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
            source = Util.getLocalResource(ROOT + sourceName);
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

    public String getSource() {
        return source;
    }

    public String getPresentation() {
        return presentation;
    }

    public Object getNativeData() {
        if (nativeData == null) {
            throw new NullPointerException("No object is parsed.");
        }
        return nativeData;
    }
}
