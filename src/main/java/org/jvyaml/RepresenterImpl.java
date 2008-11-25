/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.yaml.snakeyaml.YamlConfig;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.serializer.Serializer;
import org.yaml.snakeyaml.util.Base64Coder;

/**
 * @see PyYAML 3.06 for more information
 */
public class RepresenterImpl implements Representer {
    private final Serializer serializer;
    private final char defaultStyle;
    private final Map representedObjects;

    public RepresenterImpl(final Serializer serializer, final YamlConfig opts) {
        this.serializer = serializer;
        this.defaultStyle = opts.useDouble() ? '"' : (opts.useSingle() ? '\'' : 0);
        this.representedObjects = new HashMap();
    }

    private Node representData(final Object data) throws IOException {
        String aliasKey = null;
        Node node = null;
        // TODO in PyYAML if is inversed
        if (!ignoreAliases(data)) {
            aliasKey = "" + System.identityHashCode(data);
        }

        if (null != aliasKey) {
            if (this.representedObjects.containsKey(aliasKey)) {
                node = (Node) this.representedObjects.get(aliasKey);
                if (null == node) {
                    throw new RepresenterException("recursive objects are not allowed: " + data);
                }
                return node;
            }
            this.representedObjects.put(aliasKey, null);
        }

        node = getNodeCreatorFor(data).toYamlNode(this);

        if (aliasKey != null) {
            this.representedObjects.put(aliasKey, node);
        }

        return node;
    }

    public Node scalar(final String tag, final String value, char style) throws IOException {
        return representScalar(tag, value, style);
    }

    public Node representScalar(final String tag, final String value, char style)
            throws IOException {
        char realStyle = style == 0 ? this.defaultStyle : style;
        return new ScalarNode(tag, value, null, null, style);
    }

    public Node seq(final String tag, final List sequence, final boolean flowStyle)
            throws IOException {
        return representSequence(tag, sequence, flowStyle);
    }

    public Node representSequence(final String tag, final List sequence, final boolean flowStyle)
            throws IOException {
        List value = new ArrayList(sequence.size());
        for (final Iterator iter = sequence.iterator(); iter.hasNext();) {
            value.add(representData(iter.next()));
        }
        return new SequenceNode(tag, value, null, null, flowStyle);
    }

    public Node map(final String tag, final Map mapping, final boolean flowStyle)
            throws IOException {
        return representMapping(tag, mapping, flowStyle);
    }

    public Node representMapping(final String tag, final Map mapping, final boolean flowStyle)
            throws IOException {
        Map value = new HashMap();
        for (final Iterator iter = mapping.keySet().iterator(); iter.hasNext();) {
            final Object itemKey = iter.next();
            final Object itemValue = mapping.get(itemKey);
            value.put(representData(itemKey), representData(itemValue));
        }
        return new MappingNode(tag, value, null, null, flowStyle);
    }

    public void represent(final Object data) throws IOException {
        Node node = representData(data);
        this.serializer.serialize(node);
        this.representedObjects.clear();
    }

    protected boolean ignoreAliases(final Object data) {
        return false;
    }

    protected YAMLNodeCreator getNodeCreatorFor(final Object data) {
        if (data instanceof YAMLNodeCreator) {
            return (YAMLNodeCreator) data;
        } else if (data instanceof Map) {
            return new MappingYAMLNodeCreator(data);
        } else if (data instanceof List) {
            return new SequenceYAMLNodeCreator(data);
        } else if (data instanceof Set) {
            return new SetYAMLNodeCreator(data);
        } else if (data instanceof Date) {
            return new DateYAMLNodeCreator(data);
        } else if (data instanceof String) {
            return new StringYAMLNodeCreator(data);
        } else if (data instanceof Number) {
            return new NumberYAMLNodeCreator(data);
        } else if (data instanceof Boolean) {
            return new ScalarYAMLNodeCreator("tag:yaml.org,2002:bool", data);
        } else if (data == null) {
            return new ScalarYAMLNodeCreator("tag:yaml.org,2002:null", "null");
        } else if (data.getClass().isArray()) {
            return new ArrayYAMLNodeCreator(data);
        } else if (data instanceof ByteBuffer) {
            return new BinaryYAMLNodeCreator(data);
        } else { // Fallback, handles JavaBeans and other
            return new JavaBeanYAMLNodeCreator(data);
        }
    }

