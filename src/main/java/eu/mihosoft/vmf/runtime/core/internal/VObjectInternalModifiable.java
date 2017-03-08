/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 */
package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vmf.runtime.core.Changes;
import eu.mihosoft.vmf.runtime.core.VObject;

import java.util.Objects;

/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 *
 * Created by miho on 20.02.17.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@Deprecated
public interface VObjectInternalModifiable extends VObjectInternal {

    /**
     * Sets values of properties by id (calls setter methods).
     *
     * @param propertyId id of the property that shall be changed
     * @param value the value to set
     */
    void _vmf_setPropertyValueById(int propertyId, Object value);

    default void setModelToChanges(Changes c) {
        ChangesImpl cImpl = (ChangesImpl) c;
        cImpl.setModel(this);
    }

    default void _vmf_setId(long id) {
        throw new UnsupportedOperationException("FIXME: unsupported method invoked. This should not happen :(");
    }

    @Override
    default void _vmf_findUniqueId() {

        // finding unique id causes overhead
        // therefore, it can be disabled (e.g. in clone())
        if(!_vmf_isUniqueIdUpdateEnabled()) {
            return;
        }

        boolean idIsUnique = true;

        // if we find our current id in the object graphs that reference us we
        // need to start the search for a unique id
        for(VObject vObj : _vmf_referencedBy()) {
            if(vObj.vmf().content().stream().filter(vo->vo!=this).mapToLong(vo->vo.vmf().id()).anyMatch(lId-> lId == _vmf_getId())) {
                idIsUnique = false;
                break;
            }
        }

        // if the current id is not used we stop the search
        if(idIsUnique) return;


        long uniqueId = 0;

        // find the smallest id that is larger than all ids in the graphs that reference us
        for(VObject vObj : _vmf_referencedBy()) {
            long localMax = vObj.vmf().content().stream().mapToLong(vo->vo.vmf().id()).max().orElseGet(()->0) + 1;
            uniqueId = Math.max(localMax, uniqueId);
        }

        // check whether the previously selected id is unique within our own graph
        final long finalUniqueId = uniqueId;
        idIsUnique = vmf().content().stream().mapToLong(vo->vo.vmf().id()).anyMatch(lId-> lId == finalUniqueId);

        // if id is not unique within our own graph we use the maximum id of our own graph +1  and
        // the previously selected id
        if(!idIsUnique) {
            long localMax = vmf().content().stream().filter(vo->vo!=this).mapToLong(vo -> vo.vmf().id()).max().orElseGet(()->0) + 1;
            uniqueId = Math.max(localMax, uniqueId);
        }

        // finally, set the unique id
        _vmf_setId(uniqueId);
    }
}
