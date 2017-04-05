package eu.mihosoft.vmf.runtime.core;

/**
 * Created by miho on 05.04.17.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface DelegatedBehavior<T extends VObject> {

    /**
     * Sets the caller that delegates to this class. This method is called by VMF directly
     * after initializing this object.
     * @param caller the caller that delegates to this class
     */
    public default void setCaller(T caller) {}
}
