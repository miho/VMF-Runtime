package eu.mihosoft.vmf.runtime.core;

import java.beans.PropertyChangeListener;

public interface ObservableObject {
    void addPropertyChangeListener(PropertyChangeListener l);
    void removePropertyChangeListener(PropertyChangeListener l);
}
