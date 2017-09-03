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
 */
package com.wcs.vaadin.cdi;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;
import com.wcs.vaadin.cdi.ViewContextStrategy.ViewNameAndParameters;

import javax.enterprise.inject.Stereotype;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes implementing {@link View} and annotated with <code>@CDIView</code>
 * are automatically registered with {@link CDIViewProvider} for use by
 * {@link Navigator}.
 * <p>
 * By default, the view name is derived from the class name of the annotated
 * class, but this can also be overriden by defining a {@link #value()}.
 * <p>
 * <code>@CDIView</code> views are by default {@link ViewScoped}.
 * 
 * @see javax.inject.Named
 */
@Stereotype
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
@ViewScoped
public @interface CDIView {

    /**
     * 
     * The name of the CDIView can be derived from the simple class name So it
     * is optional. Also multiple views without a value may exist at the same
     * time.
     * <p>
     * Example: UserDetailView by convention becomes "user-detail" and
     * UserCDIExample becomes "user-cdi-example".
     */
    public String value() default USE_CONVENTIONS;

    /**
     * USE_CONVENTIONS is treated as a special case that will cause the
     * automatic View mapping to occur.
     */
    public static final String USE_CONVENTIONS = "USE CONVENTIONS";

    /**
     * Specifies whether view parameters can be passed to the view as part of
     * the name, i.e in the form of {@code viewName/viewParameters}. Make sure
     * there are no other views that start with the same name, since the
     * ViewProvider will only check that the given {@code viewAndParameters}
     * starts with the view name.
     */
    public boolean supportsParameters() default false;

    /**
     * Specifies which UIs can show the view. {@link CDIViewProvider} only lists
     * the views that have the current UI on this list.
     * <p>
     * If this list contains UI.class, the view is available for all UIs.
     * <p>
     * This only needs to be specified if the application has multiple UIs that
     * use {@link CDIViewProvider}.
     * 
     * @return list of UIs in which the view can be shown.
     */
    public Class<? extends UI>[] uis() default { UI.class };

    /**
     * The strategy to decide when to open a new context for this view.
     *
     * @see ViewNameAndParameters
     * @see com.wcs.vaadin.cdi.ViewContextStrategy.ViewName
     * @see com.wcs.vaadin.cdi.ViewContextStrategy.Always
     * @return a strategy class
     */
    public Class<? extends ViewContextStrategy> contextStrategy() default ViewNameAndParameters.class;
}
