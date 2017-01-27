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
     * Returns an iterator that traverses the object graph (depth first).
     * @return an iterator that traverses the object graph
     */
    Iterator<VObject> iterator();

    /**
     * Returns a stream that contains all elements of thie object graph (depth first).
     * @return a stream that contains all elements of thie object graph
     */
    Stream<VObject> stream();
}

