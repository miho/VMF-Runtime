/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vcollections.VList;

import javax.observer.Subscription;

/**
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Changes {

    /**
     * Adds the specified change listener.
     *
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
    void startCommand();

    /**
     * Publishes a transaction that consists of all changes since the last
     * {@code startCommand()} or {@code publishTransaction()} call.
     */
    void publishTransaction();

    /**
     * Stops recording changes. Unpublished transactions will be published.
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

    /**
     * Returns the model version.
     *
     * @return model version
     */
    ModelVersion modelVersion();

    /**
     * Indicates whether model versioning is enabled.
     *
     * @return {@code true} if model versioning is enabled; {@code false} otherwise
     */
    boolean isModelVersioningEnabled();
}

