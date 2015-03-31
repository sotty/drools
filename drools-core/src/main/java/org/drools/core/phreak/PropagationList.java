package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;

import java.util.LinkedList;

public class PropagationList {

    private final LinkedList<PropagationEntry> list = new LinkedList<PropagationEntry>();

    public synchronized void addEntry(PropagationEntry propagationEntry) {
        list.add(propagationEntry);
    }

    public synchronized void flush(InternalWorkingMemory workingMemory) {
        while (!list.isEmpty()) {
            list.removeFirst().propgate(workingMemory);
        }
    }
}
