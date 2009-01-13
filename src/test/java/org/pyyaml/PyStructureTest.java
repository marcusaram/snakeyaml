/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.pyyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ScalarEvent;

/**
 * @see imported from PyYAML
 */
public class PyStructureTest extends PyImportTest {

    private void compareEvents(List<Event> events1, List<Event> events2, boolean full) {
        assertEquals(events1.size(), events2.size());
        Iterator<Event> iter1 = events1.iterator();
        Iterator<Event> iter2 = events2.iterator();
        while (iter1.hasNext()) {
            Event event1 = iter1.next();
            Event event2 = iter2.next();
            assertEquals(event1.getClass(), event2.getClass());
            if (event1 instanceof AliasEvent && full) {
                assertEquals(((AliasEvent) event1).getAnchor(), ((AliasEvent) event2).getAnchor());
            }
            if (event1 instanceof CollectionStartEvent) {
                String tag1 = ((CollectionStartEvent) event1).getTag();
                String tag2 = ((CollectionStartEvent) event1).getTag();
                if (tag1 != null && !"!".equals(tag1) && tag2 != null && !"!".equals(tag1)) {
                    assertEquals(tag1, tag2);
                }
            }
            if (event1 instanceof ScalarEvent) {
                ScalarEvent scalar1 = (ScalarEvent) event1;
                ScalarEvent scalar2 = (ScalarEvent) event2;
                boolean[] oldImplicit = scalar1.getImplicit();
                boolean[] newImplicit = scalar2.getImplicit();
                if (!oldImplicit[0] && !oldImplicit[1] && !newImplicit[0] && !newImplicit[1]) {
                    assertEquals(scalar1.getTag(), scalar2.getTag());
                }
                assertEquals(scalar1.getValue(), scalar2.getValue());
            }
        }
    }

    public void testParser() throws IOException {
        File[] canonicalFiles = getStreamsByExtension(".canonical", false);
        assertTrue("No test files found.", canonicalFiles.length > 0);
        File[] files = getStreamsByExtension(".data", true);
        assertTrue("No test files found.", files.length > 0);
        int index = 0;
        for (File file : files) {
            try {
                List<Event> events1 = parse(new FileInputStream(file));
                assertFalse(events1.isEmpty());
                File canonical = canonicalFiles[index++];
                List<Event> events2 = canonicalParse(new FileInputStream(canonical));
                assertFalse(events2.isEmpty());
                compareEvents(events1, events2, false);
            } catch (Exception e) {
                System.out.println("Failed File: " + file);
                // fail("Failed File: " + file + "; " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

}
