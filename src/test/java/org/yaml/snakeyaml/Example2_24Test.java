/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.util.Map;

import junit.framework.TestCase;

import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

/**
 * Test Example 2.24 from the YAML specification
 * 
 * @author py4fun
 * @see http://yaml.org/spec/1.1/
 */
public class Example2_24Test extends TestCase {
    class MyConstructor extends Constructor {
        public MyConstructor() {
            this.yamlConstructors.put("!tag:clarkevans.com,2002:shape", new ConstructShape());
            this.yamlConstructors.put("!tag:clarkevans.com,2002:circle", new ConstructCircle());
            this.yamlConstructors.put("!tag:clarkevans.com,2002:line", new ConstructLine());
            this.yamlConstructors.put("!tag:clarkevans.com,2002:label", new ConstructLabel());
        }

        private class ConstructShape implements Construct {
            public Object construct(Node node) {
                String val = (String) constructScalar((ScalarNode) node);
                return val;
            }
        }

        private class ConstructCircle implements Construct {
            public Object construct(Node node) {
                String val = (String) constructScalar((ScalarNode) node);
                return val;
            }
        }

        private class ConstructLine implements Construct {
            public Object construct(Node node) {
                String val = (String) constructScalar((ScalarNode) node);
                return val;
            }
        }

        private class ConstructLabel implements Construct {
            public Object construct(Node node) {
                String val = (String) constructScalar((ScalarNode) node);
                return val;
            }
        }
    }

    private class Shape {
        private Circle circle;
        private Line line;
        private Label label;
    }

    private class Circle {
        private Map center;
        private Integer radius;

        public Map getCenter() {
            return center;
        }

        public void setCenter(Map center) {
            this.center = center;
        }

        public Integer getRadius() {
            return radius;
        }

        public void setRadius(Integer radius) {
            this.radius = radius;
        }
    }

    private class Line {
        private Map start;
        private Map finish;

        public Map getStart() {
            return start;
        }

        public void setStart(Map start) {
            this.start = start;
        }

        public Map getFinish() {
            return finish;
        }

        public void setFinish(Map finish) {
            this.finish = finish;
        }
    }

    private class Label {
        private Map start;
        private Integer colpr;
        private String text;

        public Map getStart() {
            return start;
        }

        public void setStart(Map start) {
            this.start = start;
        }

        public Integer getColpr() {
            return colpr;
        }

        public void setColpr(Integer colpr) {
            this.colpr = colpr;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public void testExample_2_24() {
        YamlDocument document = new YamlDocument("example2_24.yaml", true, new MyConstructor());
    }
}
