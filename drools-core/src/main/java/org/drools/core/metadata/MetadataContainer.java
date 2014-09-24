package org.drools.core.metadata;

import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.ClassUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class MetadataContainer<T extends Metadatable> implements Serializable {

	protected transient T target;

	public MetadataContainer( T metadatableObject ) {
		this.target = metadatableObject;
	}

    protected MetaClass metaClassInfo;

    public MetaClass<T> getMetaClassInfo() {
        return metaClassInfo;
    }

    protected T getTarget() {
        return target;
    }

    public void setTarget( T target ) {
        this.target = target;
    }

    public List<MetaProperty<T,?>> properties() {
        return Collections.unmodifiableList( Arrays.asList( getMetaClassInfo().getProperties() ) );
    }

    public String[] propertyNames() {
        String[] names = new String[ metaClassInfo.getProperties().length ];
        for ( int j = 0; j < metaClassInfo.getProperties().length; j++ ) {
            names[ j ] = metaClassInfo.getProperties()[ j ].getName();
        }
        return names;
    }

    protected <T,R> MetaProperty<T,R> getProperty( String name ) {
        for ( MetaProperty p : metaClassInfo.getProperties() ) {
            if ( p.getName().equals( name ) ) {
                return p;
            }
        }
        return null;
    }

    protected <T,R> MetaProperty getProperty( int index ) {
        return metaClassInfo.getProperties()[ index ];
    }


    public static abstract class ModifyLiteral<T extends Metadatable> implements Modify<T> {
        private T target;
        private ModifyTask<T,?> task;
        private long modificationMask;

        protected abstract MetaClass<T> getMetaClassInfo();

        public ModifyLiteral( T target ) {
            this.target = target;
        }

        public T getTarget() {
            return target;
        }

        public T call() {
            modificationMask = task.getModificationMask();
            task.call( target );
            return target;
        }

        public long getModificationMask() {
            return modificationMask;
        }

        public abstract Class getModificationClass();

        protected <R> void addTask( MetaProperty<? extends Metadatable,R> p, R val ) {
            ModifyTask<T,R> newTask = new ModifyTask<T, R>( p, val );
            if ( task == null ) {
                task = newTask;
            } else {
                ModifyTask<T,?> lastTask = task;
                while ( task.nextTask != null ) {
                    lastTask = task.nextTask;
                }
                lastTask.nextTask = newTask;
            }
        }

        protected class ModifyTask<T extends Metadatable,R> {
            public MetaProperty<T,R> propertyLiteral;
            public R value;
            public ModifyTask<T,?> nextTask;

            protected ModifyTask( MetaProperty<? extends Metadatable,R> p, R val ) {
                propertyLiteral = (MetaProperty<T, R>) p;
                value = val;
            }

            public void call( T target ) {
                propertyLiteral.set( target, value );
                if ( nextTask != null ) {
                    nextTask.call( target );
                }
            }

            public long getModificationMask() {
                long downstreamMask = nextTask != null ? nextTask.getModificationMask() : 0;
                return BitMaskUtil.set( downstreamMask, getMetaClassInfo().getPropertyIndex( propertyLiteral ) );
            }
        }
    }

    public abstract static class ClassLiteral<T> implements MetaClass<T> {

        protected MetaProperty<T,?>[] properties;
        protected List<String> propertyNames;

        public ClassLiteral( MetaProperty<T,?>[] propertyLiterals ) {
            this.properties = propertyLiterals;
            cachePropertyNames();
        }

        protected abstract void cachePropertyNames();

        @Override
        public MetaProperty<T, ?>[] getProperties() {
            return properties;
        }

        public int getPropertyIndex( MetaProperty prop ) {
            return propertyNames.indexOf( prop.getName() );
        }
    }

}
