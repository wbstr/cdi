package com.wcs.vaadin.cdi.uis;

import javax.inject.Inject;

import com.wcs.vaadin.cdi.CDINavigator;
import com.wcs.vaadin.cdi.CDIUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@CDIUI("")
public class NonPassivatingUI extends UI {

    @Inject
    CDINavigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        navigator.init(this,this);
    }

}
