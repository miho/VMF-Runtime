/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vcollections.VList;
import eu.mihosoft.vcollections.VListChange;
import eu.mihosoft.vcollections.VListChangeEvent;
import eu.mihosoft.vmf.runtime.core.internal.VObjectInternal;
import eu.mihosoft.vmf.runtime.core.internal.VObjectInternalModifiable;

import javax.observer.collection.CollectionChangeEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Change {

    VObject object();

    String propertyName();

    void undo();
    boolean isUndoable();

    Optional<PropertyChange> propertyChange();
    Optional<VListChangeEvent<Object>> listChange();

    enum ChangeType {
        PROPERTY,
        LIST
    }

    ChangeType getType();

}




