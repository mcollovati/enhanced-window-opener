## Extend once 

Avoid multiple extensions on single component.

```java
Button button2 = new Button("Extended only once");
button2.addClickListener(e -> {
    EnhancedBrowserWindowOpener.extendOnce(button2)
        .open(generateResource());
});
```        