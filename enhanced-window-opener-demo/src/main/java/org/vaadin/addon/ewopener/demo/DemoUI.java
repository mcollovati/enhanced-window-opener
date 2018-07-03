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
package org.vaadin.addon.ewopener.demo;

import javax.servlet.annotation.WebServlet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.jsoup.safety.Whitelist;
import org.vaadin.addon.ewopener.EnhancedBrowserWindowOpener;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@Theme("demo")
@Title("Enhanced Window Opener Demo")
@Viewport("width=device-width, user-scalable=no, initial-scale=1.0")
@Push
@SuppressWarnings("serial")
public class DemoUI extends UI {

    private final AtomicInteger downloadCounter = new AtomicInteger(0);
    private Table table;

    @Override
    protected void init(VaadinRequest request) {

        EnhancedBrowserWindowOpener opener1 = new EnhancedBrowserWindowOpener()
            .popupBlockerWorkaround(true);
        Button button1 = new Button("Click me");
        button1.addClickListener(e -> {
            opener1.open(generateResource());
        });
        opener1.extend(button1);

        EnhancedBrowserWindowOpener opener4 = new EnhancedBrowserWindowOpener()
            .popupBlockerWorkaround(true);
        Button button4 = new Button("Nothing to open here");
        button4.addClickListener(e -> {
            opener4.open((Resource) null);
        });
        opener4.extend(button4);


        Button button2 = new Button("Click me");
        button2.addClickListener(e -> {
            EnhancedBrowserWindowOpener.extendOnce(button2)
                .open(generateResource());
        });

        Button button3 = new Button("Click me");
        EnhancedBrowserWindowOpener opener3 = new EnhancedBrowserWindowOpener()
            .popupBlockerWorkaround(true)
            .withGeneratedContent("myFileName.txt", this::generateContent)
            .doExtend(button3);
        button3.addClickListener(opener3::open);

        Link link = new Link("Click me", null);
        new EnhancedBrowserWindowOpener()
            .clientSide(true)
            .withGeneratedContent("myFileName.txt", this::generateContent)
            .doExtend(link);

        Link link2 = new Link("Click me", null);
        new EnhancedBrowserWindowOpener()
            .clientSide(true)
            .withGeneratedContent("myFileName.txt", this::generateContent,
                resource -> {
                    resource.setCacheTime(0);
                    resource.setFilename("runtimeFileName-" + Instant.now().getEpochSecond() + ".txt");
                })
            .doExtend(link2);


        EnhancedBrowserWindowOpener opener5 = new EnhancedBrowserWindowOpener(new ClassResource(DemoUI.class, "static.txt"));
        CssLayout hiddenComponent = new MCssLayout().withWidth("0").withHeight("0");
        opener5.extend(hiddenComponent);
        CompletableFuture.runAsync(this::doSomeLongProcessing)
            .thenRun(() -> getUI().access(opener5::open));

        table = new Table("Select items to download", new BeanItemContainer<>(DummyService.Person.class, DummyService.data()));
        table.setImmediate(true);
        table.setVisibleColumns("name", "age");
        table.setColumnHeaders("Name", "Age");
        table.setWidth("100%");
        table.setPageLength(20);
        table.setMultiSelectMode(MultiSelectMode.DEFAULT);
        table.setMultiSelect(true);
        table.setSelectable(true);


        final MyPopupContent popupContent = new MyPopupContent();
        Button popupButton = new Button("Open modal", event -> {
            Window window = new Window("Test", popupContent);
            window.setWidth(40, Sizeable.Unit.PERCENTAGE);
            window.setHeight(200, Sizeable.Unit.PIXELS);
            window.setModal(true);
            window.setDraggable(false);
            window.setResizable(false);
            window.center();
            getUI().addWindow(window);
        });

        MenuBar.Command cmd = selectedItem -> Notification.show(
            "Item clicked", "Item is " + selectedItem.getDescription(), Notification.Type.TRAY_NOTIFICATION
        );
        MenuBar menuBar = new MenuBar();
        menuBar.setSizeFull();
        EnhancedBrowserWindowOpener opener6 = new EnhancedBrowserWindowOpener()
            .withGeneratedContent("menu-item-serverside.txt", this::generateContent)
            .popupBlockerWorkaround(true);
        EnhancedBrowserWindowOpener opener7 = new EnhancedBrowserWindowOpener()
            .withGeneratedContent("menu-item-clientside-1.txt", this::generateContent)
            .clientSide(true);
        EnhancedBrowserWindowOpener opener8 = new EnhancedBrowserWindowOpener()
            .withGeneratedContent("menu-item-clientside-2.txt", this::generateContent)
            .clientSide(true);
        MenuBar.MenuItem menuItem = menuBar.addItem("Download from Menu (Client side)", selectedItem -> {
            System.out.println("OK, Invoked");
        });
        MenuBar.MenuItem subMenu = menuBar.addItem("Sub menu", null);
        subMenu.addItem("Item 1", cmd);
        subMenu.addItem("Item 2", cmd);
        MenuBar.MenuItem subItem = subMenu.addItem("Download (client side)", cmd);
        MenuBar.MenuItem subItem2 = subMenu.addItem("Download (server side)", selectedItem -> opener6.open());
        opener7.doExtend(menuBar, menuItem);
        opener6.doExtend(menuBar, subItem2);
        opener8.doExtend(menuBar, subItem);


        // Show it in the middle of the screen
        final Layout layout = new MVerticalLayout(
            new MLabel("Enhanced Window Opener Demo")
                .withStyleName(ValoTheme.LABEL_COLORED, ValoTheme.LABEL_H1),
            new MHorizontalLayout().add(table, 1).add(
                new MCssLayout(
                    menuBar, readMarkdown("code_menu.md"),
                    new MVerticalLayout(readMarkdown("code1.md"), button1)
                        .alignAll(Alignment.MIDDLE_CENTER).withWidthUndefined()
                        .withMargin(false),
                    new MVerticalLayout(readMarkdown("code2.md"), button2)
                        .alignAll(Alignment.MIDDLE_CENTER).withWidthUndefined().withMargin(false),
                    new MVerticalLayout(readMarkdown("code7.md"), button3)
                        .alignAll(Alignment.MIDDLE_CENTER).withWidthUndefined().withMargin(false),
                    new MVerticalLayout(readMarkdown("code5.md"), link)
                        .alignAll(Alignment.MIDDLE_CENTER).withWidthUndefined().withMargin(false),
                    new MVerticalLayout(readMarkdown("code6.md"), link2)
                        .alignAll(Alignment.MIDDLE_CENTER).withWidthUndefined().withMargin(false),
                    new MVerticalLayout(readMarkdown("code3.md"), button4)
                        .alignAll(Alignment.MIDDLE_CENTER).withWidthUndefined().withMargin(false),
                    new MVerticalLayout(readMarkdown("code8.md"), popupButton)
                        .alignAll(Alignment.MIDDLE_CENTER).withWidthUndefined().withMargin(false),
                    new MVerticalLayout(readMarkdown("code4.md"), hiddenComponent)
                        .alignAll(Alignment.MIDDLE_CENTER).withWidthUndefined().withMargin(false)
                ).withFullWidth().withStyleName("demo-samples")
                , 5).withFullWidth()
        ).withFullWidth().withMargin(true);

        setContent(layout);

    }

