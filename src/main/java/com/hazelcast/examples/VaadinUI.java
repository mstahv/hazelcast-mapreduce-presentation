package com.hazelcast.examples;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.cdi.CDIUI;
import com.vaadin.server.Extension;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import org.vaadin.cdiviewmenu.ViewMenuUI;

@Theme("valo")
@CDIUI("")
@JavaScript("prettify.js")
@Title("MapReduce tutorial")
public class VaadinUI extends ViewMenuUI {

    @Override
    public void beforeClientResponse(boolean initial) {
        if (initial && Page.getCurrent().getWebBrowser().getBrowserApplication().
                contains("Firefox")) {
            // Responsive, FF, cross site is currently broken :-(
            Extension r = null;
            for (Extension ext : getExtensions()) {
                if (ext instanceof Responsive) {
                    r = ext;
                }
            }
            removeExtension(r);
        }
        super.beforeClientResponse(initial);
    }

}
