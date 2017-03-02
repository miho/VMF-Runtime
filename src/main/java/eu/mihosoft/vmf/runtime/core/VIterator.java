/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vcollections.VList;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Iterator that an iterates over the specified object graph.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@SuppressWarnings("deprecation")
public class VIterator implements Iterator<VObject> {

    private final Iterator<eu.mihosoft.vmf.runtime.core.internal.VObjectInternal> iterator;

    private VIterator(
            Iterator<eu.mihosoft.vmf.runtime.core.internal.VObjectInternal> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public VObject next() {
        return iterator.next();
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
     * Returns an iterator that iterates over the specified object graph using the
     * {@link VIterator.IterationStrategy#UNIQUE_NODE} iteration strategy.
     *
     * @param root object graph to iterate
     * @return an iterator that iterates over the specified object graph
     */
    public static VIterator of(VObject root) {
        return new VIterator(
                new VMFIterator(
                        (eu.mihosoft.vmf.runtime.core.internal.VObjectInternal) root, IterationStrategy.UNIQUE_NODE)
        );
    }

    /**
     * Returns an iterator that iterates over the specified object graph using the specified iteration strategy
     *
     * @param root object graph to iterate
     * @param iterationStrategy iteration strategy
     * @return an iterator that iterates over the specified object graph
     */
    public static VIterator of(VObject root, IterationStrategy iterationStrategy) {
        return new VIterator(
                new VMFIterator(
                        (eu.mihosoft.vmf.runtime.core.internal.VObjectInternal) root, iterationStrategy)
        );
    }

    /**
     * Iteration strategy.
     */
    public enum IterationStrategy {
        /**
         * Visits each node exactly once. References of the same node that are encountered are not visited.
         */
        UNIQUE_NODE,
        /**
         * Visits each edge exactly once. References of the same node might be visited multiple times.
         */
//        UNIQUE_EDGE
    }
}

/**
 * Iterator that iterates over the specified model object graph (depth-object).
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@SuppressWarnings("deprecation")
class VMFIterator
        implements
        Iterator<eu.mihosoft.vmf.runtime.core.internal.VObjectInternal> {

    private final Map<IdentityEquals, Object> identityMap
            = new HashMap<>();

    private eu.mihosoft.vmf.runtime.core.internal.VObjectInternal first;
    private Iterator<VObject> currentIterator;
    private VObject lastVisited;

    private final Stack<Iterator> iteratorStack = new Stack<>();
    private final VIterator.IterationStrategy iterationStrategy;

    private static boolean DEBUG;

    static boolean isDebug() {
        return DEBUG;
    }

    public VMFIterator(
            eu.mihosoft.vmf.runtime.core.internal.VObjectInternal root, VIterator.IterationStrategy iterationStrategy) {
        first = root;
        currentIterator = new VMFPropertyIterator(identityMap, root, lastVisited, iterationStrategy);
        this.iterationStrategy = iterationStrategy;
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

        // visit object/root element if not visited already
        if (first != null) {
            n = first;
            identityMap.put(IdentityEquals.newInstance(lastVisited, n, iterationStrategy), null);
            first = null;
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

            IdentityEquals edge = IdentityEquals.newInstance(unwrapIfReadOnlyInstanceForIdentityCheck(lastVisited), nIdentityObj, iterationStrategy);

            if (!identityMap.containsKey(edge)) {
                identityMap.put(edge, null);
                iteratorStack.push(currentIterator);
                currentIterator = new VMFPropertyIterator(
                        identityMap,
                        (eu.mihosoft.vmf.runtime.core.internal.VObjectInternal) n, lastVisited, iterationStrategy);
            }
        }

        lastVisited = n;

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

        if(o == null) return null;

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
    private Iterator<VObject> getCurrentIterator() {

        if (currentIterator == null || !currentIterator.hasNext()) {
            if (isDebug()) {
                System.out.println(" --> leaving current");
            }

            if(!iteratorStack.empty()) {
                // obtain the last iterator that has been pushed
                currentIterator = iteratorStack.pop();
            } else {
                currentIterator = Collections.emptyIterator();
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

}

/**
 * Iterates over the properties of a model object. Empty properties, non model
 * properties (external types) and empty lists are skipped. If properties with
 * non empty lists are visited, the list elements are visited object before the
 * next property is continued.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@SuppressWarnings("deprecation")
class VMFPropertyIterator implements Iterator<VObject> {

    // element's properties are visited
    private final eu.mihosoft.vmf.runtime.core.internal.VObjectInternal object;

    // element index on current layer
    private int index = -1;

    // list iterator that is used for properties that consists of lists
    private Iterator<VObject> listIterator;

    // identity map that contains already visited elements
    private final Map<IdentityEquals, Object> identityMap;

    private VObject lastVisited;

    private VIterator.IterationStrategy iterationStrategy;

    /**
     * Creates a new iterator
     *
     * @param identityMap identity map that marks already visited elements
     * @param object model object to visit
     */
    public VMFPropertyIterator(Map<IdentityEquals, Object> identityMap,
                               eu.mihosoft.vmf.runtime.core.internal.VObjectInternal object,
                               VObject lastVisited, VIterator.IterationStrategy iterationStrategy) {
        this.identityMap = identityMap;
        this.object = object;
        this.lastVisited = lastVisited;
        this.iterationStrategy = iterationStrategy;

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

        // number of properties that are not external types
        int numProperties = object.
                _vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes().length;

        hasNext = index + 1 < numProperties;

        // look-ahead and check whether next element is an empty list, a
        // null element or an already visited element
        if (hasNext) {

            // fetch next property element without increasing the current
            // iterator index
            int nextIndex = index + 1;
            int propIndex = object.
                    _vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes()[nextIndex];
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
                @SuppressWarnings("unchecked")
                boolean hasNonEmpty = ((VList) o).stream().filter(e -> e != null).
                        filter(e -> !identityMap.containsKey(IdentityEquals.newInstance(
                                VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(lastVisited),
                                VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(e),
                                iterationStrategy
                        ))
                        ).
                        count() > 0;

                if (!hasNonEmpty && index + 2 < numProperties) {

                    // skip
                    index++;
                    hasNext = hasNext();

                    // we have seen the future
                    // that's why we are allowed to exit early
                    return hasNext;
                }

                // we have a next element if there elements in the list that
                // are not null and not already visited
                hasNext = hasNonEmpty;
            }

            // skip forward until no already visited element is present
            boolean alreadyVisited = identityMap.containsKey(IdentityEquals.newInstance(
                    VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(lastVisited),
                    VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(o),
                    iterationStrategy
            ));
            if (alreadyVisited && index + 2 < numProperties) {

                // skip
                index++;
                hasNext = hasNext();

            } else if (alreadyVisited) {
                hasNext = false;
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

        int propIndex = object.
                _vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes()[index];
        Object o = object._vmf_getPropertyValueById(propIndex);

        // skip forward until no null element is present
        if (o == null) {
            o = next();
        }

        // iterating through list
        if (o instanceof VList) {
            listIterator = ((VList) o).stream().filter(e -> e != null).
                    filter(e
                            -> !identityMap.containsKey(IdentityEquals.newInstance(
                            VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(lastVisited),
                            VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(e),
                            iterationStrategy
                    ))).
                    iterator();
            if (VMFIterator.isDebug()) {
                System.out.println("  --> switching to list at " + index);
            }
            o = listIterator.next();
        }

        // skip already visited
        boolean alreadyVisited = identityMap.
                containsKey(IdentityEquals.newInstance(
                        VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(lastVisited),
                        VMFIterator.unwrapIfReadOnlyInstanceForIdentityCheck(o),
                        iterationStrategy
                ));
        if (alreadyVisited) {
            o = next();
        }

        return (VObject) o;
    }

    @Override
    public void remove() {
        // TODO remove object from object graph
        Iterator.super.remove();
    }
}

/**
 * The purpose of this class is to store a pair (directed graph edge) of objects used
 * for equals(). This class's equals() method checks equality by object identity. Same
 * for hashCode() which uses identity hashes of 'first' and 'second' to compute the
 * hash.
 *
 * This class can be used in conjunction with a regular HashMap to get similar results
 * to an IdentityHashMap, except that in this case identity pairs can be used. And we
 * don't have to use a map implementation that is deliberately broken by design.
 */
class EqualsPairEdge implements IdentityEquals{

    private final Object first;
    private final Object second;

    EqualsPairEdge(Object first, Object second) {
        this.first = first;
        this.second = second;

//        System.out.println("----");
//        System.out.println(" -> first:  " + first);
//        System.out.println(" -> second: " + second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(System.identityHashCode(first),
                System.identityHashCode(second));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EqualsPairEdge other = (EqualsPairEdge) obj;
        if (this.first!=other.first) {
            return false;
        }
        if (this.second!=other.second) {
            return false;
        }
        return true;
    }
}

/**
 * The purpose of this class is to store an object used for equals(). This class's
 * equals() method checks equality by object identity. Same for hashCode() which
 * uses the identity hash of 'object' to compute the hash.
 *
 * This class can be used in conjunction with a regular HashMap to get similar results
 * to an IdentityHashMap, except that in this case identity pairs can be used. And we
 * don't have to use a map implementation that is deliberately broken by design.
 */
class EqualsSingle implements IdentityEquals{
    private final Object object;

    EqualsSingle(Object object) {
        this.object = object;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(object);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EqualsSingle other = (EqualsSingle) obj;
        if (this.object !=other.object) {
            return false;
        }

        return true;
    }
}

interface IdentityEquals {

    /**
     * Creates a new identity object for equals comparison.
     *
     * @param first first edge node (ignored by equals single)
     * @param second second edge node (used by equals single)
     * @param strategy iteration strategy
     * @return identity object
     */
    static IdentityEquals newInstance(Object first, Object second, VIterator.IterationStrategy strategy) {
        switch(strategy) {
//            case UNIQUE_EDGE:
//                return new EqualsPairEdge(first,second);
            case UNIQUE_NODE:
                return new EqualsSingle(second);
            default:
                throw new IllegalArgumentException("Specified iteration type '" + strategy + "' is not supported");
        }
    }

}