    public static class DateYAMLNodeCreator implements YAMLNodeCreator {
        private final Calendar calendar;

        public DateYAMLNodeCreator(final Object data) {
            final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTime((Date) data);
            this.calendar = cal;
        }

        public String taguri() {
            return "tag:yaml.org,2002:timestamp";
        }

        public Node toYamlNode(final Representer representer) throws IOException {
            // because SimpleDateFormat ignores timezone we have to use Calendar
            int years = calendar.get(Calendar.YEAR);
            int months = calendar.get(Calendar.MONTH) + 1; // 0..12
            int days = calendar.get(Calendar.DAY_OF_MONTH); // 1..31
            int hour24 = calendar.get(Calendar.HOUR_OF_DAY); // 0..24
            int minutes = calendar.get(Calendar.MINUTE); // 0..59
            int seconds = calendar.get(Calendar.SECOND); // 0..59
            int millis = calendar.get(Calendar.MILLISECOND);
            StringBuffer buffer = new StringBuffer(String.valueOf(years));
            buffer.append("-");
            if (months < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(months));
            buffer.append("-");
            if (days < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(days));
            buffer.append("T");
            if (hour24 < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(hour24));
            buffer.append(":");
            if (minutes < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(minutes));
            buffer.append(":");
            if (seconds < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(seconds));
            if (millis > 0) {
                if (millis < 10) {
                    buffer.append("00");
                } else if (millis < 100) {
                    buffer.append("0");
                }
                buffer.append(".");
                buffer.append(String.valueOf(millis));
            }
            buffer.append("Z");
            return representer.scalar(taguri(), buffer.toString(), (char) 0);
        }
    }

    public static class SetYAMLNodeCreator implements YAMLNodeCreator {
        private final Set data;

        public SetYAMLNodeCreator(final Object data) {
            this.data = (Set) data;
        }

        public String taguri() {
            return "tag:yaml.org,2002:set";
        }

        public Node toYamlNode(final Representer representer) throws IOException {
            final Map entries = new HashMap();
            for (final Iterator iter = data.iterator(); iter.hasNext();) {
                entries.put(iter.next(), null);
            }
            return representer.map(taguri(), entries, false);
        }
    }

    public static class ArrayYAMLNodeCreator implements YAMLNodeCreator {
        private final Object data;

        public ArrayYAMLNodeCreator(final Object data) {
            this.data = data;
        }

        public String taguri() {
            return "tag:yaml.org,2002:seq";
        }

        public Node toYamlNode(final Representer representer) throws IOException {
            final int l = java.lang.reflect.Array.getLength(data);
            final List lst = new ArrayList(l);
            for (int i = 0; i < l; i++) {
                lst.add(java.lang.reflect.Array.get(data, i));
            }
            return representer.seq(taguri(), lst, false);
        }
    }

    public static class NumberYAMLNodeCreator implements YAMLNodeCreator {
        private final Number data;

        public NumberYAMLNodeCreator(final Object data) {
            this.data = (Number) data;
        }

        public String taguri() {
            if (data instanceof Float || data instanceof Double
                    || data instanceof java.math.BigDecimal) {
                return "tag:yaml.org,2002:float";
            } else {
                return "tag:yaml.org,2002:int";
            }
        }

        public Node toYamlNode(Representer representer) throws IOException {
            String str = data.toString();
            if (str.equals("Infinity")) {
                str = ".inf";
            } else if (str.equals("-Infinity")) {
                str = "-.inf";
            } else if (str.equals("NaN")) {
                str = ".nan";
            }
            return representer.scalar(taguri(), str, (char) 0);
        }
    }

