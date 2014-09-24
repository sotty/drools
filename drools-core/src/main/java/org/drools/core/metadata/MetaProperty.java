package org.drools.core.metadata;

public interface MetaProperty<T,R> extends Comparable<MetaProperty<T,R>> {

    public int getIndex();

    public String getName();

    public String getKey();

    public R get( T object );

    public void set( T o, R value );
}
