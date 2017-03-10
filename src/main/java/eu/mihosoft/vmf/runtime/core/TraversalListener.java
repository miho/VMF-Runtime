package eu.mihosoft.vmf.runtime.core;

import java.util.Iterator;

/**
 * Created by miho on 10.03.2017.
 */
public interface TraversalListener {
    void onEnter(VObject o);
    void onExit(VObject o);

    static void traverse(VObject o, TraversalListener tl) {
        traverse(o,tl, VIterator.IterationStrategy.UNIQUE_NODE);
    }

    static void traverse(VObject o, TraversalListener tl, VIterator.IterationStrategy strategy) {
        Iterator<VObject> it = VIterator.of(o, tl, strategy);

        while(it.hasNext()) {
            it.next();
        }
    }
}
