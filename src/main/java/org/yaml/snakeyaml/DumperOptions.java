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
    private boolean allowUnicode = true;
    private int indent = 2;
    private int bestWidth = 80;
    private String lineBreak = "\n";
    private boolean explicitStart = false;
    private boolean explicitEnd = false;
    private String explicitRoot = null;
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

    public void setWidth(int bestWidth) {
        this.bestWidth = bestWidth;
    }

    public int getWidth() {
        return this.bestWidth;
    }

    public String getLineBreak() {
        return lineBreak;
    }

    public void setDefaultFlowStyle(Boolean defaultFlowStyle) {
        this.defaultFlowStyle = defaultFlowStyle;
    }

    public Boolean isDefaultFlowStyle() {
        return defaultFlowStyle;
    }

    public String getExplicitRoot() {
        return explicitRoot;
    }

    /**
     * @param expRoot
     *            - tag to be used for the root node or <code>null</code> to get
     *            the default
     */
    public void setExplicitRoot(String expRoot) {
        this.explicitRoot = expRoot;
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

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
