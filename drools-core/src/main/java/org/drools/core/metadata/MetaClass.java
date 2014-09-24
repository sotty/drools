package org.drools.core.metadata;

public interface MetaClass<T> {

    public MetaProperty<T,?>[] getProperties();

    public int getPropertyIndex( MetaProperty propertyLiteral );
}
