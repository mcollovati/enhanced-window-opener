## Runtime generated content

Open window with content generated on click. 

```java
EnhancedBrowserWindowOpener opener = new EnhancedBrowserWindowOpener();
Button button1 = new Button("Generated content");
button1.addClickListener(e -> {
    opener.open(generateResource());
});
opener.extend(button1);
```