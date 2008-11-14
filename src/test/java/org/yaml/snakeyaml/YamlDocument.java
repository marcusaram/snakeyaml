/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class YamlDocument {
    public static final String ROOT = "specification/";
    private String source;
    private String presentation;
    private Object nativeData;

    public YamlDocument(String sourceName) {
        InputStream input = YamlDocument.class.getClassLoader().getResourceAsStream(
                ROOT + sourceName);
        Yaml yaml = new Yaml();
        nativeData = yaml.load(input);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        yaml.dump(nativeData, new OutputStreamWriter(output));
        try {
            presentation = output.toString("UTF-8");
            source = Util.getLocalResource(ROOT + sourceName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // try to read generated presentation to prove that the presentation
        // is identical to the source
        Object result = yaml.load(presentation);
        if (!nativeData.equals(result)) {
            throw new RuntimeException("Generated presentation is not valid: " + presentation);
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
