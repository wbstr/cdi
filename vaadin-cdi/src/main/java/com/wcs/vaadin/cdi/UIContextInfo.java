package com.wcs.vaadin.cdi;

public class UIContextInfo {
    final private int uiId;

    public UIContextInfo(int uiId) {
        this.uiId = uiId;
    }

    public int getUiId() {
        return uiId;
    }
}
