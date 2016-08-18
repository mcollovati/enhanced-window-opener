## Automatically close window

Automatically close popup if there is not content to open.

```java
EnhancedBrowserWindowOpener opener4 = new EnhancedBrowserWindowOpener()
    .popupBlockerWorkaround(true);
Button button4 = new Button("Nothing to open here");
button4.addClickListener(e -> {
    opener4.open((Resource)null);
});
opener4.extend(button4);
```        