/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @see http://yaml.org/type/binary.html
 */
public class BinaryTagTest extends AbstractTest {
    String line1 = "R0lGODlhDAAMAIQAAP//9/X17unp5WZmZgAAAOfn515eXvPz7Y6OjuDg4J+fn5";
    String line2 = "OTk6enp56enmlpaWNjY6Ojo4SEhP/++f/++f/++f/++f/++f/++f/++f/++f/+";
    String line3 = "+f/++f/++f/++f/++f/++SH+Dk1hZGUgd2l0aCBHSU1QACwAAAAADAAMAAAFLC";
    String line4 = "AgjoEwnuNAFOhpEMTRiggcz4BNJHrv/zCFcLiwMWYNG84BwwEeECcgggoBADs=";
    String content = line1 + line2 + line3 + line4;

    public void testBinary() throws IOException {

        ByteBuffer binary = (ByteBuffer) getMapValue("canonical: !!binary " + content, "canonical");
        Charset charset = Charset.forName("ISO-8859-1");
        CharBuffer buffer = charset.decode(binary);
        String result = buffer.toString();
        assertTrue(result.startsWith("GIF89"));
    }

    public void testBinaryTag() throws IOException {
        ByteBuffer binary = (ByteBuffer) getMapValue("canonical: !<tag:yaml.org,2002:binary> "
                + content, "canonical");
        Charset charset = Charset.forName("ISO-8859-1");
        CharBuffer buffer = charset.decode(binary);
        String result = buffer.toString();
        assertTrue(result.startsWith("GIF89"));
    }

    public void testBinaryOut() throws IOException {
        byte[] data = "GIF89\tbinary\nimage\n".getBytes("ISO-8859-1");
        ByteBuffer buffer = ByteBuffer.wrap(data);
        //
        Map<String, ByteBuffer> map = new HashMap<String, ByteBuffer>();
        map.put("canonical", buffer);
        String output = dump(map);
        assertTrue(output, output.contains("canonical: !!binary R0lGOD"));
    }

}
