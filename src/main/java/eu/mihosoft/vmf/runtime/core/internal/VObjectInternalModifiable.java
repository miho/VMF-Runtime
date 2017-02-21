package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vmf.runtime.core.Changes;

/**
 * Created by miho on 20.02.17.
 */
@Deprecated
public interface VObjectInternalModifiable extends VObjectInternal {

    /**
     * Sets values of properties by id (calls setter methods).
     */
    void _vmf_setPropertyValueById(int propertyId, Object value);


    default void setModelToChanges(Changes c) {
        ChangesImpl cImpl = (ChangesImpl) c;
        cImpl.setModel(this);
    }
}
