package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.jvyaml.DefaultYAMLConfig;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ScalarEvent;

public class EmitterTest extends TestCase {

    public void testEmit() throws IOException {
        Writer writer = new StringWriter();
        Emitter emitter = new EmitterImpl(writer, new DefaultYAMLConfig());
        //
        emitter.emit(new DocumentStartEvent(null, null, false, new Integer[] { new Integer(1),
                new Integer(1) }, null));
        Event event = new ScalarEvent(null, "tag:yaml.org,2002:str", new boolean[] { true, true },
                "abc", null, null, (char) 0);
        emitter.emit(event);
        emitter.emit(new DocumentEndEvent(null, null, false));
        writer.close();
        System.out.println(writer.toString());
    }

}