    public static class BinaryYAMLNodeCreator implements YAMLNodeCreator {
        private final ByteBuffer data;

        public BinaryYAMLNodeCreator(final Object data) {
            this.data = (ByteBuffer) data;
        }

        public String taguri() {
            return "tag:yaml.org,2002:binary";
        }

        public Node toYamlNode(Representer representer) throws IOException {
            byte[] array = data.array();
            char[] encoded = Base64Coder.encode(array);
            String str = String.valueOf(encoded);
            return representer.scalar(taguri(), str, (char) 0);
        }
    }

    public static class ScalarYAMLNodeCreator implements YAMLNodeCreator {
        private final String tag;
        private final Object data;

        public ScalarYAMLNodeCreator(final String tag, final Object data) {
            this.tag = tag;
            this.data = data;
        }

        public String taguri() {
            return this.tag;
        }

        public Node toYamlNode(Representer representer) throws IOException {
            return representer.scalar(taguri(), data.toString(), (char) 0);
        }
    }

    public static class StringYAMLNodeCreator implements YAMLNodeCreator {
        private final Object data;

        public StringYAMLNodeCreator(final Object data) {
            this.data = data;
        }

        public String taguri() {
            if (data instanceof String) {
                return "tag:yaml.org,2002:str";
            } else {
                return "tag:yaml.org,2002:str:" + data.getClass().getName();
            }
        }

        public Node toYamlNode(Representer representer) throws IOException {
            return representer.scalar(taguri(), data.toString(), (char) 0);
        }
    }

    public static class SequenceYAMLNodeCreator implements YAMLNodeCreator {
        private final List data;

        public SequenceYAMLNodeCreator(final Object data) {
            this.data = (List) data;
        }

        public String taguri() {
            if (data instanceof ArrayList) {
                return "tag:yaml.org,2002:seq";
            } else {
                return "tag:yaml.org,2002:seq:" + data.getClass().getName();
            }
        }

        public Node toYamlNode(Representer representer) throws IOException {
            return representer.seq(taguri(), data, false);
        }
    }

    public static class MappingYAMLNodeCreator implements YAMLNodeCreator {
        private final Map data;

        public MappingYAMLNodeCreator(final Object data) {
            this.data = (Map) data;
        }

        public String taguri() {
            if (data instanceof HashMap) {
                return "tag:yaml.org,2002:map";
            } else {
                return "tag:yaml.org,2002:map:" + data.getClass().getName();
            }
        }

        public Node toYamlNode(Representer representer) throws IOException {
            return representer.map(taguri(), data, false);
        }
    }

    public static class JavaBeanYAMLNodeCreator implements YAMLNodeCreator {
        private final Object data;

        public JavaBeanYAMLNodeCreator(final Object data) {
            this.data = data;
        }

        public String taguri() {
            return "!java/object:" + data.getClass().getName();
        }

        public Node toYamlNode(Representer representer) throws IOException {
            final Map values = new HashMap();
            final Method[] ems = data.getClass().getMethods();
            for (int i = 0, j = ems.length; i < j; i++) {
                if (ems[i].getParameterTypes().length == 0) {
                    final String name = ems[i].getName();
                    if (name.equals("getClass")) {
                        continue;
                    }
                    String pname = null;
                    if (name.startsWith("get")) {
                        pname = "" + Character.toLowerCase(name.charAt(3)) + name.substring(4);
                    } else if (name.startsWith("is")) {
                        pname = "" + Character.toLowerCase(name.charAt(2)) + name.substring(3);
                    }
                    if (null != pname) {
                        try {
                            values.put(pname, ems[i].invoke(data, null));
                        } catch (final Exception exe) {
                            values.put(pname, null);
                        }
                    }
                }
            }
            return representer.map(taguri(), values, false);
        }
    }

}
