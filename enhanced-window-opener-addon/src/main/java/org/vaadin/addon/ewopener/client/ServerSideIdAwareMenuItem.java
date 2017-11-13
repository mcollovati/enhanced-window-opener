/*
 * Copyright (C) 2016-2017 Marco Collovati (mcollovati@gmail.com)
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


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Command;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.VMenuBar;

/**
 * {@link com.vaadin.client.ui.VMenuBar.CustomMenuItem} extension that tracks
 * the server side menu item id.
 */
public class ServerSideIdAwareMenuItem extends VMenuBar.CustomMenuItem {

    private int serverSideId = -1;

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        serverSideId = uidl.getIntAttribute("id");
    }

    @Override
    public Command getCommand() {
        Command command = super.getCommand();
        if (getSubMenu() == null) {
            return () -> {
                fireMenuItemSelectedEvent();
                if (command != null) {
                    command.execute();
                }
            };
        }
        return command;
    }

    public int getServerSideId() {
        return serverSideId;
    }

    private void fireMenuItemSelectedEvent() {
        VMenuBar parent = getParentMenu();
        while (parent.getParentMenu() != null) {
            parent = parent.getParentMenu();
        }
        parent.fireEvent(new MenuItemSelectedEvent(this));
    }

    public interface MenuItemSelectionHandler extends EventHandler {
        void onMenuItemSelected(MenuItemSelectedEvent event);
    }

    public static class MenuItemSelectedEvent extends GwtEvent<MenuItemSelectionHandler> {

        private static final Type<MenuItemSelectionHandler> EVENT_TYPE = new Type<>();

        private final ServerSideIdAwareMenuItem menuItem;

        public MenuItemSelectedEvent(ServerSideIdAwareMenuItem menuItem) {
            this.menuItem = menuItem;
        }

        public ServerSideIdAwareMenuItem getMenuItem() {
            return menuItem;
        }

        @Override
        public Type<MenuItemSelectionHandler> getAssociatedType() {
            return EVENT_TYPE;
        }

        @Override
        protected void dispatch(MenuItemSelectionHandler handler) {
            handler.onMenuItemSelected(this);
        }

        public static Type<MenuItemSelectionHandler> getType() {
            return EVENT_TYPE;
        }
    }

}
