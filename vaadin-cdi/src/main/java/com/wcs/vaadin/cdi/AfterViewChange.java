package com.wcs.vaadin.cdi;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import static java.lang.annotation.ElementType.*;

/**
 * {@link ViewChangeEvent} can be observed with this qualifier.
 *
 * Observers called after all non-cdi
 * {@link com.vaadin.navigator.ViewChangeListener#afterViewChange(ViewChangeEvent)} listeners.
 *
 * Keep in mind, the context of the new view is activated before the event is fired.
 * Acessing any {@link NormalViewScoped} bean through
 * {@link ViewChangeEvent#getOldView()} might lead do unexpected result,
 * because the bean is looked up in the new context.
 *
 * Though, the context of the new view, and the context of the old view can be the same
 * according to {@link CDIView#contextStrategy()}.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, METHOD, PARAMETER, FIELD })
public @interface AfterViewChange {
}
