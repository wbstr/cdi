package com.wcs.vaadin.cdi.internal;

import com.wcs.vaadin.cdi.NormalViewScoped;

@NormalViewScoped
public class ViewScopedBean {

    public static final String ID = "view-scoped-bean";

    public ViewScopedBean() {
    }

    public ViewScopedBean getUnderlyingInstance() {
        return this;
    }

}
