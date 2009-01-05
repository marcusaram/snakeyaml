/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * Test Example 2.24 from the YAML specification
 * 
 * @author py4fun
 * @see http://yaml.org/spec/1.1/
 */
public class Example2_24Test extends TestCase {
    class MyConstructor extends Constructor {
        public MyConstructor() {
            this.yamlConstructors.put("tag:clarkevans.com,2002:shape", new ConstructShape());
            this.yamlConstructors.put("tag:clarkevans.com,2002:circle", new ConstructCircle());
            this.yamlConstructors.put("tag:clarkevans.com,2002:line", new ConstructLine());
            this.yamlConstructors.put("tag:clarkevans.com,2002:label", new ConstructLabel());
        }

        private class ConstructShape implements Construct {
            @SuppressWarnings("unchecked")
            public Object construct(Node node) {
                SequenceNode snode = (SequenceNode) node;
                List<Entity> values = (List<Entity>) constructSequence(snode);
                Shape shape = new Shape(values);
                return shape;
            }
        }

        private class ConstructCircle implements Construct {
            @SuppressWarnings("unchecked")
            public Object construct(Node node) {
                MappingNode mnode = (MappingNode) node;
                Map values = (Map) constructMapping(mnode);
                Circle circle = new Circle((Map) values.get("center"), (Integer) values
                        .get("radius"));
                return circle;
            }
        }

        private class ConstructLine implements Construct {
            @SuppressWarnings("unchecked")
            public Object construct(Node node) {
                MappingNode mnode = (MappingNode) node;
                Map values = (Map) constructMapping(mnode);
                Line line = new Line((Map) values.get("start"), (Map) values.get("finish"));
                return line;
            }
        }

        private class ConstructLabel implements Construct {
            @SuppressWarnings("unchecked")
            public Object construct(Node node) {
                MappingNode mnode = (MappingNode) node;
                Map values = (Map) constructMapping(mnode);
                Label label = new Label((Map) values.get("start"), (Integer) values.get("color"),
                        (String) values.get("text"));
                return label;
            }
        }
    }

    private class Shape {
        private List<Entity> entities;

        public List<Entity> getEntities() {
            return entities;
        }

        public Shape(List<Entity> entities) {
            this.entities = entities;
        }
    }

    private class Entity {

    }

    private class Circle extends Entity {
        private Map center;
        private Integer radius;

        public Circle(Map center, Integer radius) {
            this.center = center;
            this.radius = radius;
        }

        public Map getCenter() {
            return center;
        }

        public Integer getRadius() {
            return radius;
        }
    }

    private class Line extends Entity {
        private Map start;
        private Map finish;

        public Line(Map start, Map finish) {
            this.start = start;
            this.finish = finish;
        }

        public Map getStart() {
            return start;
        }

        public Map getFinish() {
            return finish;
        }
    }

    private class Label extends Entity {
        private Map start;
        private Integer color;
        private String text;

        public Label(Map start, Integer color, String text) {
            this.start = start;
            this.color = color;
            this.text = text;
        }

        public Map getStart() {
            return start;
        }

        public Integer getColor() {
            return color;
        }

        public String getText() {
            return text;
        }
    }

    public void testExample_2_24() throws IOException {
        Loader loader = new Loader(new MyConstructor());
        Yaml yaml = new Yaml(loader);
        Shape shape = (Shape) yaml.load(Util.getLocalResource("specification/example2_24.yaml"));
        assertNotNull(shape);
        // YamlDocument document = new YamlDocument("example2_24.yaml", true,
        // new MyConstructor());
    }
}
