package org.drools.core.metadata;

import org.kie.api.definition.type.Position;

import java.io.Serializable;

public abstract class PropertyLiteral<T, R> implements MetaProperty<T,R>, Serializable {

    private final int index;
    private final String name;

    private final String key;

    public PropertyLiteral( int index, String name ) {
        this( index, name, name );
    }

    public PropertyLiteral( int index, String name, String key ) {
        this.index = index;
        this.name = name;
        this.key = key != null ? key : name;
    }

    public abstract R get( T o );

    public abstract void set( T o, R value );

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        PropertyLiteral that = (PropertyLiteral) o;

        if ( !key.equals( that.key ) ) return false;

        return true;
    }

    @Override
    public int compareTo( MetaProperty<T, R> o ) {
        return this.getName().compareTo( o.getName() );
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

}
