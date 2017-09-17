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

import com.vaadin.navigator.ViewChangeListener;

import java.lang.annotation.*;

/**
 * Annotation for the strategy to hold the context open while
 * view name and view parameters do not change.
 * <p>
 * This strategy is the default, it is on par with navigator view lifecycle.
 * <p>
 * Before applying the view change new view context is activated,
 * and old view context is closed.
 * It happens right after all {@link ViewChangeListener.ViewChangeEvent#beforeViewChange(ViewChangeListener.ViewChangeEvent)} returned true;
 * <p>
 * {@link com.vaadin.navigator.View#enter(ViewChangeListener.ViewChangeEvent)} won't be called
 * again on the same view instance.
 *
 * @see com.vaadin.cdi.ViewContextStrategy
 * @see ViewNameDriven
 * @see EveryNavigationDriven
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@ViewContextStrategyQualifier
public @interface ViewNameAndParametersDriven {
}


