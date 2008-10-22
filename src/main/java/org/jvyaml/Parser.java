/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.Iterator;

import org.jvyaml.events.Event;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public interface Parser {
    boolean checkEvent(final Class[] choices);
    Event peekEvent();
    Event getEvent();
    Iterator eachEvent();
    Iterator iterator();
    void parseStream();
    Event parseStreamNext();
}// Parser
