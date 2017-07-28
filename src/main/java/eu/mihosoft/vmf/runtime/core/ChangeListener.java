package eu.mihosoft.vmf.runtime.core;

/**
 * A model change listener.
 *
 * Created by miho on 23.02.17.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@FunctionalInterface
public interface ChangeListener {

    /**
     * Called whenever the object graph changes.
     *
     * @param change change event
     */
    void onChange(Change change);
}
