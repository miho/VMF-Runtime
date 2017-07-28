package eu.mihosoft.vmf.runtime.core;

import java.util.Iterator;

/**
 * Traversal listener for traversing object graphs and performing corresponding actions.
 *
 * Created by miho on 10.03.2017.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface TraversalListener {

    /**
     * Called whenever the traversal algorithm enters an object node.
     *
     * @param o object node
     */
    void onEnter(VObject o);

    /**
     * Called whenever the traversal algorithm exits an object node.
     *
     * @param o object node
     */
    void onExit(VObject o);

    /**
     * Traverses the specified object graph with the default strategy.
     *
     * @param o object graph to be traversed
     * @param tl traversal listener
     * @see VIterator.IterationStrategy
     */
    static void traverse(VObject o, TraversalListener tl) {
        traverse(o,tl, VIterator.IterationStrategy.UNIQUE_NODE);
    }

    /**
     * Traverses the specified object graph.
     *
     * @param o object graph to be traversed
     * @param tl traversal listener
     * @param strategy iteration strategy
     */
    static void traverse(VObject o, TraversalListener tl, VIterator.IterationStrategy strategy) {
        Iterator<VObject> it = VIterator.of(o, tl, strategy);

        while(it.hasNext()) {
            it.next();
        }
    }
}
