package eu.mihosoft.vmf.runtime.core.internal;

import eu.mihosoft.vmf.runtime.core.Reflect;
import eu.mihosoft.vmf.runtime.core.Property;
import eu.mihosoft.vmf.runtime.core.VObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Don't rely on this API. Seriously, <b>don't</b> rely on it!
 *
 * Created by miho on 18.06.2018.
 *
 * @author Michael Hoffer (info@michaelhoffer.de)
 */
@Deprecated
public class ReflectImpl implements Reflect {

    private VObject model;
    private List<Property> properties;

    public void setModel(VObject model) {
        this.model = model;
    }

    @Override
    public List<Property> properties() {

        if(properties==null) {

            VObjectInternal parent = (VObjectInternal) model;

            properties = new ArrayList<>(parent._vmf_getPropertyNames().length);

            for(String pName : parent._vmf_getPropertyNames()) {
                properties.add(Property.newInstance(parent, pName));
            }
        }

        return properties;
    }
}
