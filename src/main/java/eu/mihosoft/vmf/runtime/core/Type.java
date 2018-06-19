package eu.mihosoft.vmf.runtime.core;

import java.util.Objects;

public final class Type {

    private boolean modelType;
    private String name;

    private Type(boolean modelType, String name) {
        this.modelType = modelType;
        this.name = name;
    }

    static Type newInstance(boolean modelType, String name) {
        return new Type(modelType, name);
    }

    public String getName() {
        return this.name;
    }

    public boolean isModelType() {
        return this.modelType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return modelType == type.modelType &&
                Objects.equals(name, type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelType, name);
    }

    @Override
    public String toString() {
        return "[ name=" + name + ", modelType=" + modelType + " ]";
    }
}
