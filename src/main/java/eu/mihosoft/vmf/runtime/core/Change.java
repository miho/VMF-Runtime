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

import vjavax.observer.collection.CollectionChangeEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A model change.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Change {

    /**
     * Returns the object affected by this change
     * @return the object affected by this change
     */
    VObject object();

    /**
     * Returns the name of the property affected by this change.
     * @return the name of the property affected by this change
     */
    String propertyName();

    /**
     * Performs an undo operation (if possible).
     */
    void undo();

    /**
     * Indicates whether this change can be reverted.
     * @return {@code true} if this change can be reverted; {@code false} otherwise
     */
    boolean isUndoable();

    /**
     * Returns the property change (optional) which exists if this change affects a single property.
     * @return the property change (optional)
     */
    default Optional<PropertyChange> propertyChange() {
        return Optional.empty();
    }

    /**
     * Returns the list change (optional) which exists if this change affects a list (list elements added, removed, etc.).
     * @return the list change (optional)
     */
    default Optional<VListChangeEvent<Object>> listChange() {
        return Optional.empty();
    }

    /**
     * Change Type
     */
    enum ChangeType {
        /**
         * Change affects a single property.
         */
        PROPERTY,
        /**
         * Change affects a list.
         */
        LIST
    }

    /**
     * Returns the type of this change.
     * @return the type of this change
     */
    ChangeType getType();

    /**
     * Returns the timestamp which denotes the creation of this change.
     * @return the timestamp which denotes the creation of this change
     */
    long getTimestamp();

}




