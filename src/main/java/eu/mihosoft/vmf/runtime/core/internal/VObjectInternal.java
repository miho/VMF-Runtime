/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 */
package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vmf.runtime.core.ObservableObject;
import eu.mihosoft.vmf.runtime.core.VObject;

import java.util.List;
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

    /**
     * Indicates whether this object is a read-only instance.
     * @return {@code true} if this object is a read-only instance; {@code false} otherwise
     */
    default boolean _vmf_isReadOnly() {
        return false;
    }

    /**
     * Returns the mutable instance wrapped by this instance. If this object is a mutable
     * instance this method is a no-op.
     * @return the mutable instance wrapped by this instance
     */
    default VObject _vmf_getMutableObject() {
        return this;
    }

    /**
     * Returns the id of this instance.
     * @return the id of this instance
     */
    default long _vmf_getId() {
        throw new UnsupportedOperationException("FIXME: unsupported method invoked. This should not happen :(");
    }

    /**
     * Requests a new id.
     * @return a new id
     */
    static long newId() {
        return currentId.getAndIncrement();
    }

    /**
     * The current id.
     */
    static final AtomicLong currentId = new AtomicLong(0);

    /**
     * Finds a unique id among graphs that reference this instance.
     */
    void _vmf_findUniqueId();

    /**
     * @return objects that reference this object
     */
    List<VObject> _vmf_referencedBy();

    /**
     * @return objects that are referenced by this object
     */
    List<VObject> _vmf_references();

    /**
     * Enables unique id generation (default).
     */
    void _vmf_enableUniqueIdUpdate();
    /**
     * Disables unique id generation (only use it for clone/serialization).
     */
    void _vmf_disableUniqueIdUpdate();

    /**
     * Indicates whether unique id update/generation is enabled.
     * @return {@code true} if unique id update/generation is enabled; {@code false} otherwise
     */
    boolean _vmf_isUniqueIdUpdateEnabled();
}
