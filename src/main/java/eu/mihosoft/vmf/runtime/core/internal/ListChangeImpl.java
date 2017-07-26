/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 */
package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vcollections.VListChange;
import eu.mihosoft.vcollections.VListChangeEvent;
import eu.mihosoft.vmf.runtime.core.Change;
import eu.mihosoft.vmf.runtime.core.PropertyChange;
import eu.mihosoft.vmf.runtime.core.VObject;

import vjavax.observer.collection.CollectionChangeEvent;
import java.util.Optional;

/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 *
 * Created by miho on 21.02.2017.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@Deprecated
class ListChangeImpl implements Change {
    private final VObject object;
    private final String propertyName;
    private final CollectionChangeEvent<Object,VList<Object>, VListChange<Object>> evt;
    private final VList list;
    private final long timestamp;

    @SuppressWarnings({"deprecation", "unchecked"})
    public ListChangeImpl(VObject object, String propertyName, CollectionChangeEvent<Object,VList<Object>, VListChange<Object>> evt) {
        this.object = object;
        this.propertyName = propertyName;
        this.evt = evt;

        VObjectInternalModifiable internal = (VObjectInternalModifiable) object;
        int propId = internal._vmf_getPropertyIdByName(propertyName);
        list = (VList) internal._vmf_getPropertyValueById(propId);

        this.timestamp = System.nanoTime();
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
//            System.out.println("SET");
            if (evt.removed().indices().length == list.size()) {
                list.setAll(evt.removed().indices()[0], evt.removed().elements());
            }
        } else if (evt.wasAdded()) {
//            System.out.println("ADD: " + evt.added().elements().get(0));
            list.removeAll(evt.added().indices());
//            System.out.println("ADD-DONE");
        } else if(evt.wasRemoved()) {
//            System.out.println("REM" + evt.added().elements().get(0));
            list.addAll(evt.removed().indices(),evt.removed().elements());
//            System.out.println("REM-DONE");
        }

    }

    @Override
    public boolean isUndoable() {
        if (evt.wasSet()) {
            for(int index : evt.removed().indices()) {
                if(index > evt.removed().indices().length) {
                    return false;
                }
            }
        } else if (evt.wasAdded()) {
            // TODO check size changes
            for(int index : evt.added().indices()) {
                if (index > evt.added().indices().length) {
                    return false;
                }
            }
        } else if(evt.wasRemoved()) {
            // TODO check size changes
            for(int index : evt.removed().indices()) {
                if(index > evt.removed().indices().length) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Optional<PropertyChange> propertyChange() {
        return Optional.empty();
    }

    @Override
    public Optional<VListChangeEvent<Object>> listChange() {
        VListChangeEvent<Object> result;
        if (evt.wasSet()) {
            result =  VListChangeEvent.getSetEvent(evt.source(), evt.added().indices(), evt.removed().elements(), evt.added().elements());
        } else if(evt.wasAdded()) {
            result =   VListChangeEvent.getAddedEvent(evt.source(), evt.added().indices(), evt.added().elements());
        } else if (evt.wasRemoved()) {
            result =   VListChangeEvent.getRemovedEvent(evt.source(), evt.removed().indices(), evt.removed().elements());
        } else {
            result = null;
        }

        return Optional.ofNullable(result);
    }

    @Override
    public ChangeType getType() {
        return ChangeType.LIST;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
