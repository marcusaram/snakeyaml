/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.resolver;

public class RagelMachine {
    %%{
        machine snakeyaml;
        action bool_tag { tag = "tag:yaml.org,2002:bool"; }
        action merge_tag { tag = "tag:yaml.org,2002:merge"; }
        action null_tag { tag = "tag:yaml.org,2002:null"; }
        action value_tag { tag = "tag:yaml.org,2002:value"; }
        action int_tag { tag = "tag:yaml.org,2002:int"; }
        
        Bool = ("yes" | "Yes" | "YES" | "no" | "No" | "NO" | 
                "true" | "True" | "TRUE" | "false" | "False" | "FALSE" | 
                "on" | "On" | "ON" | "off" | "Off" | "OFF") %/bool_tag;
        Merge = "<<" %/merge_tag;
        Value = "=" %/value_tag;
        Null  = ("~" | "null" | "Null" | "null" | "NULL" | " ") %/null_tag;
        sign = "-" | "+";
        digit2 = digit | "_" | ",";
        binaryInt = "0b" [0-1_]+;
        octalInt = "0" [0-7_]+;
        decimalInt = "0" |
                     [1-9]digit2* (":" [0-5]? digit)*;
        hexaInt = "0x" [0-9a-fA-F_,]+;
        Int = sign? (binaryInt | octalInt | decimalInt | hexaInt) %/int_tag;
        
        Scalar = Bool | Null | Int | Merge | Value;
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