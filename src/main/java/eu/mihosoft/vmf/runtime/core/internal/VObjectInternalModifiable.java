package eu.mihosoft.vmf.runtime.core.internal;

/**
 * Created by miho on 20.02.17.
 */
@Deprecated
public interface VObjectInternalModifiable extends VObjectInternal {

    /**
     * Sets values of properties by id (calls setter methods).
     */
    void _vmf_setPropertyValueById(int propertyId, Object value);
}
