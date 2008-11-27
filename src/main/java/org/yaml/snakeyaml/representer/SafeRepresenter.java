/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Represent;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.serializer.Serializer;
import org.yaml.snakeyaml.util.Base64Coder;

/**
 * @see PyYAML 3.06 for more information
 */
public class SafeRepresenter extends BaseRepresenter {

    @SuppressWarnings("unchecked")
    public SafeRepresenter(Serializer serializer, Map<Class, Represent> representers,
            char default_style, Boolean default_flow_style) {
        super(serializer, representers, default_style, default_flow_style);
        this.nullRepresenter = new RepresentNull();
        this.representers.put(String.class, new RepresentString());
        this.representers.put(Boolean.class, new RepresentBoolean());
        this.multiRepresenters.put(Number.class, new RepresentNumber());
        this.multiRepresenters.put(List.class, new RepresentList());
        this.multiRepresenters.put(Map.class, new RepresentMap());
        this.multiRepresenters.put(Set.class, new RepresentSet());
        this.representers.put(Date.class, new RepresentDate());
        this.representers.put(null, new RepresentUndefined());
    }

    protected boolean ignoreAliases(Object data) {
        if (data == null) {
            return true;
        }
        if (data instanceof Object[]) {
            Object[] array = (Object[]) data;
            if (array.length == 0) {
                return true;
            }
        }
        return data instanceof String || data instanceof Boolean || data instanceof Integer
                || data instanceof Long || data instanceof Float || data instanceof Double;
    }

    private class RepresentNull implements Represent {
        public Node representData(Object data) {
            return representScalar("tag:yaml.org,2002:null", "null");
        }
    }

    public static Pattern BINARY_PATTERN = Pattern
            .compile("^[\\x00-\\x08,\\x0B,\\x0C,\\x0E-\\x1F]");

    private class RepresentString implements Represent {
        public Node representData(Object data) {
            String tag = "tag:yaml.org,2002:str";
            Character style = null;
            String value = data.toString();
            if (BINARY_PATTERN.matcher(value).find()) {
                tag = "tag:yaml.org,2002:binary";
                Charset charset = Charset.forName("ISO-8859-1");
                char[] binary = Base64Coder.encode(value.getBytes(charset));
                value = String.valueOf(binary);
                style = '|';
            }
            return representScalar(tag, value, style);
        }
    }

    private class RepresentBoolean implements Represent {
        public Node representData(Object data) {
            String value;
            if (Boolean.TRUE.equals(data)) {
                value = "true";
            } else {
                value = "false";
            }
            return representScalar("tag:yaml.org,2002:bool", value);
        }
    }

    private class RepresentNumber implements Represent {
        public Node representData(Object data) {
            String tag;
            String value;
            if (data instanceof Byte || data instanceof Short || data instanceof Integer
                    || data instanceof Long || data instanceof BigInteger) {
                tag = "tag:yaml.org,2002:int";
                value = data.toString();
            } else {
                Number number = (Number) data;
                tag = "tag:yaml.org,2002:float";
                if (number.doubleValue() == Double.NaN) {
                    value = ".nan";
                } else if (number.doubleValue() == Double.POSITIVE_INFINITY) {
                    value = ".inf";
                } else if (number.doubleValue() == Double.NEGATIVE_INFINITY) {
                    value = "-.inf";
                }
                value = number.toString().toLowerCase();
            }
            return representScalar(tag, value);
        }
    }

    private class RepresentList implements Represent {
        @SuppressWarnings("unchecked")
        public Node representData(Object data) {
            return representSequence("tag:yaml.org,2002:seq", (List<Object>) data, null);
        }
    }

    private class RepresentMap implements Represent {
        @SuppressWarnings("unchecked")
        public Node representData(Object data) {
            return representMapping("tag:yaml.org,2002:map", (Map<Object, Object>) data, null);
        }
    }

    private class RepresentSet implements Represent {
        @SuppressWarnings("unchecked")
        public Node representData(Object data) {
            Map<Object, Object> value = new LinkedHashMap<Object, Object>();
            Set<Object> set = (Set<Object>) data;
            for (Object key : set) {
                value.put(key, null);
            }
            return representMapping("tag:yaml.org,2002:set", value, null);
        }
    }

    private class RepresentDate implements Represent {
        @SuppressWarnings("unchecked")
        public Node representData(Object data) {
            // because SimpleDateFormat ignores timezone we have to use Calendar
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTime((Date) data);
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
            return representScalar("tag:yaml.org,2002:timestamp", buffer.toString(), null);
        }
    }

    private class RepresentJavaBean implements Represent {
        @SuppressWarnings("unchecked")
        public Node representData(Object data) {
            Map values = new HashMap();
            Method[] ems = data.getClass().getMethods();
            for (int i = 0, j = ems.length; i < j; i++) {
                if (ems[i].getParameterTypes().length == 0) {
                    String name = ems[i].getName();
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
                            values.put(pname, ems[i].invoke(data, (Object[]) null));
                        } catch (Exception exe) {
                            values.put(pname, null);
                        }
                    }
                }
            }
            return representMapping(data.getClass().toString(), values, null);
        }
    }

    private class RepresentUndefined implements Represent {
        public Node representData(Object data) {
            throw new RepresenterException("cannot represent an object: " + data.toString());
        }
    }

}
