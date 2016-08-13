## Generated content with client side click listener

Add a click listener on client side but make your content
generated on download request.

```java
Link link = new Link("Click me", null);
new EnhancedBrowserWindowOpener()
    .clientSide(true)
    .withGeneratedContent("myFileName.txt", this::makeStreamSource)
    .doExtend(link);
```
