Vaadin AltCDI
===============

AltCDI is a fork of the official Vaadin CDI integration for
[Vaadin Framework](https://github.com/vaadin/framework).

This addon is compatible with the official addon as far as it can, but contains breaking changes.
I've reconsidered some fundamental concepts.

Breaking changes
---

### package names
I've changed package names from "com.vaadin.cdi" to "com.wcs.vaadin.cdi".
Just to be sure which cdi addon are you using.

### @NormalUIScoped UIs are not supported.
It means Every @CDIUI have to be @UIScoped.
Just omit any scope on your @CDIUI, it's @UIScoped implicitly.

The UI bean, all UIScoped, and all NormalUIScoped beans destroyed right
on UI detach.
In contrast [here](https://github.com/vaadin/cdi/issues/191) is a
summary of the official behaviour.

### ViewScoped context lifecycle changed.
A new ViewScoped context is
- created before beforeViewChange fired
- activated before afterViewChange fired
- destroyed before next afterViewChange
- destroyed immediately when viewChange reverted by a viewChangeListener.
In this case previous context is reactivated - see related
[issue](https://github.com/vaadin/cdi/issues/190).

To sum up @ViewScope now in sync with Vaadin view navigation and
view change events.

Every navigation (even to the same view with different parameters) creates a new view scoped
context, and destroys the previous. The enter method will be called on
a fresh new instance of the bean.
In case you want your view bean to survive navigation, use a
@UIScoped view.

Note: since vaadin 7.6.7 navigate to exact same view and params is a no-op.


@Dependent views are not supported.
The new behaviour of view scope is the same as you would expect from
dependent scope.

### CDINavigator API introduced
To make @ViewScoped work, you have to use CDINavigator instead of
standard Vaadin Navigator. Not a big deal, since it's a CDI bean
extended form Vaadin Navigator.
As a benefit CDIViewProvider, is managed automatically. Just use CDINavigator instead of CDIViewProvider.

```
@CDIUI("")
public class MyUI extends UI {
    @Inject
    CDINavigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        navigator.init(this, myViewContainer);

```
To navigate you can inject CDINavigator in your CDI beans, and just
call navigateTo.
Or use ui.getNavigator().

New features
----

### @VaadinSessionScoped introduced.
This is a normal ( proxied ) scope.
Should be VaadinSessionNormalScoped, but since it makes no sense to
handle non-normal session scope, I've took the shorter name.

Other Vaadin CDI contexts are stored in the corresponding
VaadinSessionScoped context. VaadinSessionScoped context itself stored
in VaadinSession.

### Clustering supported by http session replication.
Since VaadinSessionScoped context stored in VaadinSession,
it's replicated with HttpSession.

Well in theory, because in practice there is an
[issue](https://github.com/vaadin/framework/issues/7535).
I've introduced a workaround to make replication really happen.


Building the project
----
Execute `mvn clean install` in the root directory to build vaadin-cdi.

Issue tracking
----
If you find an issue, please report it in the GitHub issue tracker: https://github.com/wbstr/vaadin-alt-cdi/issues

Contributions
----
Contributions to the project can be done through pull requests in GitHub.


Copyright 2016 Vaadin Ltd.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.

Modifications copyright (C) 2017 kumm