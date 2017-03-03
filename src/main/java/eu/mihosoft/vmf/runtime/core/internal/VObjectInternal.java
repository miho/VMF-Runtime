/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 */
package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vmf.runtime.core.ObservableObject;
import eu.mihosoft.vmf.runtime.core.VObject;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Internal interface. Don't rely on this API. Seriously, <b>don't</b> rely on it!
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@Deprecated
public interface VObjectInternal extends VObject, ObservableObject {

    /**
     * Returns the type id of this class.
     */
    int _vmf_getTypeId();

    /**
     * Returns the names of the properties defined in this type/object.
     *
     */
    public String[] _vmf_getPropertyNames();

    /**
     * Returns the types of the properties defined in this type/object.
     */
    public int[] _vmf_getPropertyTypes();

    /**
     * Returns values of properties by id (calls getter methods).
     */
    public Object _vmf_getPropertyValueById(int propertyId);

    /**
     * Returns the indices of all properties with model types or collections
     * that contain model instances.
     */
    public int[] _vmf_getIndicesOfPropertiesWithModelTypes();

    /**
     * Returns the indices of all collection properties with model element
     * types.
     */
    public int[] _vmf_getIndicesOfPropertiesWithModelElementTypes();

    /**
     * Returns the indices of all properties with model instances
     * or collections that contain model instances.
     */
    public int[] _vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes();

    /**
     * Returns the indices of all children properties which declare this object as
     * parent container (opposite). This includes collection properties that
     * contain children.
     */
    public int[] _vmf_getChildrenIndices();

    /**
     * Returns the property index of the specified property name.
     * @param propertyName
     * @return the property index of the specified property name or {@code -1}
     *         if the specified property does not exist
     */
    public int _vmf_getPropertyIdByName(String propertyName);
    
    
    default boolean _vmf_isReadOnly() {
        return false;
    }

    default VObject _vmf_getMutableObject() {
        return this;
    }

    default long _vmf_getId() {
        throw new UnsupportedOperationException("FIXME: unsupported method invoked. This should not happen :(");
    }

    static long newId() {
        return currentId.getAndIncrement();
    }

    static final AtomicLong currentId = new AtomicLong(0);


}
