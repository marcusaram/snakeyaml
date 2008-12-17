/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.events;

import java.nio.charset.Charset;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class StreamStartEvent extends Event {
    private Charset encoding;

    public StreamStartEvent(Mark startMark, Mark endMark) {
        super(startMark, endMark);
        this.encoding = Charset.forName("UTF-8");
    }

    public Charset getEncoding() {
        return encoding;
    }
}
