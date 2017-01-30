/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vmf.runtime.core.VObject;

/**
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
/**
 * Internal interface. Don't rely on this API.
 */
@Deprecated
public interface VObjectInternal extends VObject {

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
     * Returns the indices of all collection properties with model element types
     * or collections that contain model instances.
     */
    public int[] _vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes();
    
    
    default boolean _vmf_isReadOnly() {
        return false;
    }

}