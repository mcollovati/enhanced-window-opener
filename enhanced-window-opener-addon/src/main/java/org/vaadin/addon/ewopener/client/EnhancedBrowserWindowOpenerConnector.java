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

import com.google.gwt.event.dom.client.ClickEvent;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.extensions.BrowserWindowOpenerConnector;
import com.vaadin.shared.ui.Connect;
import org.vaadin.addon.ewopener.EnhancedBrowserWindowOpener;
import org.vaadin.addon.ewopener.shared.EnhancedBrowserWindowOpenerState;

/**
 * Client side code for {@link EnhancedBrowserWindowOpener}.
 */
@Connect(EnhancedBrowserWindowOpener.class)
public class EnhancedBrowserWindowOpenerConnector extends BrowserWindowOpenerConnector {

    @Override
    public void onClick(ClickEvent event) {
        if (getState().clientSide) {
            super.onClick(event);
        }
    }


    @OnStateChange("lastUpdated")
    private void onLastUpdateChanged() {
        if (!getState().clientSide && getState().lastUpdated > 0) {
            super.onClick(null);
        }
    }

    @Override
    public EnhancedBrowserWindowOpenerState getState() {
        return (EnhancedBrowserWindowOpenerState) super.getState();
    }
}
