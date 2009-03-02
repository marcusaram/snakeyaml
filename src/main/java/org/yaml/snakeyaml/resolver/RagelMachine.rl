/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.resolver;

public class RagelMachine {
    %%{
        machine snakeyaml;
        action bool_tag { tag = "tag:yaml.org,2002:bool"; }
        
        Bool = ("yes" | "Yes" | "YES" | "no" | "No" | "NO" | 
                "true" | "True" | "TRUE" | "false" | "False" | "FALSE" | 
                "on" | "On" | "ON" | "off" | "Off" | "OFF") %/bool_tag;
        
        Scalar = Bool;
        main := Scalar;
    }%%
    
    %% write data nofinal;

    public String scan(String scalar) {
        if (scalar == null) {
            throw new NullPointerException("Scalar must be provided");
        }
        String tag = null;
        int cs = 0;
        int p = 0;
        int pe = scalar.length();
        int eof = pe;
        char[] data;
        if (pe == 0) {
            // NULL value
            data = new char[] { '~' };
            pe = 1;
        } else {
            data = scalar.toCharArray();
        }
        %%{
            # Initialize and execute.
            write init;
            write exec;
        }%%

        return tag;
    }
}