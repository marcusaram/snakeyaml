/* Copyright (c) 2006 Ola Bini
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
/**
 * $Id: YAML.java,v 1.1 2006/06/06 19:19:13 olabini Exp $
 */
package org.jvyaml;

import java.io.Reader;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class YAML {
    public static final String DEFAULT_SCALAR_TAG = "tag:yaml.org,2002:str";
    public static final String DEFAULT_SEQUENCE_TAG = "tag:yaml.org,2002:seq";
    public static final String DEFAULT_MAPPING_TAG = "tag:yaml.org,2002:map";

    public static Object load(final String io) {
        return load(io, new DefaultYAMLFactory());
    }

    public static Object load(final Reader io) {
        return load(io, new DefaultYAMLFactory());
    }

    public static Object load(final String io, final YAMLFactory fact) {
        final Constructor ctor = fact.createConstructor(fact.createComposer(fact.createParser(fact.createScanner(io)),fact.createResolver()));
        if(ctor.checkData()) {
            return ctor.getData();
        } else {
            return null;
        }
    }

    public static Object load(final Reader io, final YAMLFactory fact) {
        final Constructor ctor = fact.createConstructor(fact.createComposer(fact.createParser(fact.createScanner(io)),fact.createResolver()));
        if(ctor.checkData()) {
            return ctor.getData();
        } else {
            return null;
        }
    }
}// YAML
