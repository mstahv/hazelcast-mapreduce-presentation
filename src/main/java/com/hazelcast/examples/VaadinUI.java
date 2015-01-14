package com.hazelcast.examples;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.cdi.CDIUI;
import org.vaadin.cdiviewmenu.ViewMenuUI;

@Theme("valo")
@CDIUI("")
@JavaScript("prettify.js")
@Title("MapReduce tutorial")
public class VaadinUI extends ViewMenuUI {

}
