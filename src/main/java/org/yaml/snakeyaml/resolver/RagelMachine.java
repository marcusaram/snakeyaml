/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.resolver;

public class RagelMachine {

    public String scan(String scalar) {
        if (scalar == null) {
            throw new NullPointerException("Scalar must be provided");
        }
        String tag = null;
        int cs;
        int p = 0;
        int pe = scalar.length();
        char[] data;
        if (pe == 0) {
            // NULL value
            data = new char[] { '~' };
            pe = 1;
        } else {
            data = scalar.toCharArray();
        }
        return tag;
    }
}
