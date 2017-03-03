package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vmf.runtime.core.ModelVersion;

/**
 * Created by miho on 03.03.17.
 */
class ModelVersionImpl implements ModelVersion {
    private final long timestamp;
    private final long modelVersion;

    public ModelVersionImpl(long timestamp, long modelVersion) {
        this.timestamp = timestamp;
        this.modelVersion = modelVersion;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    @Override
    public long versionNumber() {
        return modelVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelVersionImpl that = (ModelVersionImpl) o;

        if (timestamp != that.timestamp) return false;
        return modelVersion == that.modelVersion;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (int) (modelVersion ^ (modelVersion >>> 32));
        return result;
    }
}
