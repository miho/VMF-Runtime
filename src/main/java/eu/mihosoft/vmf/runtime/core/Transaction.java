/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vmf.runtime.core;

import java.util.List;

/**
 * A collection of changes. Transactions can be used to describe
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface Transaction {

    List<Change> changes();

    boolean isUndoable();

    void undo();
}

