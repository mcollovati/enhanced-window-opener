/*
 * Copyright (C) 2016 Marco Collovati (mcollovati@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addon.ewopener.client;

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.VConsole;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.extensions.BrowserWindowOpenerConnector;
import com.vaadin.client.ui.VMenuBar;
import com.vaadin.client.ui.menubar.MenuBarConnector;
import com.vaadin.shared.ui.BrowserWindowOpenerState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.util.SharedUtil;
import org.vaadin.addon.ewopener.EnhancedBrowserWindowOpener;
import org.vaadin.addon.ewopener.shared.EnhancedBrowserWindowOpenerState;

/**
 * Client side code for {@link EnhancedBrowserWindowOpener}.
 */
@Connect(EnhancedBrowserWindowOpener.class)
public class EnhancedBrowserWindowOpenerConnector extends BrowserWindowOpenerConnector {

    private JavaScriptObject window;
    private HandlerRegistration menuClickHandlerReg;


    @Override
    public void onClick(ClickEvent event) {
        if (getState().clientSide) {
            super.onClick(event);
        } else if (getState().popupBlockerWorkaround) {
            window = openWindow(getState().target, getState().features);
        }
    }

    @Override
    public void onUnregister() {
        window = null;
        super.onUnregister();
        if (menuClickHandlerReg != null) {
            menuClickHandlerReg.removeHandler();
        }


    }

    @Override
    public EnhancedBrowserWindowOpenerState getState() {
        return (EnhancedBrowserWindowOpenerState) super.getState();
    }

    @Override
    protected void extend(ServerConnector target) {
        if (target instanceof MenuBarConnector) {
            extendMenu((MenuBarConnector) target);
        } else {
            super.extend(target);
        }
    }


    private void extendMenu(MenuBarConnector target) {
        VMenuBar widget = target.getWidget();

        menuClickHandlerReg = widget.addHandler(new ServerSideIdAwareMenuItem.MenuItemSelectionHandler() {
            @Override
            public void onMenuItemSelected(ServerSideIdAwareMenuItem.MenuItemSelectedEvent event) {
                VConsole.log("======================= menu clicked " + EnhancedBrowserWindowOpenerConnector.this.getState().menuItem + " item is "
                    + event.getMenuItem().getServerSideId());
                if (event.getMenuItem().getServerSideId() == EnhancedBrowserWindowOpenerConnector.this.getState().menuItem) {
                    EnhancedBrowserWindowOpenerConnector.this.onClick(null);
                }
            }
        }, ServerSideIdAwareMenuItem.MenuItemSelectedEvent.getType());
    }

    @OnStateChange("lastUpdated")
    private void onLastUpdateChanged() {
        if (!getState().clientSide && getState().lastUpdated > 0) {
            if (getState().popupBlockerWorkaround && window != null) {
                String url = getResourceUrl(BrowserWindowOpenerState.locationResource);
                url = addParametersAndFragment(url);
                if (url != null) {
                    setWindowUrl(window, url);
                } else {
                    closeWindow(window);
                }
                window = null;
            } else {
                super.onClick(null);
            }

        }
    }

    private String addParametersAndFragment(String url) {
        if (url == null) {
            return null;
        }

        if (!getState().parameters.isEmpty()) {
            StringBuilder params = new StringBuilder();
            for (Map.Entry<String, String> entry : getState().parameters.entrySet()) {
                if (params.length() != 0) {
                    params.append('&');
                }
                params.append(URL.encodeQueryString(entry.getKey()));
                params.append('=');

                String value = entry.getValue();
                if (value != null) {
                    params.append(URL.encodeQueryString(value));
                }
            }

            url = SharedUtil.addGetParameters(url, params.toString());
        }

        if (getState().uriFragment != null) {
            // Replace previous fragment or just add to the end of the url
            url = url.replaceFirst("#.*|$", "#" + getState().uriFragment);
        }

        return url;
    }

    private static native JavaScriptObject openWindow(String name, String features) /*-{
    return $wnd.open(undefined, name, features);
  }-*/;

    private static native void setWindowUrl(JavaScriptObject window, String url) /*-{
    window.location.href = url;
  }-*/;

    private static native void closeWindow(JavaScriptObject window) /*-{
    window.close();
  }-*/;

    private static native void wrapMenu(VMenuBar hostReference) /*-{
    console.log("======================================== OK", hostReference);
    var clickFn = hostReference.onMenuClick;
    hostReference.onMenuClick = function(itemId) {
        console.log("My Fn invoked", itemId);
        clickFn.apply(this, arguments);
    }
  }-*/;
}
