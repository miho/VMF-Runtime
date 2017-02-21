/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vcollections.VListChangeEvent;
import eu.mihosoft.vmf.runtime.core.internal.VObjectInternal;
import eu.mihosoft.vmf.runtime.core.internal.VObjectInternalModifiable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Change {
    VObject object();

    String propertyName();

    void undo();

    boolean isUndoable();
}

enum ChangeType {
    PROPERTY,
    LIST
}

class ListChangeImpl implements Change {
    private final VObject object;
    private final String propertyName;
    private final VListChangeEvent evt;
    private final VList list;

    public ListChangeImpl(VObject object, String propertyName, VListChangeEvent evt) {
        this.object = object;
        this.propertyName = propertyName;
        this.evt = evt;

        VObjectInternalModifiable internal = (VObjectInternalModifiable) object;
        int propId = internal._vmf_getPropertyIdByName(propertyName);
        list = (VList) internal._vmf_getPropertyValueById(propId);
    }

    @Override
    public VObject object() {
        return object;
    }

    @Override
    public String propertyName() {
        return propertyName;
    }

    @Override
    public void undo() {

        if (!isUndoable()) return;

        if (evt.wasSet()) {
            if (evt.removed().indices().length == list.size()) {
                list.setAll(evt.removed().indices()[0], evt.removed().elements());
            }
        } else if (evt.wasAdded()) {
            list.removeAll(evt.added().indices());
        } else if(evt.wasRemoved()) {
            list.addAll(evt.removed().indices(),evt.removed().elements());
        }

    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}

class PropChangeImpl implements Change {

    private final VObject object;
    private final String propertyName;
    private final Object oldValue;
    private final Object newValue;

    PropChangeImpl(VObject object, String propertyName, Object oldValue, Object newValue) {
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

        if (!isUndoable()) return;

        VObjectInternalModifiable internal = (VObjectInternalModifiable) object;
        int propId = internal._vmf_getPropertyIdByName(propertyName);
        internal._vmf_setPropertyValueById(propId, oldValue);
    }

    @Override
    public boolean isUndoable() {

        VObjectInternalModifiable internal = (VObjectInternalModifiable) object;
        int propId = internal._vmf_getPropertyIdByName(propertyName);

        if (Objects.equals(newValue, internal._vmf_getPropertyValueById(propId))) {
            return true;
        } else {
            return false;
        }
    }
}


