/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

/**
 * @see PyYAML 3.06 for more information
 */
public interface YamlConfig {
    YamlConfig indent(final int indent);

    int getIndent();

    YamlConfig useHeader(final boolean useHeader);

    boolean useHeader();

    YamlConfig version(final Integer[] version);

    Integer[] version();

    YamlConfig explicitStart(final boolean expStart);

    boolean explicitStart();

    YamlConfig explicitEnd(final boolean expEnd);

    boolean explicitEnd();

    YamlConfig anchorFormat(final String format);

    String anchorFormat();

    // TODO it is not present in PyYAML. Do we need it ?
    YamlConfig explicitTypes(final boolean expTypes);

    boolean explicitTypes();

    YamlConfig canonical(final boolean canonical);

    boolean isCanonical();

    YamlConfig bestWidth(final int bestWidth);

    int getWidth();

    YamlConfig useBlock(final boolean useBlock);

    boolean useBlock();

    YamlConfig useFlow(final boolean useFlow);

    boolean useFlow();

    YamlConfig usePlain(final boolean usePlain);

    boolean usePlain();

    YamlConfig useSingle(final boolean useSingle);

    boolean useSingle();

    YamlConfig useDouble(final boolean useDouble);

    boolean useDouble();

    String getLineBreak();

}
