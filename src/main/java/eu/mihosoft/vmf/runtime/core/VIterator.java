/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vmf.runtime.core.internal.VObjectInternal;
import eu.mihosoft.vcollections.VList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * Iterator that an iterates over the specified object graph.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@SuppressWarnings("deprecation")
public class VIterator implements Iterator<VObject> {

    private final Iterator<VObjectInternal> iterator;
    private final boolean asReadOnly;

    private VIterator(Iterator<VObjectInternal> iterator, boolean asReadOnly) {
        this.iterator = iterator;
        this.asReadOnly = asReadOnly;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public VObject next() {
        if (asReadOnly) {
            VObject result = iterator.next();
            if (result != null) {
                result = result.asReadOnly();
            }
            return result;
        } else {
            return iterator.next();
        }
    }

    /**
     * Returns an iterator that iterates over the specified object graph.
     *
     * @param root object graph to iterate
     * @return an iterator that iterates over the specified object graph
     */
    public static VIterator of(VObject root) {
        return new VIterator(new VMFIterator((VObjectInternal) root), false);
    }

    /**
     * Returns a read-only iterator that iterates over the specified object
     * graph.
     *
     * @param root object graph to iterate
     * @return a read-only iterator that iterates over the specified object
     * graph
     */
    public static VIterator readOnlyOf(VObject root) {
        return new VIterator(new VMFIterator((VObjectInternal) root), true);
    }
}

/**
 * Iterator that iterates over the specified model object graph (depth-first).
 *
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@SuppressWarnings("deprecation")
class VMFIterator implements Iterator<VObjectInternal> {

    private final IdentityHashMap<Object, Object> identityMap
            = new IdentityHashMap<>();

    private VObjectInternal first;
    private Iterator<VObject> currentIterator;

    private final Stack<Iterator> iteratorStack = new Stack<>();

    public VMFIterator(VObjectInternal root) {
        first = root;
        currentIterator = new VMFPropertyIterator(identityMap, root);
    }

    @Override
    public boolean hasNext() {

        if (first != null) {
            return true;
        }

        return getCurrentIterator().hasNext();
    }

    @Override
    public VObjectInternal next() {
        Object n;

        // visit first/root element if not visited already
        if (first != null) {
            n = first;
            identityMap.put(n, null);
            first = null;
        } else {
            // obtain next element from current iterator
            n = getCurrentIterator().next();

            // - if element was not visited before then mark it as visited
            // - push the current iterator and create a new visitor that
            //   walks over all properties of the current element. if we are
            //   done, we will continue with the current iterator and the
            //   elements after current element (if present)
            if (!identityMap.containsKey(n)) {
                identityMap.put(n, null);
                iteratorStack.push(currentIterator);
                currentIterator = new VMFPropertyIterator(
                        identityMap, (VObjectInternal) n);
            }

        }

        return (VObjectInternal) n;
    }

    @Override
    public void remove() {
        Iterator.super.remove();
    }

    private Iterator<VObject> getCurrentIterator() {

        if (currentIterator == null || !currentIterator.hasNext()) {
            // obtain the last iterator that has been pushed
            currentIterator = iteratorStack.pop();

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
 * non empty lists are visited, the list elements are visited first before the
 * next property is continued.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@SuppressWarnings("deprecation")
class VMFPropertyIterator implements Iterator<VObject> {

    // element's properties are visited
    private final VObjectInternal object;

    // element index on current layer
    private int index = -1;

    // list iterator that is used for properties that consists of lists
    private Iterator<VObject> listIterator;

    // identity map that contains already visited elements
    private final IdentityHashMap<Object, Object> identityMap;

    /**
     * Creates a new iterator
     *
     * @param identityMap identity map that marks already visited elements
     * @param object model object to visit
     */
    public VMFPropertyIterator(IdentityHashMap<Object, Object> identityMap, VObjectInternal object) {
        this.identityMap = identityMap;
        this.object = object;
    }

    @Override
    public boolean hasNext() {

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
                index++;
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

                boolean hasNonEmpty = ((VList) o).stream().filter(e -> e != null).
                        filter(e -> !identityMap.containsKey(e)).count() > 0;

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

            // skip forward until no already visited element is present
            boolean alreadyVisited = identityMap.containsKey(o);
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
    public VObject next() {

        // if list iterator is present, we return the list elements
        if (listIterator != null) {
            return listIterator.next();
        }

        index++;

        int propIndex = object._vmf_getIndicesOfPropertiesWithModelTypeOrElementTypes()[index];
        Object o = object._vmf_getPropertyValueById(propIndex);

        // skip forward until no null element is present
        if (o == null) {
            o = next();
        }

        // iterating through list
        if (o instanceof VList) {
            listIterator = ((VList) o).stream().filter(e -> e != null).
                    filter(e -> !identityMap.containsKey(e)).iterator();
            o = listIterator.next();
        }

        // skip already visited
        boolean alreadyVisited = identityMap.containsKey(o);
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
