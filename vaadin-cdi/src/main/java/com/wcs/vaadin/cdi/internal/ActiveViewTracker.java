/*
 * Copyright 2017 kumm.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.wcs.vaadin.cdi.internal;

import com.vaadin.navigator.ViewChangeListener;
import com.wcs.vaadin.cdi.CDINavigator;
import com.wcs.vaadin.cdi.NormalUIScoped;
import com.wcs.vaadin.cdi.UIContextInfo;

import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.Serializable;

@NormalUIScoped
public class ActiveViewTracker implements Serializable {
    private String viewName;
    private String parameters;
    @Inject
    private CDINavigator navigator;

    private void init(@Observes @Initialized(NormalUIScoped.class) UIContextInfo info) {
        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                viewName = event.getViewName();
                parameters = event.getParameters();
            }
        });
    }

    public String getViewName() {
        return viewName;
    }

    public String getParameters() {
        return parameters;
    }
}
