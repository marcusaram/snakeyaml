package org.yaml.snakeyaml.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jvyaml.Mark;
import org.jvyaml.YAMLException;

/**
 * Reader: determines the data encoding and converts it to unicode, checks if
 * characters are in allowed range, adds '\0' to the end. <br/> Yeah, it's ugly
 * and slow.
 * 
 * @see yaml.reader in PyYAML 3.06
 */
public class Reader {
    //changed from PyYAML: \uFFFD excluded because Java returns it in case of data corruption
    final static Pattern NON_PRINTABLE = Pattern
            .compile("[^\t\n\r\u0020-\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFC]");
    private final static String LINEBR = "\n\u0085\u2028\u2029";

    private String name;
    private InputStream stream;
    private int pointer = 0;
    private boolean eof = true;
    private StringBuffer buffer;
    private int index = 0;// for error reporting only
    private int line = 0;// for error reporting only
    private int column = 0;
    private Charset encoding;

    public Reader(String stream) {
        this.name = "<string>";
        this.buffer = new StringBuffer();
        checkPrintable(stream);
        this.buffer.append(stream);
        this.encoding = null;
        this.stream = null;
        this.eof = true;
    }

    public Reader(InputStream stream) throws IOException {
        this.name = "<stream>";
        this.buffer = new StringBuffer();
        UnicodeInputStream unicodeStream = new UnicodeInputStream(stream, "UTF-8");
        this.encoding = Charset.forName(unicodeStream.getEncoding());
        this.stream = unicodeStream;
        this.eof = false;
    }

    void checkPrintable(final CharSequence data) {
        final Matcher em = NON_PRINTABLE.matcher(data);
        if (em.find()) {
            int position = this.index + this.buffer.length() - this.pointer + em.start();
            throw new ReaderException(this.name, position, em.group().charAt(0),
                    " special characters are not allowed");
        }
    }

    public Mark getMark() {
        if (this.stream == null) {
            return new Mark(this.name, this.index, this.line, this.column, this.buffer.toString(),
                    this.pointer);
        } else {
            return new Mark(this.name, this.index, this.line, this.column, null, 0);
        }
    }

    public void forward() {
        forward(1);
    }

    public void forward(final int length) {
        if (this.pointer + length + 1 >= this.buffer.length()) {
            update(length + 1);
        }
        char ch = 0;
        for (int i = 0; i < length; i++) {
            ch = this.buffer.charAt(this.pointer);
            this.pointer++;
            this.index++;
            if (LINEBR.indexOf(ch) != -1
                    || (ch == '\r' && this.buffer.charAt(this.pointer) != '\n')) {
                this.line++;
                this.column = 0;
            } else if (ch != '\uFEFF') {
                this.column++;
            }
        }
    }

    public char peek() {
        return peek(0);
    }

    public char peek(final int index) {
        if (this.pointer + index + 1 > this.buffer.length()) {
            update(index + 1);
        }
        return this.buffer.charAt(this.pointer + index);
    }

    public String prefix() {
        return prefix(1);
    }

    public String prefix(final int length) {
        if (this.pointer + length >= this.buffer.length()) {
            update(length);
        }
        if (this.pointer + length > this.buffer.length()) {
            return this.buffer.substring(this.pointer, this.buffer.length());
        } else {
            return this.buffer.substring(this.pointer, this.pointer + length);
        }
    }

    public void update(final int length) {
        this.buffer.delete(0, this.pointer);
        this.pointer = 0;
        while (this.buffer.length() < length) {
            String rawData = "";
            if (!this.eof) {
                byte[] data = new byte[1024];
                int converted = -2;
                try {
                    converted = this.stream.read(data);
                } catch (IOException ioe) {
                    throw new YAMLException(ioe);
                }
                if (converted == -1) {
                    this.eof = true;
                } else {
                    rawData = new String(data, 0, converted, this.encoding);
                }
            }
            checkPrintable(rawData);
            this.buffer.append(rawData);
            if (this.eof) {
                this.buffer.append('\0');
                break;
            }
        }
    }

    public int getColumn() {
        return column;
    }

    public Charset getEncoding() {
        return encoding;
    }
}
