# Enhanced Window Opener Add-on for Vaadin 7

Enhanced Window Opener is an extension of Vaadin BrowserWindowOpener that aims
to simplify the opening of generated content in a new browser window or tab.
If you need to serve static content or to open an external URL please
use [BrowserWindowOpener](https://vaadin.com/docs/-/part/framework/advanced/advanced-windows.html#advanced.windows.popup.popping) 
from Vaadin Framework.
 

There are a lot of post on vaadin forum regarding the usage BrowserWindowOpener extension.
The main problems encountered by people depends on a misunderstanding of how 
BrowserWindowOpener works; it does not open a window immediately when
the target component is extended but instead it adds a client side click listener
on target component, so its effects are available only after the connector has done its work.

To make the BrowserWindowOpener work correctly you should extend the target component 
(a Button for example) only once and before you use the component on client side.
 
 For instance, if you want to open a window clicking on a Button you should do something like this
 
```java
Button button = new Button(...);
BrowserWindowOpener opener = new BrowserWindowOpener(new ExternalResource("http://..."));
 opener.extend(button);
```

Most people do this on the Button click listener

```java
Button button = new Button();
button.addClickListener( e -> {
    new BrowserWindowOpener(new ExternalResource("http://..."))
        .extend(button);
});
```

This leeds to two problemes:

1. the window will not be opened after the click, but only on the next click on the button
1. the second click on the button will open the window but, after that, it will also
   execute the server side listener extending the button once again and consequently 
   adding another click listener on the client side; next time clicking the button will 
   open two windows and so on

Another problem frequently noticed on forum is about opening in a new window a runtime
generated file; most people puts the generation logic on a button click listener, wrapping the
result in a `StreamResource` and then extends the button with `BrowserWindowOpener`.

```java
Button button = new Button();
button.addClickListener( e -> {
    StreamResource res = new StreamResource(createStreamSource(), "file.pdf");
    new BrowserWindowOpener(res).extend(button);
});
```


This way however, as mentioned before, won't open the window on first click; it will only generate 
a resource tha will be opened on second click.
Furthermore the second click will open the resource generated from first click because the 
server side click listener will be invoked after the window is opened. 

The aims of this addon are the following:
 
 * Avoid a target component to be extended more than once by the same extension in 
   order to avoid multiple window opening
 * Have a BrowserWindowOpener version that opens the window at the end of the request
   instead of adding a client side click listener so that you could open immediately a
   runtime generated resource.
   
Opening the window as a result of click listener will not play well wit browsers popup blockers.
The browser will block all `window.open(...)` calls that are not directly triggered by a human interaction; 
if the window is opened on a client side click listener there is no problem but if it is done as a result of 
some async computation (for example an AJAX call) the popup will be blocked.
The addon can be configured to workaround this problem opening a blank window on client click listener
and setting the location after a server side state change has been triggered.

Enhanced Window Opener requires a widgetset ricompilation in order to be used in a Vaadin project; Java 8 is also required.


## Online demo

Try the add-on demo at https://mbf-vaadin-ewo.herokuapp.com/

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, 
go to http://vaadin.com/addon/enhanced-window-opener

## Building and running demo

```
git clone https://github.com/mcollovati/enhanced-window-opener.git
mvn clean install
cd demo
mvn jetty:run
```

To see the demo, navigate to http://localhost:8080/
 

## Issue tracking

The [issues](https://github.com/mcollovati/enhanced-window-opener/issues) for this add-on are tracked on its 
[github.com page](https://github.com/mcollovati/enhanced-window-opener).
All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

# Developer Guide

## Getting started

Here is a simple example on how to try out the add-on component:

Open a window as a result of a server side listener (popup will be blocked by the browser).

```java
EnhancedBrowserWindowOpener opener1 = new EnhancedBrowserWindowOpener();
Button button1 = new Button("Click me");
button1.addClickListener(e -> {
    opener1.open(generateResource());
});
opener1.extend(button1);
```

Open a window for runtime generated content (human interaction, popup will not be blocked)

```java
Link link = new Link("Click me", null);
new EnhancedBrowserWindowOpener()
    .clientSide(true)
    .withGeneratedContent("myFileName.txt", this::makeStreamSource)
    .doExtend(link);
```

Open a window for runtime generated content (blocker workaround, popup will not be blocked)

```java
Button button3 = new Button("Click me");
EnhancedBrowserWindowOpener opener3 = new EnhancedBrowserWindowOpener()
    .popupBlockerWorkaround(true)
    .withGeneratedContent("myFileName.txt", this::makeStreamSource)
    .doExtend(button3);
button3.addClickListener(opener3::open);
```

For a more comprehensive example, see enhanced-window-opener-demo/src/main/java/org/vaadin/addon/ewopener/demo/DemoUI.java

## API

Enhanced Window Opener JavaDoc is available online at https://mbf-vaadindemo.herokuapp.com/docs/enhanced-window-opener/api/
