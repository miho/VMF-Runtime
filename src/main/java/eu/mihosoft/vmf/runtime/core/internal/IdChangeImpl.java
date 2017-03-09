package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vcollections.VListChangeEvent;
import eu.mihosoft.vmf.runtime.core.Change;
import eu.mihosoft.vmf.runtime.core.IdChange;
import eu.mihosoft.vmf.runtime.core.PropertyChange;
import eu.mihosoft.vmf.runtime.core.VObject;

import java.util.Optional;

/**
 * Created by miho on 09.03.17.
 */
@Deprecated
public class IdChangeImpl implements IdChange{
    private final VObject object;

    private final long oldId;
    private final long newId;
    private final long timestamp;

    public IdChangeImpl(VObject object, long oldId, long newId) {
        this.object = object;
        this.oldId = oldId;
        this.newId = newId;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public long oldId() {
        return oldId;
    }

    @Override
    public long newId() {
        return newId;
    }

    @Override
    public VObject object() {
        return object;
    }

    @Override
    public String propertyName() {
        return "_vmf_id";
    }

    @Override
    public void undo() {
        //
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public Optional<PropertyChange> propertyChange() {
        return Optional.empty();
    }

    @Override
    public Optional<VListChangeEvent<Object>> listChange() {
        return Optional.empty();
    }

    @Override
    public Optional<IdChange> idChange() {
        return Optional.of(this);
    }

    @Override
    public ChangeType getType() {
        return ChangeType.ID;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
