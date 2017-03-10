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

    /**
     * TODO implement uniqueness guarantees.
     *
     * Returns the id of this object that has been assigned by VMF. The id can be used to identify and query objects
     * within an object graph.
     *
     * <p>
     *     <b>Note:</b> the uniqueness of this id is only guaranteed within an object graph. For objects that are
     *     referenced from multiple object graphs, VMF will assign an id which is unique among those graphs. Cloned
     *     and serialized graphs duplicate the ids of the clone source. However, cloned and deserialized graphs update
     *     their id if they are added to another graph to prevent id collisions.
     * </p>
     *
     * @return the id of this object that has been assigned by VMF
     */
    default String id() {
        throw new UnsupportedOperationException("FIXME: unsupported method invoked. This should not happen :(");
    }
    
    
}
