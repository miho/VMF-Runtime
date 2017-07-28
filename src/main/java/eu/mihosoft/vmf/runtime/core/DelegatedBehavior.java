package eu.mihosoft.vmf.runtime.core;

/**
 * Delegation interface for invoking custom implementations. VMF objects do not support
 * custom extensions by manipulating their implementation code. However, custom behavior can be realized be defining
 * delegation classes that implement this interface.
 *
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
