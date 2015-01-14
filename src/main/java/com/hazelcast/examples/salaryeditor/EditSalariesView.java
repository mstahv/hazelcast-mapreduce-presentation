package com.hazelcast.examples.salaryeditor;

import com.hazelcast.examples.HazelcastService;
import com.hazelcast.examples.model.SalaryYear;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Window;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@CDIView
public class EditSalariesView extends MVerticalLayout implements View {

    @Inject
    HazelcastService service;

    @Inject
    SalaryYearForm form;

    MTable<SalaryYear> table = new MTable<>(SalaryYear.class)
            .withProperties("email", "year")
            .withColumnHeaders("E-mail", "year");
    RichText headerText = new RichText().withMarkDown("# Edit salaries\n " +
            "This is a simple editor for the salary data.\n");

    @PostConstruct
    void init() {
        table.setBeans(service.getSalaries());
        add(headerText, table);

        table.addMValueChangeListener(e -> editSalary(e.getValue()));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private void editSalary(SalaryYear value) {
        if(value != null) {
            form.setEntity(value);
            Window w = form.openInModalPopup();
            w.setWidth("80%");
            w.setHeight("80%");
            form.setSavedHandler(s -> {
                service.saveSalary(s);
                w.close();
            });
            form.setResetHandler(s -> {
                w.close();
            });
        }
    }
}
