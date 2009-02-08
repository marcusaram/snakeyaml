/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.util.Map;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML< /a> for more information
 */
public class DumperOptions {
    private Character defaultStyle = null;
    private Boolean defaultFlowStyle = null;
    private boolean canonical = false;
    private boolean allowUnicode = false;
    private int indent = 2;
    private int bestWidth = 80;
    private String lineBreak = "\n";
    private boolean expStart = false;
    private boolean expEnd = false;
    private String expRoot = null;
    private Integer[] version = null;
    private Map<String, String> tags = null;

    public boolean isAllowUnicode() {
        return allowUnicode;
    }

    public void setAllowUnicode(boolean allowUnicode) {
        this.allowUnicode = allowUnicode;
    }

    public Character getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(Character defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public DumperOptions setIndent(final int indent) {
        this.indent = indent;
        return this;
    }

    public int getIndent() {
        return this.indent;
    }

    public DumperOptions setVersion(final Integer[] version) {
        this.version = version;
        return this;
    }

    public Integer[] getVersion() {
        return this.version;
    }

    public DumperOptions explicitStart(final boolean expStart) {
        this.expStart = expStart;
        return this;
    }

    public boolean explicitStart() {
        return this.expStart;
    }

    public DumperOptions explicitEnd(final boolean expEnd) {
        this.expEnd = expEnd;
        return this;
    }

    public boolean explicitEnd() {
        return this.expEnd;
    }

    public DumperOptions setCanonical(boolean canonical) {
        this.canonical = canonical;
        return this;
    }

    public boolean isCanonical() {
        return this.canonical;
    }

    public DumperOptions setWidth(int bestWidth) {
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

    public void setDefaultFlowStyle(Boolean defaultFlowStyle) {
        this.defaultFlowStyle = defaultFlowStyle;
    }

    public Boolean isDefaultFlowStyle() {
        return defaultFlowStyle;
    }

    public String getExpRoot() {
        return expRoot;
    }

    /**
     * @param expRoot
     *            - tag to be used for the root node or <code>null</code> to get
     *            the default
     */
    public void setExpRoot(String expRoot) {
        this.expRoot = expRoot;
    }

}
