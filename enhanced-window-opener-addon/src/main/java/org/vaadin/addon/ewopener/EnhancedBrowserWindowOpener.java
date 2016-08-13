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
package org.vaadin.addon.ewopener;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.vaadin.addon.ewopener.shared.EnhancedBrowserWindowOpenerState;

import java.time.Instant;
import java.util.Objects;

/**
 * Extension of {@link BrowserWindowOpener} that simplifies open windows for
 * runtime generated content.
 *
 * Extension could work as server side component or in client side mode.
 * In the first case there no client side click listeners are added to the target component;
 * to open a window call {@link #open()} method on server side code.
 * In client side mode it behaves like {@link BrowserWindowOpener} but it allows to
 * to generate the content on download request.
 */
public class EnhancedBrowserWindowOpener extends BrowserWindowOpener {

    /**
     * {@inheritDoc}
     */
    public EnhancedBrowserWindowOpener(Class<? extends UI> uiClass) {
        super(uiClass);
    }

    /**
     * {@inheritDoc}
     */
    public EnhancedBrowserWindowOpener(Class<? extends UI> uiClass, String path) {
        super(uiClass, path);
    }

    /**
     * {@inheritDoc}
     */
    public EnhancedBrowserWindowOpener(String url) {
        super(url);
    }

    /**
     * {@inheritDoc}
     */
    public EnhancedBrowserWindowOpener(Resource resource) {
        super(resource);
    }

    /**
     * Creates a window opener without a resource.
     */
    public EnhancedBrowserWindowOpener() {
        super((Resource) null);
    }


    /**
     * Extends the target component or return an already attached extension instance.
     *
     * @param target The component to extend
     * @return The extension instance
     */
    public static EnhancedBrowserWindowOpener extendOnce(AbstractComponent target) {
        return Objects.requireNonNull(target).getExtensions().stream()
            .filter(EnhancedBrowserWindowOpener.class::isInstance)
            .findFirst()
            .map(EnhancedBrowserWindowOpener.class::cast)
            .orElseGet(() -> new EnhancedBrowserWindowOpener().doExtend(target));
    }


    /**
     * Forces the client to open a window for the given resource when the current request completes.
     *
     * @param resource the resource to open in the window
     * @return current object for further customization
     */
    public EnhancedBrowserWindowOpener open(Resource resource) {
        setResource(resource);
        return open();
    }

    /**
     * Forces the client to open a window for the given url when the current request completes.
     *
     * @param url then url to open in the window
     * @return current object for further customization
     */
    public EnhancedBrowserWindowOpener open(String url) {
        setUrl(url);
        return open();
    }

    /**
     * Forces the client to open a window for the configured resource when the current request completes.
     *
     * @return current object for further customization
     */
    public final EnhancedBrowserWindowOpener open() {
        getState().lastUpdated = Instant.now().toEpochMilli();
        return this;
    }

    /**
     * Simple method to attach the extension as listener for components.
     *
     * {@code
     *  opener = new EnhancedBrowserWindowOpener();
     *  ...
     *  button.addClickListener(opener::open);
     * }
     *
     * @param event event
     * @return current object for further customization
     */
    public final EnhancedBrowserWindowOpener open(Component.Event event) {
        return open();
    }


    /**
     * Set if the extension should work as client side listener.
     *
     * If set to true the extension behaves exactly as {@link BrowserWindowOpener}.
     * Defaults to false.
     *
     * @param clientSide true to enable client side mode, otherwise false.
     * @return current object for further customization
     */
    public EnhancedBrowserWindowOpener clientSide(boolean clientSide) {
        getState().clientSide = clientSide;
        return this;
    }

    /**
     * Sets a {@code resource} for this instance whose content will be generated
     * when the window will be opened.
     *
     * The content will be generated through {@code generator} instance.
     *
     * @param filename  The filename for the generated resource.
     * @param generator The content generator.
     * @return current object for further customization
     */
    public EnhancedBrowserWindowOpener withGeneratedContent(String filename, DynamicContentGenerator generator) {
        return withGeneratedContent(filename, generator, r -> {
        });
    }

    /**
     * Sets a {@code resource} for this instance whose content will be generated
     * when the window will be opened.
     *
     * The content will be generated through {@code generator} instance.
     * The {@link StreamResource} could be customized through given {@code resourceCustomizer}.
     *
     * @param filename           The filename for the generated resource.
     * @param generator          The content generator.
     * @param resourceCustomizer The {@link StreamResource} customizer
     * @return current object for further customization
     */
    public EnhancedBrowserWindowOpener withGeneratedContent(String filename, DynamicContentGenerator generator,
                                                            StreamResourceCustomizer resourceCustomizer) {
        Objects.requireNonNull(generator);
        Objects.requireNonNull(resourceCustomizer);
        StreamResource resource = new StreamResource(null, filename) {
            @Override
            public StreamSource getStreamSource() {
                return generator.streamSource();
            }
        };
        resourceCustomizer.customize(resource);
        setResource(resource);
        return this;
    }


    // Fluent sugar

    /**
     * Extends the given connector.
     *
     * @param target The connector to extend
     * @return current object for further customization
     */
    public EnhancedBrowserWindowOpener doExtend(AbstractClientConnector target) {
        this.extend(target);
        return this;
    }

    /**
     * A fluent setter for {@link #setFeatures(String)}.
     *
     * @param features a string with window features, or <code>null</code> to use the default features.
     * @return current object for further customization
     * @see #setFeatures(String)
     */
    public EnhancedBrowserWindowOpener withFeatures(String features) {
        setFeatures(features);
        return this;
    }

    /**
     * A fluent setter for {@link #setWindowName(String)}.
     *
     * @param windowName the target name for the window
     * @return current object for further customization
     * @see #setWindowName(String)
     */
    public EnhancedBrowserWindowOpener withWindowName(String windowName) {
        setWindowName(windowName);
        return this;
    }

    /**
     * A fluent setter for {@link #setParameter(String, String)}.
     *
     * @param name  the name of the parameter to add, not <code>null</code>
     * @param value the value of the parameter to add, not <code>null</code>
     * @return current object for further customization
     * @see #setParameter(String, String)
     */
    public EnhancedBrowserWindowOpener withParameter(String name, String value) {
        setParameter(name, value);
        return this;
    }

    /**
     * A fluent setter for {@link #setUriFragment(String)}.
     *
     * @param uriFragment the URI fragment string that should be included in the opened
     *                    URI, or <code>null</code> to preserve the original fragment of
     *                    the URI.
     * @return current object for further customization
     * @see #setUriFragment(String)
     */
    public EnhancedBrowserWindowOpener withUriFragment(String uriFragment) {
        setUriFragment(uriFragment);
        return this;
    }

    @Override
    protected EnhancedBrowserWindowOpenerState getState() {
        return (EnhancedBrowserWindowOpenerState) super.getState();
    }

    @Override
    protected EnhancedBrowserWindowOpenerState getState(boolean markAsDirty) {
        return (EnhancedBrowserWindowOpenerState) super.getState(markAsDirty);
    }


}
