package org.vaadin.addon.ewopener.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@Theme("demo")
public class AboutUI extends UI {
    @Override
    protected void init(VaadinRequest request) {
        setContent(new Label("EnhancedWindowOpener"));
    }
}
