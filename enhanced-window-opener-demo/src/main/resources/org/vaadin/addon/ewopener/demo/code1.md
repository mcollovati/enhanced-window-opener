## Runtime generated content

Open window with content generated on click; popup blocker workaround activated. 

```java
EnhancedBrowserWindowOpener opener1 = new EnhancedBrowserWindowOpener()
    .popupBlockerWorkaround(true);
Button button1 = new Button("Click me");
button1.addClickListener(e -> {
    opener1.open(generateResource());
});
opener1.extend(button1);
```