/*
 * Copyright 2016-2017 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * If you use this software for scientific research then please cite the following publication(s):
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */
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
     * Returns a stream that contains all elements of the object graph (depth first) that implement/extend the
     * specified type. It maps all elements to the specified type, i.e. returns {@code Stream<T>}.
     * @param type type for filtering and mapping
     * @param <T> element type
     * @return a stream of type <T>, i.e. {@code Stream<T>}
     */
    <T extends VObject> Stream<T> stream(Class<T> type);
    
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

