package org.yaml.snakeyaml.representer;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

public class RepresentTest extends TestCase {

    public void testCustomRepresenter() {
        Yaml yaml = new Yaml();
        // yaml.addRepresenter(Dice.class, new CoinRepresenter());
        String output = yaml.dump(new Dice("A", 1));
        // assertEquals("!!Dice 'Ad1'\n", output);
    }

    class Dice {
        private String prefix;
        private int suffix;

        public Dice(String prefix, int suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getPrefix() {
            return prefix;
        }

        public int getSuffix() {
            return suffix;
        }
    }

    class MyRepresenter extends Representer {
        public MyRepresenter() {
            this.representers.put(Dice.class, new RepresentDice());
        }

        private class RepresentDice implements Represent {
            public Node representData(Object data) {
                Dice coin = (Dice) data;
                String value = coin.getPrefix() + "d" + coin.getSuffix();
                return representScalar("!!Dice", value);
            }
        }
    }
}
