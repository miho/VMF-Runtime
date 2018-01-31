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

import eu.mihosoft.vcollections.VList;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Iterator that iterates over the specified object graph.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@SuppressWarnings("deprecation")
public class VIterator implements Iterator<VObject> {

    private final VMFIterator iterator;

    private VIterator(
            VMFIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public VObject next() {
        VObject result = iterator.next();

        // exit last element
        if(!iterator.hasNext()) {
            iterator.onExit(result);
        }

        return result;
    }

    /**
     * Returns a stream backed by this iterator.
     *
     * @return a stream backed by this iterator
     */
    public Stream<VObject> asStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        iterator,
                        Spliterator.ORDERED),
                false);
    }

    /**
     * Returns an iterator that iterates over the specified object graph using
     * the {@link VIterator.IterationStrategy#UNIQUE_NODE} iteration strategy.
     *
     * @param root object graph to iterate
     * @return an iterator that iterates over the specified object graph
     */
    public static VIterator of(VObject root) {
        return new VIterator(
                new VMFIterator(
                        (eu.mihosoft.vmf.runtime.core.internal.VObjectInternal) root, null, IterationStrategy.UNIQUE_NODE)
        );
    }

    /**
     * Returns an iterator that iterates over the specified object graph using
     * the specified iteration strategy
     *
     * @param root object graph to iterate
     * @param iterationStrategy iteration strategy
     * @return an iterator that iterates over the specified object graph
     */
    public static VIterator of(VObject root, IterationStrategy iterationStrategy) {
        return new VIterator(
                new VMFIterator(
                        (eu.mihosoft.vmf.runtime.core.internal.VObjectInternal) root, null, iterationStrategy)
        );
    }

    /**
     * Returns an iterator that iterates over the specified object graph using
     * the specified iteration strategy
     *
     * @param root object graph to iterate
     * @param tl traversal listener
     * @param iterationStrategy iteration strategy
     * @return an iterator that iterates over the specified object graph
     */
    public static VIterator of(VObject root, TraversalListener tl, IterationStrategy iterationStrategy) {
        return new VIterator(
                new VMFIterator(
                        (eu.mihosoft.vmf.runtime.core.internal.VObjectInternal) root, tl, iterationStrategy)
        );
    }

    /**
     * Iteration strategy.
     */
    public enum IterationStrategy {
        /**
         * Visits each node exactly once. References of the same node that are
         * encountered are not visited.
         */
        UNIQUE_NODE,
        /**
         * Visits each property of each node exactly once. References of the
         * same node might be visited multiple times.
         */
        UNIQUE_PROPERTY,
        /**
         * Visits the containment tree. References are completely ignored.
         */
        CONTAINMENT_TREE
    }
}

