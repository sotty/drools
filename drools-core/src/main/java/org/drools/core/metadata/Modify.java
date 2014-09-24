package org.drools.core.metadata;

import org.drools.core.metadata.Metadatable;

public interface Modify<T extends Metadatable> {

    public T call();

    public T getTarget();

    public long getModificationMask();

    public Class getModificationClass();
}
