package org.jvyaml;

import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;

import junit.framework.TestCase;

import org.yaml.snakeyaml.tokens.Token;

public class ScannerImplTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testEachToken() {
        String test1 = "--- \n\"\\xfc\"\n";
        Scanner sce2 = new ScannerImpl(new org.yaml.snakeyaml.reader.Reader(test1));
        for (Iterator<Token> iter = sce2.eachToken(); iter.hasNext();) {
            Token token = iter.next();
            System.out.println(token);
        }
    }

    public void testMain() throws Exception {
        main(new String[0]);
    }

    /*
     * public static void oldmain(final String[] args) { final String test1 =
     * "--- \nA: b\nc: 3.14\n"; final String filename = args[0];
     * 
     * final ScannerImpl sce = new ScannerImpl(test1);
     * System.out.println("Reading of string: \"" + test1 + "\"");
     * while(!sce.eof) { int toShow = 20; if(sce.buffer.remaining()<20) {
     * toShow = sce.buffer.remaining(); } System.out.println("--prefix" + toShow + ":
     * \"" + sce.prefix(toShow) + "\""); sce.forward(toShow); }
     * 
     * System.out.println("Reading of file: \"" + filename + "\""); final
     * ScannerImpl sce2 = new ScannerImpl(new FileReader(filename));
     * while(!sce2.eof) { int toShow = 20; if(sce2.buffer.remaining()<20) {
     * toShow = sce2.buffer.remaining(); } System.out.println("--prefix" +
     * toShow + ": \"" + sce2.prefix(toShow) + "\""); sce2.forward(toShow); } }
     */

    public static void main(final String[] args) throws Exception {
        // final String test1 = "--- \nA: b\nc: 3.14\n";
        String filename;
        if (args.length == 1) {
            filename = args[0];
        } else {
            filename = "src/test/resources/specification/example2_28.yaml";
        }
        /*
         * final Scanner sce = new ScannerImpl(test1);
         * System.out.println("Reading of string: \"" + test1 + "\""); for(final
         * Iterator iter = sce.eachToken();iter.hasNext();) {
         * System.out.println(iter.next()); }
         */
        System.out.println("Reading of file: \"" + filename + "\"");

        final StringBuffer input = new StringBuffer();
        final Reader reader = new FileReader(filename);
        char[] buff = new char[1024];
        int read = 0;
        while (true) {
            read = reader.read(buff);
            input.append(buff, 0, read);
            if (read < 1024) {
                break;
            }
        }
        reader.close();
        final String str = input.toString();
        final long before = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            final Scanner sce2 = new ScannerImpl(new org.yaml.snakeyaml.reader.Reader(str));
            for (final Iterator iter = sce2.eachToken(); iter.hasNext();) {
                System.out.println(iter.next());
            }
        }
        final long after = System.currentTimeMillis();
        final long time = after - before;
        final double timeS = (after - before) / 1000.0;
        System.out.println("Walking through the tokens for the file: " + filename + " took " + time
                + "ms, or " + timeS + " seconds");
    }
}
