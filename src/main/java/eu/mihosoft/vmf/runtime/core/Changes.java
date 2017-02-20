/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vcollections.VMappedList;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Changes {

    /**
     * Starts recording changes.
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
     * @return all changes to the model (observable collection)
     */
    VList<Change> all();

    /**
     * Returns all model transactions (observable collection).
     * @return ll model transactions (observable collection)
     */
    VList<Transaction> transactions();
}

class ChangesImpl implements Changes {

    private final VList<Change> all = VList.newInstance(new ArrayList<>());
    private final VList<Change> unmodifiableAll = 
            VMappedList.newUnmodifiableInstance(all, (e)->e);
    private final VList<Transaction> transactions
            = VList.newInstance(new ArrayList<>());
    private final VList<Transaction> unmodifiableTransactions
            = VList.newInstance(Collections.unmodifiableList(transactions));
    private int currentTransactionStartIndex = 0;

    @Override
    public void start() {
        // TODO register with model
    }

    @Override
    public void startTransaction() {
        //TODO update currentTransactionStartIndex = ...
    }

    @Override
    public void publishTransaction() {
        if (currentTransactionStartIndex < unmodifiableAll.size()) {
            transactions.add(() -> unmodifiableAll.subList(
                    currentTransactionStartIndex, all.size()));
            currentTransactionStartIndex = unmodifiableAll.size();
        }
    }

    @Override
    public void stop() {
        if (currentTransactionStartIndex < all.size()) {
            publishTransaction();
        }
        // TODO unregister from model
    }

    @Override
    public VList<Change> all() {
        return unmodifiableAll;
    }

    @Override
    public VList<Transaction> transactions() {
        return unmodifiableTransactions;
    }

}
