/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

/**
 * @see PyYAML 3.06 for more information
 */
public interface YamlConfig {
    YamlConfig indent(final int indent);

    int indent();

    YamlConfig useHeader(final boolean useHeader);

    boolean useHeader();

    YamlConfig useVersion(final boolean useVersion);

    boolean useVersion();

    YamlConfig version(final String version);

    String version();

    YamlConfig explicitStart(final boolean expStart);

    boolean explicitStart();

    YamlConfig explicitEnd(final boolean expEnd);

    boolean explicitEnd();

    YamlConfig anchorFormat(final String format);

    String anchorFormat();

    YamlConfig explicitTypes(final boolean expTypes);

    boolean explicitTypes();

    YamlConfig canonical(final boolean canonical);

    boolean canonical();

    YamlConfig bestWidth(final int bestWidth);

    int bestWidth();

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
}
