## Push open

Open a window from server (Please, don't try this to at home).

You will see a window pop up 20 seconds after page has loaded;
 
you must be running on localhost or allow popup for this site.

```java
EnhancedBrowserWindowOpener opener4 = new EnhancedBrowserWindowOpener(
    new ClassResource(DemoUI.class, "static.text")
);
CssLayout hiddenComponent = new CssLayout();
hiddenComponent.withVisible(false);
opener4.extend(hiddenComponent);
CompletableFuture.runAsync(this::doSomeLongProcessing)
    .thenRun( () -> getUI().access(opener4::open));
```        