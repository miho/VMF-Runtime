package eu.mihosoft.vmf.runtime.core;

/**
 * Describes a property change.
 *
 * Created by miho on 23.02.17.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface PropertyChange {
    Object oldValue();
    Object newValue();
}
