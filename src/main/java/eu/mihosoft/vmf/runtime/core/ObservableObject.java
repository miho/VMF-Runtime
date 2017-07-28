package eu.mihosoft.vmf.runtime.core;

import java.beans.PropertyChangeListener;

/**
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface ObservableObject {
    void addPropertyChangeListener(PropertyChangeListener l);
    void removePropertyChangeListener(PropertyChangeListener l);
}
