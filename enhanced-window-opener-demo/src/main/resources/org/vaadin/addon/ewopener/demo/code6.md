## Customize generate content

Customize StreamResource attributes if you need

```java
Link link2 = new Link("Click me", null);
new EnhancedBrowserWindowOpener()
    .clientSide(true)
    .withGeneratedContent("myFileName.txt", this::makeStreamSource,
        resource -> {
            resource.setCacheTime(0);
            resource.setFilename("runtimeFileName-" + 
                Instant.now().getEpochSecond() + ".txt");
        })
    .doExtend(link2);
```        