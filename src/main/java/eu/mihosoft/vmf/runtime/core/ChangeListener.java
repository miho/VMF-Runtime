package eu.mihosoft.vmf.runtime.core;

/**
 * Created by miho on 23.02.17.
 */
@FunctionalInterface
public interface ChangeListener {
    void onChange(Change change);
}
