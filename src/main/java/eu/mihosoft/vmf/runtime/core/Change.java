/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vmf.runtime.core.internal.VObjectInternal;
import eu.mihosoft.vmf.runtime.core.internal.VObjectInternalModifiable;

import java.util.Objects;

/**
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Change {
    VObject object();
    String propertyName();
    Object oldValue();
    Object newValue();
    void undo();
    boolean isUndoable();
}


class ChangeImpl implements Change {
    private final VObject object;
    private final String propertyName;
    private final Object oldValue;
    private final Object newValue;

    ChangeImpl(VObject object, String propertyName, Object oldValue, Object newValue) {
        this.object = object;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
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

        if(!isUndoable()) return;

        VObjectInternalModifiable internal = (VObjectInternalModifiable) object;
        int propId = internal._vmf_getPropertyIdByName(propertyName);
        internal._vmf_setPropertyValueById(propId, oldValue);
    }

    @Override
    public boolean isUndoable() {

        VObjectInternalModifiable internal = (VObjectInternalModifiable) object;
        int propId = internal._vmf_getPropertyIdByName(propertyName);

        if(Objects.equals(newValue, internal._vmf_getPropertyValueById(propId))) {
            return true;
        } else {
            return false;
        }
    }
}


