## Extend once 

Avoid multiple extensions on single component.
 
Only a window per click will be opened; browser may block popup.

```java
Button button2 = new Button("Extended only once");
button2.addClickListener(e -> {
    EnhancedBrowserWindowOpener.extendOnce(button2)
        .open(generateResource());
});
```        