/**
 * Iterator that iterates over the specified model object graph (depth-first).
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@SuppressWarnings("deprecation")
class VMFIterator
        implements
        Iterator<eu.mihosoft.vmf.runtime.core.internal.VObjectInternal> {

    private final IdentityHashMap<Object, Object> identityMap
            = new IdentityHashMap<>();

    private eu.mihosoft.vmf.runtime.core.internal.VObjectInternal first;
    private VObjectIterator currentIterator;

    private final Stack<VObjectIterator> iteratorStack = new Stack<>();

    private final TraversalListener traversalListener;

    private final VIterator.IterationStrategy strategy;

    private static boolean DEBUG;

    static boolean isDebug() {
        return DEBUG;
    }

    public VMFIterator(
            eu.mihosoft.vmf.runtime.core.internal.VObjectInternal root,
            TraversalListener tl,
            VIterator.IterationStrategy strategy) {
        first = root;
        traversalListener = tl;
        currentIterator = new VMFPropertyIterator(identityMap, root, strategy);
        this.strategy = strategy;
    }

    @Override
    public boolean hasNext() {

        if (first != null) {
            return true;
        }

        return getCurrentIterator().hasNext();
    }

    @Override
    public eu.mihosoft.vmf.runtime.core.internal.VObjectInternal next() {
        eu.mihosoft.vmf.runtime.core.internal.VObjectInternal n;

        // visit first/root element if not visited already
        if (first != null) {
            n = first;
            if (!(first instanceof Immutable)) {
                identityMap.put(n, null);
            }
            first = null;

            onEnter(n);

        } else {
            // obtain next element from current iterator
            n = (eu.mihosoft.vmf.runtime.core.internal.VObjectInternal) getCurrentIterator().next();

            // - if element was not visited before then mark it as visited
            // - push the current iterator and create a new visitor that
            //   walks over all properties of the current element. if we are
            //   done, we will continue with the current iterator and the
            //   elements after current element (if present)
            //
            Object nIdentityObj = unwrapIfReadOnlyInstanceForIdentityCheck(n);

            // visit properties of n if not already visited
            if (!identityMap.containsKey(nIdentityObj)) {
                if (!(nIdentityObj instanceof Immutable)) {
                    identityMap.put(nIdentityObj, null);
                }
                iteratorStack.push(currentIterator);
                onEnter(n);
                currentIterator = new VMFPropertyIterator(
                        identityMap,
                        (eu.mihosoft.vmf.runtime.core.internal.VObjectInternal) n, strategy);
            }
        }

        return n;
    }

    /**
     * Unwraps the mutable instance if a read-only instance has been specified.
     * <b>Note:</b> this method is not intended to weaken the write protection
     * of read-only instances. Its sole purpose is to return an instance that
     * can be used for identity equality checks. Since identity is not
     * guarantied for read-only instances, this method has to be used to unwrap
     * read-only instances before they are added to identity hashmaps and the
     * like.
     *
     * @param o object to unwrap
     * @return object that can be used for identity comparison
     */
    static Object unwrapIfReadOnlyInstanceForIdentityCheck(Object o) {

        // nothing to do:
        // is an immutable type and doesn't have to be unwrapped since it is never
        // added to the identity map
        if (o instanceof Immutable) {
            return o;
        }

        // nothing to do:
        // can't be a read-only instance and is definitely no model type instance
        if (!(o instanceof eu.mihosoft.vmf.runtime.core.internal.VObjectInternal)) {
            return o;
        }

        eu.mihosoft.vmf.runtime.core.internal.VObjectInternal n
                = (eu.mihosoft.vmf.runtime.core.internal.VObjectInternal) o;

        // - Read-only instances have to be unwrapped since identity
        //   is not guarantied for read-only instances
        Object nIdentityObj;
        if (n._vmf_isReadOnly()) {
            nIdentityObj = n._vmf_getMutableObject();
        } else {
            nIdentityObj = n;
        }
        return nIdentityObj;
    }

    @Override
    public void remove() {
        Iterator.super.remove();
    }

    @SuppressWarnings("unchecked")
    private VObjectIterator getCurrentIterator() {

        if (currentIterator == null || !currentIterator.hasNext()) {
            if (isDebug()) {
                System.out.println(" --> leaving current + " + currentIterator.object());
            }

            onExit(currentIterator.object());

            if (!iteratorStack.empty()) {
                // obtain the last iterator that has been pushed
                currentIterator = iteratorStack.pop();
            } else {
                currentIterator = VObjectIterator.EMTPY_ITERATOR;
            }

            // if the current iterator does not have next elements and we
            // still have iterators on the stack then get the next iterator
            // from the stack
            if (!currentIterator.hasNext() && !iteratorStack.isEmpty()) {
                currentIterator = getCurrentIterator();
            }
        }
        return currentIterator;
    }

    void onEnter(VObject o) {
        if(traversalListener==null || (traversalListener.isIgnoreNullObjects() && o == null) ) return;

        traversalListener.onEnter(o);
    }

    void onExit(VObject o) {
        if(traversalListener==null || (traversalListener.isIgnoreNullObjects() && o == null) ) return;

        traversalListener.onExit(o);
    }

}

