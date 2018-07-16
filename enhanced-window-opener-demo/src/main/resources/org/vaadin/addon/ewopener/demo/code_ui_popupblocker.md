## Open UI with shortcut 

Open window for another UI; popup blocker workaround activated.
Open action could also be triggered with a shortcut. 

```java
Button button = new Button("Open another UI (popup workaround)");


// Shortcut action will run on server side
ShortcutListener shortcutListener = new ShortcutListener("", 
    ShortcutAction.KeyCode.NUM1, new int[]{
    ShortcutAction.ModifierKey.CTRL, ShortcutAction.ModifierKey.ALT
}) {
            
    @Override
    public void handleAction(Object sender, Object target) {
        button10.click()
    }
};
getActionManager().addAction(shortcutListener);

EnhancedBrowserWindowOpener opener = new EnhancedBrowserWindowOpener(AboutUI.class)
    .popupBlockerWorkaround(true)
    .withShortcut(shortcutListener2)
    .doExtend(button);
button.addClickListener(ev -> opener7.open());
```