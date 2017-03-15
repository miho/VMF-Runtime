package eu.mihosoft.vmf.runtime.core;

/**
 * Created by miho on 23.02.17.
 */
public interface ReadOnly {

    default VObject asModifiable() {
        throw new UnsupportedOperationException("FIXME: unsupported method invoked. This should not happen :(");
    }

}
