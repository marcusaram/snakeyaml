/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @see PyYAML 3.06 for more information
 */
public class Dumper {
    private char defaultStyle = '\'';
    private boolean canonical = false;
    private int indent = 2;
    private int bestWidth = 80;
    private String lineBreak = "\n";
    private boolean expStart = false;
    private boolean expEnd = false;
    private Integer[] version = null;
    private Map<String, String> tags = null;

    public char getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(char defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public Dumper indent(final int indent) {
        this.indent = indent;
        return this;
    }

    public int getIndent() {
        return this.indent;
    }

    public Dumper version(final Integer[] version) {
        this.version = version;
        return this;
    }

    public Integer[] version() {
        return this.version;
    }

    public Dumper explicitStart(final boolean expStart) {
        this.expStart = expStart;
        return this;
    }

    public boolean explicitStart() {
        return this.expStart;
    }

    public Dumper explicitEnd(final boolean expEnd) {
        this.expEnd = expEnd;
        return this;
    }

    public boolean explicitEnd() {
        return this.expEnd;
    }

    public Dumper canonical(final boolean canonical) {
        this.canonical = canonical;
        return this;
    }

    public boolean isCanonical() {
        return this.canonical;
    }

    public Dumper bestWidth(final int bestWidth) {
        this.bestWidth = bestWidth;
        return this;
    }

    public int getWidth() {
        return this.bestWidth;
    }

    public String getLineBreak() {
        return lineBreak;
    }

    public Map<String, String> tags() {
        return tags;
    }

    /**
     * Add a representer for the given type. Representer is accepting an
     * instance of the given data type and producing the corresponding
     * representation node.
     * 
     * @param clazz
     * @param representer
     */
    public void addRepresenter(Class clazz, Represent representer) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add a representer for the given type. Multi-representer is accepting an
     * instance of the given data type or subtype and producing the
     * corresponding representation node.
     * 
     * @param clazz
     * @param representer
     */
    public void addMultiRepresenter(Class clazz, Represent representer) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add an implicit scalar detector. If an implicit scalar value matches the
     * given regexp, the corresponding tag is assigned to the scalar. first is a
     * sequence of possible initial characters or None.
     * 
     * @param tag -
     *            tag to assifgn to the node
     * @param regexp -
     *            regular expression to match against
     * @param first -
     *            a sequence of possible initial characters or None
     */
    public void addImplicitResolver(String tag, Pattern regexp, String first) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add a path based resolver for the given tag. A path is a list of keys
     * that forms a path to a node in the representation tree. Keys can be
     * string values, integers, or None.
     * 
     * @param tag
     * @param path
     * @param kind
     */
    public void addPathResolver(String tag, List path, Object kind) {
    }
}
