package com.vaadin.cdi.uis;

import javax.inject.Inject;

import com.vaadin.cdi.CDINavigator;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@CDIUI("")
public class NonPassivatingUI extends UI {

    @Inject CDIViewProvider provider;
    @Inject
    CDINavigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        navigator.init(this,this);
        navigator.addProvider(provider);
    }

}