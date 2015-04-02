package org.drools.core.phreak;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.spi.PropagationContext;

import java.util.BitSet;

public interface PropagationEntry {

    public void propgate(InternalWorkingMemory wm);

    public static class Insert implements PropagationEntry {
        private final ObjectTypeNode[] otns;
        private final InternalFactHandle handle;
        private final PropagationContext context;

        public Insert(ObjectTypeNode[] otns, InternalFactHandle handle, PropagationContext context) {
            this.otns = otns;
            this.handle = handle;
            this.context = context;
        }

        public void propgate(InternalWorkingMemory wm) {
            for (int i = 0, length = otns.length; i < length; i++) {
                otns[i].propagateAssert(handle, context, wm);
            }
        }

        @Override
        public String toString() {
            return "Insertion of " + handle.getObject();
        }
    }

    public static class Update implements PropagationEntry {
        private final EntryPointNode epn;
        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final ObjectTypeConf objectTypeConf;
        private final BitSet vetoMask;

        public Update(EntryPointNode epn, InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf) {
            this.epn = epn;
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;
            if (handle.isTraiting()) {
                BitSet originalMask = ((TraitProxy) handle.getObject()).getTypeFilter();
                if (originalMask != null) {
                    this.vetoMask = new BitSet();
                    this.vetoMask.or(originalMask);
                } else {
                    this.vetoMask = null;
                }
            } else {
                this.vetoMask = null;
            }
        }

        public void propgate(InternalWorkingMemory wm) {
            if (vetoMask != null) {
                ((TraitProxy) handle.getObject()).setTypeFilter(vetoMask);
            }
            epn.propagateModify(handle, context, objectTypeConf, wm);
            if (vetoMask != null) {
                ((TraitProxy) handle.getObject()).setTypeFilter(null);
            }
        }
    }

    public static class Delete implements PropagationEntry {
        private final EntryPointNode epn;
        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final ObjectTypeConf objectTypeConf;

        public Delete(EntryPointNode epn, InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf) {
            this.epn = epn;
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;
        }

        public void propgate(InternalWorkingMemory wm) {
            epn.propagateRetract(handle, context, objectTypeConf, wm);
        }
    }
}
