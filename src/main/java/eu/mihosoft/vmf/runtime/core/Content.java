/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Content of this object graph.
 * 
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Content {

    /**
     * Returns an iterator that traverses the object graph (depth first)
     * using the {@link VIterator.IterationStrategy#UNIQUE_PROPERTY} iteration strategy.
     * @return an iterator that traverses the object graph
     */
    Iterator<VObject> iterator();

    /**
     * Returns an iterator that traverses the object graph (depth first)
     * using the specified iteration strategy.
     * @param strategy iteration strategy
     * @return an iterator that traverses the object graph
     */
    Iterator<VObject> iterator(VIterator.IterationStrategy strategy);

    /**
     * Returns a stream that contains all elements of the object graph (depth first)
     * using the {@link VIterator.IterationStrategy#UNIQUE_PROPERTY} iteration strategy.
     * @return a stream that contains all elements of the object graph
     */
    Stream<VObject> stream();

    /**
     * Returns a stream that contains all elements of the object graph (depth first)
     * using the specified iteration strategy.
     * @param strategy iteration strategy
     * @return a stream that contains all elements of the object graph
     */
    Stream<VObject> stream(VIterator.IterationStrategy strategy);
    
    /**
     * Returns a deep copy of this object.
     * @return a deep copy of this object
     */
    <T> T deepCopy();
    
    /**
     * Returns a shallow copy of this object.
     * @return a shallow copy of this object
     */
    <T> T shallowCopy();
    
    
}

