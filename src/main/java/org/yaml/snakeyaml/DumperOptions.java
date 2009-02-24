/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.util.Map;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class DumperOptions {
    public enum DefaultScalarStyle {
        DOUBLE_QUOTED(new Character('"')), SINGLE_QUOTED(new Character('\'')), LITERAL(
                new Character('|')), FOLDED(new Character('>')), PLAIN(null);
        private Character styleChar;

        private DefaultScalarStyle(Character defaultStyle) {
            this.styleChar = defaultStyle;
        }

        public Character getChar() {
            return styleChar;
        }

        @Override
        public String toString() {
            return "Scalar style: '" + styleChar + "'";
        }
    }

    public enum DefaultFlowStyle {
        FLOW(Boolean.TRUE), BLOCK(Boolean.FALSE), AUTO(null);

        private Boolean styleBoolean;

        private DefaultFlowStyle(Boolean defaultFlowStyle) {
            styleBoolean = defaultFlowStyle;
        }

        public Boolean getStyleBoolean() {
            return styleBoolean;
        }

        @Override
        public String toString() {
            return "DEFAULT_FLOW_STYLE: '" + styleBoolean + "'";
        }
    }

    private DefaultScalarStyle defaultStyle = DefaultScalarStyle.PLAIN;
    private DefaultFlowStyle defaultFlowStyle = DefaultFlowStyle.AUTO;
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

    /**
     * Specify whether to emit non-ASCII printable Unicode characters (to
     * support ASCII terminals). The default value is true.
     * 
     * @param allowUnicode
     *            - if allowUnicode is false then all non-ASCII characters are
     *            escaped
     */
    public void setAllowUnicode(boolean allowUnicode) {
        this.allowUnicode = allowUnicode;
    }

    public DefaultScalarStyle getDefaultStyle() {
        return defaultStyle;
    }

    /**
     * Set default style for scalars. See YAML 1.1 specification, 2.3 Scalars
     * (http://yaml.org/spec/1.1/#id858081)
     * 
     * @param defaultStyle
     */
    public void setDefaultStyle(DefaultScalarStyle defaultStyle) {
        if (defaultStyle == null) {
            throw new NullPointerException("Use explicit DEFAULT_STYLE enum.");
        }
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

    /**
     * Specify the preferred width to emit scalars. When the scalar
     * representation takes more then the preferred with the scalar will be
     * split into a few lines. The default is 80.
     * 
     * @param bestWidth
     *            - the preferred with for scalars.
     */
    public void setWidth(int bestWidth) {
        this.bestWidth = bestWidth;
    }

    public int getWidth() {
        return this.bestWidth;
    }

    public String getLineBreak() {
        return lineBreak;
    }

    public void setDefaultFlowStyle(DefaultFlowStyle defaultFlowStyle) {
        this.defaultFlowStyle = defaultFlowStyle;
    }

    public DefaultFlowStyle getDefaultFlowStyle() {
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

    /**
     * Specify the line break to separate the lines. It is platform specific:
     * Windows - "\r\n", MacOS - "\r", Linux - "\n". The default value is the
     * one for Linux.
     * 
     * @param lineBreak
     *            - String to be used as a line separator.
     */
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
