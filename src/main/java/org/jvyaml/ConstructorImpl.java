/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.regex.Pattern;;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
public class ConstructorImpl extends SafeConstructorImpl {
    private final static Map yamlConstructors = new HashMap();
    private final static Map yamlMultiConstructors = new HashMap();
    private final static Map yamlMultiRegexps = new HashMap();
    public YamlConstructor getYamlConstructor(final Object key) {
        YamlConstructor mine = (YamlConstructor)yamlConstructors.get(key);
        if(mine == null) {
            mine = super.getYamlConstructor(key);
        }
        return mine;
    }

    public YamlMultiConstructor getYamlMultiConstructor(final Object key) {
        YamlMultiConstructor mine = (YamlMultiConstructor)yamlMultiConstructors.get(key);
        if(mine == null) {
            mine = super.getYamlMultiConstructor(key);
        }
        return mine;
    }

    public Pattern getYamlMultiRegexp(final Object key) {
        Pattern mine = (Pattern)yamlMultiRegexps.get(key);
        if(mine == null) {
            mine = super.getYamlMultiRegexp(key);
        }
        return mine;
    }

    public Set getYamlMultiRegexps() {
        final Set all = new HashSet(super.getYamlMultiRegexps());
        all.addAll(yamlMultiRegexps.keySet());
        return all;
    }

    public static void addConstructor(final String tag, final YamlConstructor ctor) {
        yamlConstructors.put(tag,ctor);
    }

    public static void addMultiConstructor(final String tagPrefix, final YamlMultiConstructor ctor) {
        yamlMultiConstructors.put(tagPrefix,ctor);
        yamlMultiRegexps.put(tagPrefix,Pattern.compile("^"+tagPrefix));
    }

    public ConstructorImpl(final Composer composer) {
        super(composer);
    }

    public static void main(final String[] args) throws Exception {
        final String filename = args[0];
        System.out.println("Reading of file: \"" + filename + "\"");

        final StringBuffer input = new StringBuffer();
        final java.io.Reader reader = new java.io.FileReader(filename);
        char[] buff = new char[1024];
        int read = 0;
        while(true) {
            read = reader.read(buff);
            input.append(buff,0,read);
            if(read < 1024) {
                break;
            }
        }
        reader.close();
        final String str = input.toString();
        final long before = System.currentTimeMillis();
        for(int i=0;i<1;i++) {
            final Constructor ctor = new ConstructorImpl(new ComposerImpl(new ParserImpl(new ScannerImpl(str)),new ResolverImpl()));
            for(final Iterator iter = ctor.eachDocument();iter.hasNext();iter.next()) {
                                System.out.println(iter.next());
            }
        }
        final long after = System.currentTimeMillis();
        final long time = after-before;
        final double timeS = (after-before)/1000.0;
        System.out.println("Walking through the nodes for the file: " + filename + " took " + time + "ms, or " + timeS + " seconds"); 
    }
}// ConstructorImpl
