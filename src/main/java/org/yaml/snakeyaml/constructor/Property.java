/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

abstract class Property implements Comparable<Property> {
    private final String name;
    private final Class type;

    public Property(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Property other = (Property) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    public Class getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public int compareTo(Property o) {
        int comparison = name.compareTo(o.name);
        if (comparison != 0) {
            // Sort id and name above all other fields.
            if (name.equals("id"))
                return -1;
            if (o.name.equals("id"))
                return 1;
            if (name.equals("name"))
                return -1;
            if (o.name.equals("name"))
                return 1;
        }
        return comparison;
    }

    abstract public void set(Object object, Object value) throws Exception;

    abstract public Object get(Object object) throws Exception;
}