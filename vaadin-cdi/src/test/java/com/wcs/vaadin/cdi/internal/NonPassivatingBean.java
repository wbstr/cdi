package com.wcs.vaadin.cdi.internal;

import com.wcs.vaadin.cdi.ViewScoped;

// Does not implement Serializable
@ViewScoped
public class NonPassivatingBean {

    private String someString = "NonPassivatingBean" + hashCode();
    
    public String getSomeString() {
        return someString;
    }
}
