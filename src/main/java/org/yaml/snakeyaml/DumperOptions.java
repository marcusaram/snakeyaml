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
    private boolean explicitStart = false;
    private boolean explicitEnd = false;
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

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public int getIndent() {
        return this.indent;
    }

    public void setVersion(Integer[] version) {
        this.version = version;
    }

    public Integer[] getVersion() {
        return this.version;
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

    public void setLineBreak(String lineBreak) {
        this.lineBreak = lineBreak;
    }

    public boolean isExplicitStart() {
        return explicitStart;
    }

    public void setExplicitStart(boolean explicitStart) {
        this.explicitStart = explicitStart;
    }

    public boolean isExplicitEnd() {
        return explicitEnd;
    }

    public void setExplicitEnd(boolean explicitEnd) {
        this.explicitEnd = explicitEnd;
    }
}
