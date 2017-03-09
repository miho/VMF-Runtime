/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 */
package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vcollections.VListChangeEvent;
import eu.mihosoft.vmf.runtime.core.Change;
import eu.mihosoft.vmf.runtime.core.PropertyChange;
import eu.mihosoft.vmf.runtime.core.VObject;

import java.util.Objects;
import java.util.Optional;

/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 *
 * Created by miho on 21.02.2017.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@Deprecated
class PropChangeImpl implements Change, PropertyChange {

    private final VObject object;
    private final String propertyName;
    private final Object oldValue;
    private final Object newValue;
    private final long timestamp;

    PropChangeImpl(VObject object, String propertyName, Object oldValue, Object newValue) {
        this.object = object;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;

        this.timestamp = System.currentTimeMillis();
    }

    public VObject object() {
        return object;
    }

    public String propertyName() {
        return propertyName;
    }

    public Object oldValue() {
        return oldValue;
    }

    public Object newValue() {
        return newValue;
    }

    @SuppressWarnings("deprecation")
    public void undo() {
        if (!isUndoable()) return;

        VObjectInternalModifiable internal = (VObjectInternalModifiable) object;
        int propId = internal._vmf_getPropertyIdByName(propertyName);

        internal._vmf_setPropertyValueById(propId, oldValue);
    }

    @Override
    @SuppressWarnings({"deprecation", "unchecked"})
    public boolean isUndoable() {

        VObjectInternalModifiable internal = (VObjectInternalModifiable) object;
        int propId = internal._vmf_getPropertyIdByName(propertyName);

        return Objects.equals(newValue, internal._vmf_getPropertyValueById(propId));
    }

    @Override
    public Optional<PropertyChange> propertyChange() {
        return Optional.of(this);
    }

    @Override
    public Optional<VListChangeEvent<Object>> listChange() {
        return Optional.empty();
    }

    @Override
    public ChangeType getType() {
        return ChangeType.PROPERTY;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
