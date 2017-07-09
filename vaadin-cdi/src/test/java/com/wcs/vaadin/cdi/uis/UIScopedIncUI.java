package com.wcs.vaadin.cdi.uis;

import com.wcs.vaadin.cdi.CDIUI;
import com.wcs.vaadin.cdi.NormalUIScoped;
import com.wcs.vaadin.cdi.UIScoped;
import com.wcs.vaadin.cdi.internal.ClusterIncTestLayout;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import javax.inject.Inject;

@CDIUI("uiscoped")
public class UIScopedIncUI extends UI {

    @Inject
    UIScopedBean uiScopedBean;

    @Inject
    NormalUIScopedBean normalUIScopedBean;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();
        ClusterIncTestLayout layout = new ClusterIncTestLayout();
        setContent(layout);
        layout.setSizeFull();

        layout.init(uiScopedBean, normalUIScopedBean);
    }

    @UIScoped
    public static class UIScopedBean extends ClusterIncTestLayout.IncTestBean {
    }

    @NormalUIScoped
    public static class NormalUIScopedBean extends ClusterIncTestLayout.IncTestBean {
    }

}
