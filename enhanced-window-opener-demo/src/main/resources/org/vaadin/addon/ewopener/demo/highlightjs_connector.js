window.org_vaadin_addon_ewopener_demo_HighlightJS = function() {
    var me = this;
    me.onStateChange = function() {
        if (hljs) {
            me.getElement(me.getParentId()).querySelectorAll("pre code").forEach(hljs.highlightBlock);
        }
    };

};
