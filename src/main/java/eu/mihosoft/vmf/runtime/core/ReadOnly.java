package eu.mihosoft.vmf.runtime.core;

/**
 * Denotes a read-only type.
 *
 * Created by miho on 23.02.17.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
public interface ReadOnly {

    default VObject asModifiable() {
        throw new UnsupportedOperationException("FIXME: unsupported method invoked. This should not happen :(");
    }

}
