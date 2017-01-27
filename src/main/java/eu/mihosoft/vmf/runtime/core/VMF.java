/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

/**
 * VMF API. Gives as access to VMF related functionality.
 * 
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface VMF {
    
    /**
     * Returns the content of this object.
     * @return the content of this object
     */
    Content content();
    
    /**
     * 
     */
    Changes changes();
}
