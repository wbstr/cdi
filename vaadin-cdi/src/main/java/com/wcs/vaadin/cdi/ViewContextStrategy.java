
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

import com.wcs.vaadin.cdi.internal.ActiveViewTracker;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

/**
 * Decision strategy whether the target navigation state
 * belongs to the active view context.
 */
public interface ViewContextStrategy extends Serializable {

    /**
     * Whether the active context contains the target navigation state.

     * @param state target navigation state
     * @return true, to hold context open, false to release, and create a new context
     */
    boolean contains(ViewState state);

    /**
     * Strategy to hold the context open while
     * view name does not change.
     */
    @ApplicationScoped
    class ViewName implements ViewContextStrategy {
        @Inject
        private ActiveViewTracker activeState;

        @Override
        public boolean contains(ViewState state) {
            return Objects.equals(state.getViewName(), activeState.getViewName());
        }
    }

    /**
     * Strategy to hold the context open while
     * view name and view parameters does not change.
     */
    @ApplicationScoped
    class ViewNameAndParameters implements ViewContextStrategy {
        @Inject
        private ActiveViewTracker activeState;

        @Override
        public boolean contains(ViewState state) {
            return Objects.equals(state.getViewName(), activeState.getViewName())
                    && Objects.equals(state.getParameters(), activeState.getParameters());
        }
    }

    /**
     * Strategy to release, and create a new context on every navigation
     * regardless of view name and parameters.
     */
    @ApplicationScoped
    class Always implements ViewContextStrategy {
        @Override
        public boolean contains(ViewState state) {
            return false;
        }
    }

    /**
     * Represents the state of the view to provide information
     * for strategies.
     */
    class ViewState {
        private final String viewName;
        private final String parameters;

        /**
         * Contruct by viewname and parameters
         * @param viewName view name
         * @param parameters view parameters
         */
        public ViewState(String viewName, String parameters) {
            this.viewName = viewName;
            this.parameters = parameters;
        }

        /**
         * Getter for view name
         * @return view name
         */
        public String getViewName() {
            return viewName;
        }

        /**
         * Getter for view parameters
         * @return view parameters
         */
        public String getParameters() {
            return parameters;
        }

    }

}
