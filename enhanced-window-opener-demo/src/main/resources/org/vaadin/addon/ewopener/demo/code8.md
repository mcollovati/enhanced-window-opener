## Extend and remove

If the extended component could be attached and detached multiple times
remove the extension on detach to avoid having windows magically opening 
on every attach

```java
class MyPopupContent extends MVerticalLayout {

    MButton button = new MButton("Open window");
    Registration openerClickRegistration;

    public MyPopupContent() {
        add(new MLabel("My component"),button);
    }

    // Extend the button and register the click listener
    // A `Registration` reference is stored to remove the listener on detach
    @Override
    public void attach() {
        super.attach();
        EnhancedBrowserWindowOpener opener = EnhancedBrowserWindowOpener.extendOnce(button);
        openerClickRegistration = button.addClickListener(e2 -> opener.open(streamContent()));
    }

    // Remove the extension from button and also remove the click listener
    // to avoid to open multiple windowss 
    @Override
    public void detach() {
        EnhancedBrowserWindowOpener.extendOnce(button).remove();
        openerClickRegistration.remove();
        super.detach();
    }

    private StreamResource streamContent() {
        // Generate content ...
    }
}
```
