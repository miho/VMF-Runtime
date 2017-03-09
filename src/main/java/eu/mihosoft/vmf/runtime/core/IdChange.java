package eu.mihosoft.vmf.runtime.core;

/**
 * Created by miho on 09.03.17.
 */
public interface IdChange extends Change{
    long oldId();
    long newId();
}
