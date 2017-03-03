/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 */
package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vmf.runtime.core.Changes;

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
}
