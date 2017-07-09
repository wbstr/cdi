package com.wcs.vaadin.cdi.uis;

import com.wcs.vaadin.cdi.CDIUI;
import com.wcs.vaadin.cdi.NormalUIScoped;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@CDIUI
@NormalUIScoped
public class CDIUIWrongScope extends UI {
    @Override
    protected void init(VaadinRequest request) {

    }
}
