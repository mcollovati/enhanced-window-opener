## Open from menu items

Open window with content generated from menu items; `openerWK` is an opener instance with popup blocker workaround whereas `openerCS` is a client side opener.  

```java
MenuBar menuBar = new MenuBar();
MenuBar.MenuItem subMenu = menuBar.addItem("Sub menu", null);
MenuBar.MenuItem subItem = subMenu.addItem("Download (client side)", aCommandIfYouLike);
MenuBar.MenuItem subItem2 = subMenu.addItem("Download (server side)", selectedItem -> openerWK.open());

openerWK.doExtend(subItem2);
openerCS.doExtend(subItem);
```