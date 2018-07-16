## Open UI with shortcut

Open window for another UI handling shortcut directly on client side (no need to register a shortcut listenere into the UI) 
 

```java
Button button = new Button("Open another UI (client side)");
EnhancedBrowserWindowOpener opener = new EnhancedBrowserWindowOpener(AboutUI.class)
    .clientSide(true)
    .withShortcut(ShortcutAction.KeyCode.NUM3, ShortcutAction.ModifierKey.CTRL, ShortcutAction.ModifierKey.ALT)
    .doExtend(button);
```