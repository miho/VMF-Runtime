package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vcollections.VListChange;
import eu.mihosoft.vmf.runtime.core.Change;
import eu.mihosoft.vmf.runtime.core.VObject;

import javax.observer.collection.CollectionChangeEvent;

/**
 * Created by miho on 21.02.2017.
 */
@Deprecated
class ListChangeImpl implements Change {
    private final VObject object;
    private final String propertyName;
    private final CollectionChangeEvent<Object,VList<Object>, VListChange<Object>> evt;
    private final VList list;

    @SuppressWarnings({"deprecation", "unchecked"})
    public ListChangeImpl(VObject object, String propertyName, CollectionChangeEvent<Object,VList<Object>, VListChange<Object>> evt) {
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
    @SuppressWarnings("unchecked")
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
