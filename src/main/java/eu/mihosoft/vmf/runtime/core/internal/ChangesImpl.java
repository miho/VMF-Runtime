package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vcollections.VMappedList;
import eu.mihosoft.vmf.runtime.core.*;
import eu.mihosoft.vmf.runtime.core.internal.VObjectInternalModifiable;

import javax.observer.Subscription;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

@Deprecated
public class ChangesImpl implements Changes {

    private final VList<Change> all = VList.newInstance(new ArrayList<>());
    private final VList<Change> unmodifiableAll =
            VMappedList.newInstance(all, (e) -> e,
                    (e) -> {
                        throw new UnsupportedOperationException("List modification not supported!");
                    });
    private final VList<Transaction> transactions
            = VList.newInstance(new ArrayList<>());
    private final VList<Transaction> unmodifiableTransactions
            = VMappedList.newInstance(transactions, (e) -> e,
            (e) -> {
                throw new UnsupportedOperationException("List modification not supported!");
            });
    private int currentTransactionStartIndex = 0;

    private VObject model;

    private final List<Subscription> subscriptions = new ArrayList<>();
    private final Map<Object, Subscription> listSubscriptions = new IdentityHashMap<>();

    public void setModel(VObject model) {
        this.model = model;
    }

    @Override
    public void start() {

        clear();

        PropertyChangeListener objListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Change c = new PropChangeImpl((VObject) evt.getSource(), evt.getPropertyName(),
                        evt.getOldValue(), evt.getNewValue());
                all.add(c);

                if (evt.getNewValue() instanceof VObject) {
                    VObject newObjectToObserve = (VObject) evt.getNewValue();
                    registerChangeListener(newObjectToObserve, this);
                }

                if (evt.getOldValue() instanceof VObject) {
                    VObject objectToRemoveFromObservation = (VObject) evt.getOldValue();
                    unregisterChangeListener(objectToRemoveFromObservation, this);
                }
            }
        };

        registerChangeListener(model, objListener);
    }

    private void registerChangeListener(VObject vObj, PropertyChangeListener objListener) {
        Iterator<VObject> it = vObj.vmf().content().iterator();
        while (it.hasNext()) {

            VObject obj = it.next();

            addListListenersToPropertiesOf(obj, objListener);

            obj.removePropertyChangeListener(objListener);
            obj.addPropertyChangeListener(objListener);

            subscriptions.add(() -> obj.removePropertyChangeListener(objListener));
        }
    }

    private void unregisterChangeListener(VObject vObj, PropertyChangeListener objListener) {
        Iterator<VObject> it = vObj.vmf().content().iterator();
        while (it.hasNext()) {

            VObject obj = it.next();

            removeListListenersFromPropertiesOf(obj, objListener);
            obj.removePropertyChangeListener(objListener);
        }
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    private void addListListenersToPropertiesOf(VObject object, PropertyChangeListener objListener) {
        VObjectInternalModifiable internalModel = (VObjectInternalModifiable) object;
        for (int i = 0; i < internalModel._vmf_getPropertyTypes().length; i++) {
            int type = internalModel._vmf_getPropertyTypes()[i];
            if (type == -2) {
                String propName = internalModel._vmf_getPropertyNames()[i];

                VList<Object> list = (VList<Object>) internalModel._vmf_getPropertyValueById(i);

                Subscription subscription = list.addChangeListener(
                        (evt) -> {
                            Change c = new ListChangeImpl(object, propName, evt);
                            all.add(c);

                            evt.added().elements().stream().filter(
                                    e -> e instanceof VObject).
                                    map(e -> (VObject) e).forEach(v ->
                            {
                                v.removePropertyChangeListener(objListener);
                                registerChangeListener(v, objListener);
                                subscriptions.add(
                                        () -> v.removePropertyChangeListener(objListener));
                            });
                            evt.removed().elements().stream().
                                    filter(e -> e instanceof VObject).
                                    map(e -> (VObject) e).
                                    forEach(v -> unregisterChangeListener(v, objListener));
                        }
                );

                subscriptions.add(subscription);
                listSubscriptions.put(list, subscription);
            }
        }
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    private void removeListListenersFromPropertiesOf(VObject object, PropertyChangeListener objListener) {
        VObjectInternalModifiable internalModel = (VObjectInternalModifiable) object;
        for (int i = 0; i < internalModel._vmf_getPropertyTypes().length; i++) {
            int type = internalModel._vmf_getPropertyTypes()[i];
            if (type == -2) {
                String propName = internalModel._vmf_getPropertyNames()[i];

                VList<Object> list = (VList<Object>) internalModel._vmf_getPropertyValueById(i);

                if (listSubscriptions.containsKey(list)) {
                    listSubscriptions.get(list).unsubscribe();
                }
            }
        }
    }

    @Override
    public void startTransaction() {
        currentTransactionStartIndex = all.size();
    }

    static class TransactionImpl implements Transaction {
        private final List<Change> changes;

        public TransactionImpl(List<Change> changes) {
            this.changes = changes;
        }


        @Override
        public List<Change> changes() {
            return this.changes;
        }

        @Override
        public boolean isUndoable() {

            for(Change c : changes) {
                if(!c.isUndoable()) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void undo() {
            for(int i = changes.size()-1; i > -1;i--) {
                changes.get(i).undo();
            }
        }
    }

    @Override
    public void publishTransaction() {
        if (currentTransactionStartIndex < unmodifiableAll.size()) {
            transactions.add(new TransactionImpl(
                    unmodifiableAll.subList(
                        currentTransactionStartIndex, all.size()
                    ))
            );
            currentTransactionStartIndex = unmodifiableAll.size();
        }
    }

    @Override
    public void stop() {
        if (currentTransactionStartIndex < all.size()) {
            publishTransaction();
        }
        subscriptions.forEach(s -> s.unsubscribe());
        subscriptions.clear();
    }

    @Override
    public VList<Change> all() {
        return unmodifiableAll;
    }

    @Override
    public VList<Transaction> transactions() {
        return unmodifiableTransactions;
    }

    @Override
    public void clear() {
        all.clear();
        transactions.clear();
    }

}