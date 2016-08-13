In this page you can find some example of usafe of EnhancedWindowOpener extension.
Clicking on some links you will notice that the popup window will be blocked by
the browser; this happens because browsers will block all `window.open(...)`
calls that are not directly triggered by a human interaction.

There is a workaround to open the window without having it blocked by browser
popup blocker. 
You should invoke `window.open(...)` in a click listener
without specify an URL storing the reference to the opened window;
then you can set the correct url on window reference object through
the `location.href` property.

Some example of the following examples enables this workaround on the 
EnhancedWindowOpener instance.