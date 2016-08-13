## Serve static content

Open a static resource (in this case is better use BrowserWindowOpener)

```java
EnhancedBrowserWindowOpener opener3 = new EnhancedBrowserWindowOpener(
    new ClassResource(DemoUI.class, "static.text")
);
Button button3 = new Button("Click me");
button3.addClickListener(e -> {
    opener3.open();
});
opener3.extend(button3);
```        