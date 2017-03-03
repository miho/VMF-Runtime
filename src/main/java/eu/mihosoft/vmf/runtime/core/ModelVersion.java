package eu.mihosoft.vmf.runtime.core;

/**
 * Model version.
 *
 * Created by miho on 03.03.17.
 */
public interface ModelVersion {

    /**
     * Returns the timestamp which denotes the creation of this model version.
     * @return the timestamp which denotes the creation of this model version.
     */
    long timestamp();

    /**
     * Returns the model version number. While the version number is a great way to identify changes during the
     * lifetime of a model  instance, it cannot be used to compare previously serialized instances of the model with the
     * current instantiation. Model versions are <b>NOT</b> unique among different model instances.
     *
     * @return the model version number
     */
    long versionNumber();
}

