/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vcollections.VMappedList;
import eu.mihosoft.vmf.runtime.core.internal.VObjectInternalModifiable;

import javax.observer.Subscription;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Changes {

    /**
     * Adds the specified change listener.
     * @param l the listener to add
     * @return a subscription which allows to unsubscribe the specified listener
     */
    Subscription addListener(ChangeListener l);

    /**
     * Starts recording changes. Previously recorded changes will be removed (also removes transactions).
     */
    void start();

    /**
     * Starts a new transaction.
     */
    void startTransaction();

    /**
     * Publishes a transaction that consists of all changes since the last
     * {@code startTransaction()} or {@code publishTransaction()} call.
     */
    void publishTransaction();

    /**
     * Stops recording changes. Unpublished trnsactions will be published.
     */
    void stop();

    /**
     * Returns all changes to the model (observable collection).
     *
     * @return all changes to the model (observable collection)
     */
    VList<Change> all();

    /**
     * Returns all model transactions (observable collection).
     *
     * @return ll model transactions (observable collection)
     */
    VList<Transaction> transactions();


    /**
     * Removes all recorded changes (also removes transactions).
     */
    void clear();
}

