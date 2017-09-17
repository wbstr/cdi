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

import java.lang.annotation.*;

/**
 * Annotation for the strategy to release, and create a new context
 * on every navigation regardless of view name and parameters.
 * <p>
 * It is on par with navigator view lifecycle,
 * but navigating to same view with same parameters
 * trigger a navigation too.
 * <p>
 * In practice it works same as {@link ViewNameAndParametersDriven},
 * even when parameters do not change.
 *
 * @see com.vaadin.cdi.ViewContextStrategy
 * @see ViewNameDriven
 * @see ViewNameAndParametersDriven
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
@ViewContextStrategyQualifier
public @interface EveryNavigationDriven {
}


