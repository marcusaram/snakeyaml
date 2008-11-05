/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @see PyYAML 3.06 for more information
 */
public class DefaultYAMLConfig implements YamlConfig {
    private int indent = 2;
    private boolean useHeader = false;
    private boolean useVersion = false;
    private String version = "1.1";
    private boolean expStart = true;
    private boolean expEnd = false;
    private String format = "id{0,number,####}";
    private boolean expTypes = false;
    private boolean canonical = false;
    private int bestWidth = 80;
    private boolean useBlock = false;
    private boolean useFlow = false;
    private boolean usePlain = false;
    private boolean useSingle = false;
    private boolean useDouble = false;

    public YamlConfig indent(final int indent) {
        this.indent = indent;
        return this;
    }

    public int indent() {
        return this.indent;
    }

    public YamlConfig useHeader(final boolean useHeader) {
        this.useHeader = useHeader;
        return this;
    }

    public boolean useHeader() {
        return this.useHeader;
    }

    public YamlConfig useVersion(final boolean useVersion) {
        this.useVersion = useVersion;
        return this;
    }

    public boolean useVersion() {
        return this.useVersion;
    }

    public YamlConfig version(final String version) {
        this.version = version;
        return this;
    }

    public String version() {
        return this.version;
    }

    public YamlConfig explicitStart(final boolean expStart) {
        this.expStart = expStart;
        return this;
    }

    public boolean explicitStart() {
        return this.expStart;
    }

    public YamlConfig explicitEnd(final boolean expEnd) {
        this.expEnd = expEnd;
        return this;
    }

    public boolean explicitEnd() {
        return this.expEnd;
    }

    public YamlConfig anchorFormat(final String format) {
        this.format = format;
        return this;
    }

    public String anchorFormat() {
        return this.format;
    }

    public YamlConfig explicitTypes(final boolean expTypes) {
        this.expTypes = expTypes;
        return this;
    }

    public boolean explicitTypes() {
        return this.expTypes;
    }

    public YamlConfig canonical(final boolean canonical) {
        this.canonical = canonical;
        return this;
    }

    public boolean canonical() {
        return this.canonical;
    }

    public YamlConfig bestWidth(final int bestWidth) {
        this.bestWidth = bestWidth;
        return this;
    }

    public int bestWidth() {
        return this.bestWidth;
    }

    public YamlConfig useBlock(final boolean useBlock) {
        this.useBlock = useBlock;
        return this;
    }

    public boolean useBlock() {
        return this.useBlock;
    }

    public YamlConfig useFlow(final boolean useFlow) {
        this.useFlow = useFlow;
        return this;
    }

    public boolean useFlow() {
        return this.useFlow;
    }

    public YamlConfig usePlain(final boolean usePlain) {
        this.usePlain = usePlain;
        return this;
    }

    public boolean usePlain() {
        return this.usePlain;
    }

    public YamlConfig useSingle(final boolean useSingle) {
        this.useSingle = useSingle;
        return this;
    }

    public boolean useSingle() {
        return this.useSingle;
    }

    public YamlConfig useDouble(final boolean useDouble) {
        this.useDouble = useDouble;
        return this;
    }

    public boolean useDouble() {
        return this.useDouble;
    }
}
