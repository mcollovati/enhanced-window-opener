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

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.ClassResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.jsoup.safety.Whitelist;
import org.vaadin.addon.ewopener.EnhancedBrowserWindowOpener;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.servlet.annotation.WebServlet;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Theme("demo")
@Title("Enhanced Window Opener Demo")
@Viewport("width=device-width, user-scalable=no, initial-scale=1.0")
@Push
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.addon.ewopener.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private final AtomicInteger downloadCounter = new AtomicInteger(0);

    @Override
    protected void init(VaadinRequest request) {

        EnhancedBrowserWindowOpener opener1 = new EnhancedBrowserWindowOpener();
        Button button1 = new Button("Click me");
        button1.addClickListener(e -> {
            opener1.open(generateResource());
        });
        opener1.extend(button1);


        Button button2 = new Button("Click me");
        button2.addClickListener(e -> {
            EnhancedBrowserWindowOpener.extendOnce(button2)
                .open(generateResource());
        });

        Button button3 = new Button("Click me");
        EnhancedBrowserWindowOpener opener3 = new EnhancedBrowserWindowOpener()
            .withGeneratedContent("myFileName.txt", this::makeStreamSource)
            .doExtend(button3);
        button3.addClickListener(opener3::open);

        Link link = new Link("Click me", null);
        new EnhancedBrowserWindowOpener()
            .clientSide(true)
            .withGeneratedContent("myFileName.txt", this::makeStreamSource)
            .doExtend(link);

        Link link2 = new Link("Click me", null);
        new EnhancedBrowserWindowOpener()
            .clientSide(true)
            .withGeneratedContent("myFileName.txt", this::makeStreamSource,
                resource -> {
                    resource.setCacheTime(0);
                    resource.setFilename("runtimeFileName-" + Instant.now().getEpochSecond() + ".txt");
                })
            .doExtend(link2);


        EnhancedBrowserWindowOpener opener4 = new EnhancedBrowserWindowOpener(new ClassResource(DemoUI.class, "static.txt"));
        Button button4 = new Button("Click me");
        button4.addClickListener(e -> {
            opener4.open();
        });
        opener4.extend(button4);

        EnhancedBrowserWindowOpener opener5 = new EnhancedBrowserWindowOpener(new ClassResource(DemoUI.class, "static.txt"));
        CssLayout hiddenComponent = new MCssLayout().withWidth("0").withHeight("0");
        opener5.extend(hiddenComponent);
        CompletableFuture.runAsync(this::doSomeLongProcessing)
            .thenRun(() -> getUI().access(opener5::open));


        // Show it in the middle of the screen
        final Layout layout = new MVerticalLayout(
            new MLabel("Enhanced Window Opener Demo")
                .withStyleName(ValoTheme.LABEL_COLORED, ValoTheme.LABEL_H1),
            new MCssLayout(
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
                new MVerticalLayout(readMarkdown("code4.md"), hiddenComponent)
                    .alignAll(Alignment.MIDDLE_CENTER).withWidthUndefined().withMargin(false)
            ).withFullWidth().withStyleName("demo-samples")
        ).withFullWidth().withMargin(true);

        setContent(layout);

    }

    private void doSomeLongProcessing() {
        try {
            Thread.sleep(10000);
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

    private StreamResource.StreamSource makeStreamSource() {
        return () -> {
            String content = String.format("File %d downloaded at %s", downloadCounter.incrementAndGet(),
                DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()));
            return new ByteArrayInputStream(content.getBytes());
        };
    }

    private static RichText readMarkdown(String markdown) {
        try (Scanner sc = new Scanner(DemoUI.class.getResourceAsStream(markdown))) {
            RichText rt = new RichText()
                .withMarkDown(sc.useDelimiter("\\A").next())
                .setWhitelist(Whitelist.relaxed().addAttributes("code", "class").removeTags("br"));
            HighlightJS.of(rt);
            return rt;
        }
    }

}
