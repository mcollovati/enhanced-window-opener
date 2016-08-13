## Generated content helper

Simplify opening window for generated content

```java
Button button3 = new Button("Click me");
EnhancedBrowserWindowOpener opener3 = new EnhancedBrowserWindowOpener()
    .withGeneratedContent("myFileName.txt", this::makeStreamSource)
    .doExtend(button3);
button3.addClickListener(opener3::open);
```
