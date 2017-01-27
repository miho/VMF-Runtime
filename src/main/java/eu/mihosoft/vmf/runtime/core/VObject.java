package eu.mihosoft.vmf.runtime.core;

public interface VObject extends ObservableObject {
    // static T newInstance();
    
    /**
     * Returns the VMF related functionality.
     * 
     * @return VMF related functionality
     */
    VMF vmf();
}
