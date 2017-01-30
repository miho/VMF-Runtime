package eu.mihosoft.vmf.runtime.core;

public interface VObject extends ObservableObject {
    //static T newInstance();
    
    /**
     * Returns the VMF related functionality.
     * 
     * @return VMF related functionality
     */
    VMF vmf();
    
    /**
     * Returns a deep clone of this object.
     * @return a deep clone of this object
     */
    VObject clone();
    
    /**
     * Returns a read-only wrapper of this object.
     * @return a read-only wrapper of this object
     */
    VObject asReadOnly();
}
