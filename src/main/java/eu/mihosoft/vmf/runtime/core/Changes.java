/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vcollections.VList;

import vjavax.observer.Subscription;

/**
 * Contains change related functionality of this object graph.
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Changes {

    /**
     * Adds the specified change listener. Listeners will be notified about changes regardless
     * of whether change recording is enabled. This allows to react to and/or undo specific
     * changes without the overhead of storing all previous events in a collection.
     *
     * @param l the listener to add
     * @return a subscription which allows to unsubscribe the specified listener
     */
    Subscription addListener(ChangeListener l);

    /**
     * Starts recording changes. Previously recorded changes will be removed
     * (also removes transactions).
     * @see Changes#stop()
     */
    void start();

    /**
     * Starts a new transaction.
     * @see Transaction
     * @see Changes#publishTransaction()
     */
    void startTransaction();

    /**
     * Publishes a transaction that consists of all changes since the last
     * {@code startTransaction()} or {@code publishTransaction()} call.
     * @see Transaction
     * @see Changes#startTransaction()
     */
    void publishTransaction();

    /**
     * Stops recording changes. Unpublished transactions will be published.
     * @see Changes#start()
     */
    void stop();

    /**
     * Returns all changes to the model (observable collection) that were
     * recorded since the last {@link eu.mihosoft.vmf.runtime.core.Changes#start()
     * } call.
     *
     * @return all changes to the model (observable collection) that were
     * recorded since the last {@link eu.mihosoft.vmf.runtime.core.Changes#start()
     * } call
     */
    VList<Change> all();

    /**
     * Returns all model transactions (observable collection) that were
     * published since the last {@link eu.mihosoft.vmf.runtime.core.Changes#start()
     * } call.
     *
     * @return all model transactions (observable collection)
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
     * @return {@code true} if model versioning is enabled; {@code false}
     * otherwise
     */
    boolean isModelVersioningEnabled();
}
