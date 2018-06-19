package eu.mihosoft.vmf.runtime.core;

import eu.mihosoft.vmf.runtime.core.internal.VObjectInternal;
import eu.mihosoft.vmf.runtime.core.internal.VObjectInternalModifiable;

@SuppressWarnings("deprecation")
public final class Property {

    private VObjectInternal parent;
    private int propertyId;
    private String name;
    private Type type;

    private Property(VObjectInternal parent, String name) {
        this.parent = parent;
        this.name = name;
        this.propertyId = parent._vmf_getPropertyIdByName(name);

        boolean isModelType = parent._vmf_getPropertyTypes()[propertyId]!=-1;

        this.type = Type.newInstance(isModelType, parent._vmf_getPropertyTypeNames()[propertyId]);
    }

    public static Property newInstance(VObjectInternal parent, String name) {
        return new Property(parent, name);
    }

    public boolean isSet() {
        return parent._vmf_isSetById(propertyId);
    }

    public void set(Object o) {
        if(parent instanceof VObjectInternalModifiable) {
            ((VObjectInternalModifiable)parent)._vmf_setPropertyValueById(propertyId, o);
        } else {
            throw new RuntimeException("Cannot modify unmodifiable object");
        }
    }

    public void unset() {
        if(parent instanceof VObjectInternalModifiable) {
            ((VObjectInternalModifiable)parent)._vmf_setPropertyValueById(propertyId, getDefault());
        } else {
            throw new RuntimeException("Cannot modify unmodifiable object");
        }
    }

    public Object get() {
        return parent._vmf_getPropertyValueById(propertyId);
    }

    public void setDefault(Object value) {
        if(parent instanceof VObjectInternalModifiable) {
            ((VObjectInternalModifiable)parent)._vmf_setDefaultValueById(propertyId, value);
        } else {
            throw new RuntimeException("Cannot modify unmodifiable object");
        }
    }

    public Object getDefault() {
        return parent._vmf_getDefaultValueById(propertyId);
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }


}
