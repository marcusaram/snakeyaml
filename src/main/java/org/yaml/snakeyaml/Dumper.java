/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.util.Map;

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
    private Map<String, String> tags;

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
}
