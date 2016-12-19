package com.vaadin.cdi.internal;

import com.vaadin.cdi.NormalUIScoped;

import java.io.Serializable;

@NormalUIScoped
public class ActiveViewContextHolder implements Serializable {
    private String activeViewName;

    public String getActiveViewName() {
        return activeViewName;
    }

    public void setActiveViewName(String activeViewName) {
        this.activeViewName = activeViewName;
    }
}
