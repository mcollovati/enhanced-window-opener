## Push open

Open a window from server (Please, don't do this to your users :) ).
You will see a window pop up after 10 seconds.

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