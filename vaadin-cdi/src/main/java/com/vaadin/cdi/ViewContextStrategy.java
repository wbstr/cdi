/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.cdi;


import java.io.Serializable;

/**
 * Decision strategy whether target navigation state
 * belongs to active view context.
 * <p>
 * Separate annotations annotated by {@link ViewContextStrategyQualifier}
 * have to exist for each of the implementations.
 * <p>
 * Example of a custom implementation:
 * <p>
 * A separate annotation.
 * <pre>
 * {@literal @}Retention(RetentionPolicy.RUNTIME)
 * {@literal @}Target({ ElementType.TYPE })
 * {@literal @}ViewContextStrategyQualifier
 *  public {@literal @}interface MyStrategyAnnotation {
 *  }
 * </pre>
 * An implementation class. Instantiated by CDI, so should have a scope.
 * <pre>
 * {@literal @}NormalUIScoped
 * {@literal @}MyStrategyAnnotation
 *  public class MyStrategy implements ViewContextStrategy {
 *    public boolean contains(String viewName, String parameters) {
 *      ...
 *    }
 *  }
 * </pre>
 * Use annotation on the view.
 * <pre>
 * {@literal @}CDIView("myView")
 * {@literal @}MyStrategyAnnotation
 *  public MyView implements View {
 *  ...
 *  }
 * </pre>
 */
public interface ViewContextStrategy extends Serializable {

    /**
     * Whether active context contains target navigation state.
     *
     * @param viewName   target navigation view name
     * @param parameters target navigation parameters
     * @return true, to hold context open, false to release, and create a new context
     */
    boolean contains(String viewName, String parameters);

}