    private void doSomeLongProcessing() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private StreamResource generateResource() {
        StreamResource streamResource = new StreamResource(makeStreamSource(), "simpleTextFile.txt");
        streamResource.setCacheTime(0); // do not cache
        streamResource.setMIMEType("text/plain");
        return streamResource;
    }

    @SuppressWarnings("unchecked")
    private InputStream generateContent() {
        StringBuilder content = new StringBuilder()
            .append(String.format("File %d downloaded at %s", downloadCounter.incrementAndGet(),
                DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now())));
        Collection<DummyService.Person> data = (Collection<DummyService.Person>) table.getValue();
        data.stream().map(DummyService.Person::toString)
            .peek(s -> content.append(System.lineSeparator()))
            .forEach(content::append);
        return new ByteArrayInputStream(content.toString().getBytes());
    }

    private StreamResource.StreamSource makeStreamSource() {
        return this::generateContent;
    }

    private static RichText readMarkdown(String markdown) {
        try (Scanner sc = new Scanner(DemoUI.class.getResourceAsStream(markdown))) {
            RichText rt = new RichText()
                .withMarkDown(sc.useDelimiter("\\A").next())
                .setWhitelist(Whitelist.relaxed().addAttributes("code", "class").removeTags("br"));
            HighlightJS.of(rt);
            rt.setWidth(100, Unit.PERCENTAGE);
            return rt;
        }
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.addon.ewopener.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

}

class MyPopupContent extends MVerticalLayout {

    MButton button = new MButton("Open window");
    Button.ClickListener clickListener;

    public MyPopupContent() {
        add(new MLabel("Open from popup")
                .withStyleName(ValoTheme.LABEL_COLORED, ValoTheme.LABEL_H1),
            button
        );
    }

    @Override
    public void attach() {
        super.attach();
        EnhancedBrowserWindowOpener opener = EnhancedBrowserWindowOpener.extendOnce(button);
        clickListener = e2 -> opener.open(streamContent());
        button.addClickListener(clickListener);
    }

    @Override
    public void detach() {
        EnhancedBrowserWindowOpener.extendOnce(button).remove();
        button.removeClickListener(clickListener);
        super.detach();
    }

    private StreamResource streamContent() {
        StreamResource streamResource = new StreamResource(
            () -> new ByteArrayInputStream(LocalDateTime.now().toString().getBytes()), "simpleTextFile.txt"
        );
        streamResource.setCacheTime(0); // do not cache
        streamResource.setMIMEType("text/plain");
        return streamResource;
    }
}