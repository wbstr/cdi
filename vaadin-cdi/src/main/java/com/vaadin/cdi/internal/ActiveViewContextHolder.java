package com.vaadin.cdi.internal;

import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.util.CurrentInstance;

import java.io.Serializable;

@NormalUIScoped
public class ActiveViewContextHolder implements Serializable {
    private String activeViewName;
    private transient String openingView;

    public String getActiveViewName() {
        if (openingView != null) {
            return openingView;
        } else {
            return activeViewName;
        }
    }

    public void switchTo(String activeViewName) {
        this.activeViewName = activeViewName;
        revert();
    }

    public void revert() {
        openingView = null;
    }

    public void setOpeningView(String viewName) {
        openingView = viewName;
    }

}