/**
 * Iterates over the properties of a model object. Empty properties, non model
 * properties (external types) and empty lists are skipped. If properties with
 * non empty lists are visited, the list elements are visited first before the
 * next property is continued.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@SuppressWarnings("deprecation")
class VMFPropertyIterator implements VObjectIterator {

    // element's properties are visited
    private final eu.mihosoft.vmf.runtime.core.internal.VObjectInternal object;

    // element index on current layer
    private int index = -1;

    // list iterator that is used for properties that consists of lists
    private Iterator<VObject> listIterator;

    // identity map that contains already visited elements
    private final IdentityHashMap<Object, Object> identityMap;

    private final VIterator.IterationStrategy strategy;

    /**
     * Creates a new iterator
     *
     * @param identityMap identity map that marks already visited elements
     * @param object model object to visit
     */
    public VMFPropertyIterator(IdentityHashMap<Object, Object> identityMap,
            eu.mihosoft.vmf.runtime.core.internal.VObjectInternal object, 
            VIterator.IterationStrategy strategy) {
        this.identityMap = identityMap;
        this.object = object;
        this.strategy = strategy;

        if (VMFIterator.isDebug()) {
            int numProps
                    = object._vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes().length;
            System.out.println(">> prop iterator for " + object.getClass());
            for (int i = 0; i < numProps; i++) {
                int propIndex
                        = object._vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes()[i];
                System.out.println("  --> i: " + i + ", name: "
                        + object._vmf_getPropertyNames()[propIndex]);
            }

            if (numProps == 0) {
                System.out.println("  --> no props");
            }
        }
    }

    @Override
    public boolean hasNext() {

        if (VMFIterator.isDebug()) {
            System.out.println(" --> checking " + index);
        }

        boolean hasNext;

        // if there's a list iterator then we get elements from the list
        // before visiting next property elements
        if (listIterator != null) {
            hasNext = listIterator.hasNext();

            if (hasNext) {
                return true;
            } else {
                // we are done with the current list iterator
                // skip to next
                listIterator = null;
                // index++;
                if (VMFIterator.isDebug()) {
                    System.out.println(
                            "  --> leaving list, next is " + (index + 1));
                }
            }
        }
        
        // property indices (with or without pure references)
        int[] properties;
        if(strategy == VIterator.IterationStrategy.CONTAINMENT_TREE) {
            properties = object._vmf_getChildrenIndices();
        } else {
            properties = object.
                _vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes();
        }

        // number of properties that are not external types
        int numProperties = properties.length;

        hasNext = index + 1 < numProperties;

        // look-ahead and check whether next element is an empty list, a
        // null element or an already visited element
        if (hasNext) {

            // fetch next property element without increasing the current
            // iterator index
            int nextIndex = index + 1;
            int propIndex = properties[nextIndex];
            Object o = object._vmf_getPropertyValueById(propIndex);

            // skip forward until no null element is present
            if (o == null && index + 2 < numProperties) {

                // skip
                index++;
                hasNext = hasNext();

            } else if (o == null) {
                return false;
            }

            // skip forward until no empty list is present
            if (o instanceof VList) {

                Predicate<Object> hasNextFilter;

                if (strategy == VIterator.IterationStrategy.UNIQUE_NODE) {
                    hasNextFilter = e -> !identityMap.containsKey(
                            VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(e));
                } else {
                    // we don't prevent multiple visits
                    hasNextFilter = e -> true;
                }

                @SuppressWarnings("unchecked")
                boolean hasNonEmpty = ((VList<Object>) o).
                        stream().filter(e -> e != null).
                        filter(hasNextFilter).
                        count() > 0;

                if (!hasNonEmpty && index + 2 < numProperties) {

                    // skip
                    index++;
                    hasNext = hasNext();

                    // we have seen the future
                    // that's why we are allowed to early exit
                    return hasNext;
                }

                // we have a next element if there elements in the list that
                // are not null and not already visited
                hasNext = hasNonEmpty;
            }

            if (strategy == VIterator.IterationStrategy.UNIQUE_NODE) {
                // skip forward until no already visited element is present
                boolean alreadyVisited = identityMap.containsKey(
                        VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(o)
                );
                if (alreadyVisited && index + 2 < numProperties) {

                    // skip
                    index++;
                    hasNext = hasNext();

                } else if (alreadyVisited) {
                    hasNext = false;
                }
            }
        }

        return hasNext;
    }

    @Override
    @SuppressWarnings("unchecked")
    public VObject next() {

        // if list iterator is present, we return the list elements
        if (listIterator != null) {
            if (VMFIterator.isDebug()) {
                System.out.println("  --> using list at " + index);
            }
            return listIterator.next();
        }

        index++;

        if (VMFIterator.isDebug()) {
            System.out.println("  --> returning " + index);
        }
        
         // property indices (with or without pure references)
        int[] properties;
        if(strategy == VIterator.IterationStrategy.CONTAINMENT_TREE) {
            properties = object._vmf_getChildrenIndices();
        } else {
            properties = object.
                _vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes();
        }

        int propIndex = properties[index];
        Object o = object._vmf_getPropertyValueById(propIndex);

        // skip forward until no null element is present
        if (o == null) {
            o = next();
        }

        Predicate<Object> hasNextFilter;

        if (strategy == VIterator.IterationStrategy.UNIQUE_NODE) {
            hasNextFilter = e -> !identityMap.containsKey(
                    VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(e));
        } else {
            // we don't prevent multiple visits
            hasNextFilter = e -> true;
        }

        // iterating through list
        if (o instanceof VList) {
            listIterator = ((VList<VObject>) o).stream().filter(e -> e != null).
                    filter(hasNextFilter).iterator();
            if (VMFIterator.isDebug()) {
                System.out.println("  --> switching to list at " + index);
            }
            o = listIterator.next();
        }

        if (strategy == VIterator.IterationStrategy.UNIQUE_NODE) {
            // skip already visited
            boolean alreadyVisited = identityMap.
                    containsKey(VMFIterator.
                            unwrapIfReadOnlyInstanceForIdentityCheck(o));
            if (alreadyVisited) {
                o = next();
            }
        }

        return (VObject) o;
    }

    @Override
    public void remove() {
        // TODO remove object from object graph
        VObjectIterator.super.remove();
    }

    @Override
    public VObject object() {
        return object;
    }
}

interface VObjectIterator extends Iterator<VObject>{

    static final VObjectIterator EMTPY_ITERATOR = new VObjectIterator() {
        @Override
        public VObject object() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public VObject next() {
            return null;
        }
    };

    VObject object();
}