package com.hazelcast.examples;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.examples.model.Person;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.inject.Inject;

@CDIView("")
@ViewMenuItem(title = "Welcome",order = ViewMenuItem.BEGINNING)
public class WelcomeView extends MVerticalLayout implements View {

    HazelcastInstance hazelcastInstance;

    @Inject
    public void setService(HazelcastService service) {
        hazelcastInstance = service.getHazelcastInstance();
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        removeAllComponents();
        add(new RichText().withMarkDownResource("/welcome.md"));

        IList<Person> list = hazelcastInstance.getList("persons");

        add(new RichText().withMarkDown("*"+list.size() + "* Person instances in the Hazelcast data grid, distributed on *" + hazelcastInstance.getCluster().getMembers().size() + "* nodes."));

    }

}