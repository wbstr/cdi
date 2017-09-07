
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

package com.wcs.vaadin.cdi;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.Serializable;
import java.util.Objects;

/**
 * Decision strategy whether the target navigation state
 * belongs to the active view context.
 */
public interface ViewContextStrategy extends Serializable {

    /**
     * Whether the active context contains the target navigation state.

     * @param viewName target navigation view name
     * @param parameters target navigation parameters
     * @return true, to hold context open, false to release, and create a new context
     */
    boolean contains(String viewName, String parameters);

    /**
     * Strategy to hold the context open while
     * view name does not change.
     */
    @NormalUIScoped
    class ViewName implements ViewContextStrategy {
        private String currentViewName;

        private void onViewChange(@Observes @AfterViewChange ViewChangeEvent event) {
            currentViewName = event.getViewName();
        }

        @Override
        public boolean contains(String viewName, String parameters) {
            return Objects.equals(viewName, currentViewName);
        }
    }

    /**
     * Strategy to hold the context open while
     * view name and view parameters does not change.
     *
     * This strategy is on par with vaadin navigator behaviour.
     */
    @NormalUIScoped
    class ViewNameAndParameters implements ViewContextStrategy {
        private String currentViewName;
        private String currentParameters;

        private void onViewChange(@Observes @AfterViewChange ViewChangeEvent event) {
            currentViewName = event.getViewName();
            currentParameters = event.getParameters();
        }

        @Override
        public boolean contains(String viewName, String parameters) {
            return Objects.equals(viewName, currentViewName)
                    && Objects.equals(parameters, currentParameters);
        }
    }

    /**
     * Strategy to release, and create a new context on every navigation
     * regardless of view name and parameters.
     */
    @ApplicationScoped
    class Always implements ViewContextStrategy {
        @Override
        public boolean contains(String viewName, String parameters) {
            return false;
        }
    }

}
