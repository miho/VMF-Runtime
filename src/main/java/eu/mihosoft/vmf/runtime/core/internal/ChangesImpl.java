package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vcollections.VMappedList;
import eu.mihosoft.vmf.runtime.core.*;
import eu.mihosoft.vmf.runtime.core.internal.VObjectInternalModifiable;

import javax.observer.Subscription;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Deprecated
public class ChangesImpl implements Changes {

    private final VList<Change> all = VList.newInstance(new ArrayList<>());
    private final VList<Change> unmodifiableAll =
            VMappedList.newInstance(all, (e) -> e,
                    (e)->{throw new UnsupportedOperationException("List modification not supported!");});
    private final VList<Transaction> transactions
            = VList.newInstance(new ArrayList<>());
    private final VList<Transaction> unmodifiableTransactions
            = VMappedList.newInstance(transactions, (e) -> e,
            (e)->{throw new UnsupportedOperationException("List modification not supported!");});
    private int currentTransactionStartIndex = 0;

    private VObject model;

    private final List<Subscription> subscriptions = new ArrayList<>();

    public void setModel(VObject model) {
        this.model = model;
    }

    @Override
    public void start() {
        Iterator<VObject> it = model.vmf().content().iterator();

        PropertyChangeListener objListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Change c = new PropChangeImpl((VObject) evt.getSource(), evt.getPropertyName(),
                        evt.getOldValue(), evt.getNewValue());
                all.add(c);

                if(evt.getNewValue() instanceof VObject) {
                    VObject newObjectToObserve = (VObject) evt.getNewValue();
                    newObjectToObserve.removePropertyChangeListener(this);
                    newObjectToObserve.addPropertyChangeListener(this);
                    subscriptions.add(() -> newObjectToObserve.removePropertyChangeListener(this));
                }

                if(evt.getOldValue() instanceof VObject) {
                    VObject objectToRemoveFromObservation = (VObject) evt.getOldValue();
                    objectToRemoveFromObservation.removePropertyChangeListener(this);
                }
            }
        };

        while (it.hasNext()) {
            VObject obj = it.next();

            addListListenersToPropertiesOf(obj, objListener);

            obj.addPropertyChangeListener(objListener);
            subscriptions.add(() -> obj.removePropertyChangeListener(objListener));
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

                subscriptions.add(list.addChangeListener(
                        (evt) -> {
                            Change c = new ListChangeImpl(object, propName, evt);
                            all.add(c);

                            evt.added().elements().stream().filter(e->e instanceof VObject).
                                    map(e->(VObject)e).forEach(v->
                            {
                                v.removePropertyChangeListener(objListener);
                                v.addPropertyChangeListener(objListener);
                                subscriptions.add(() -> v.removePropertyChangeListener(objListener));
                            });
                            evt.removed().elements().stream().filter(e->e instanceof VObject).
                                    map(e->(VObject)e).forEach(v->v.removePropertyChangeListener(objListener));
                        }
                ));
            }
        }
    }

    @Override
    public void startTransaction() {
        currentTransactionStartIndex = all.size();
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
        subscriptions.forEach(s->s.unsubscribe());
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

}