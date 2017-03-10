/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vmf.runtime.core.internal.ChangesImpl;

/**
 * VMF API. Gives access to VMF related functionality.
 * 
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface VMF {
    
    /**
     * Returns the content of this object.
     * @return the content of this object
     */
    default Content content() {
        throw new UnsupportedOperationException("FIXME: unsupported method invoked. This should not happen :(");
    }
    
    /**
     * Returns the changes applied to this object.
     * @return the changes applied to this object
     */
    default Changes changes() {
        throw new UnsupportedOperationException("FIXME: unsupported method invoked. This should not happen :(");
    }
    
}
