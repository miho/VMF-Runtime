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

        if(!_vmf_isUniqueIdUpdateEnabled()) {
            return;
        }

        boolean idIsUnique = true;

        for(VObject vObj : _vmf_referencedBy()) {
            if(vObj.vmf().content().stream().filter(vo->vo!=this).mapToLong(vo->vo.vmf().id()).anyMatch(lId-> lId == _vmf_getId())) {
                idIsUnique = false;
                break;
            }
        }

        if(idIsUnique) return;

        System.out.println("unique-id: num-referenced="+_vmf_referencedBy().size());

        long uniqueId = 0;

        for(VObject vObj : _vmf_referencedBy()) {
            long localMax = vObj.vmf().content().stream().mapToLong(vo->vo.vmf().id()).max().orElseGet(()->0) + 1;
            System.out.println("local-max: " + localMax);
            uniqueId = Math.max(localMax, uniqueId);
        }

        final long finalUniqueId = uniqueId;
        idIsUnique = vmf().content().stream().mapToLong(vo->vo.vmf().id()).anyMatch(lId-> lId == finalUniqueId);

        if(!idIsUnique) {
            long localMax = vmf().content().stream().filter(vo->vo!=this).mapToLong(vo -> vo.vmf().id()).max().orElseGet(()->0) + 1;
            uniqueId = Math.max(localMax, uniqueId);
        }

        _vmf_setId(uniqueId);
    }
}
