package com.hazelcast.examples.salaryeditor;

import com.hazelcast.examples.model.SalaryMonth;
import com.hazelcast.examples.model.SalaryYear;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.fields.ElementCollectionField;
import org.vaadin.viritin.fields.EnumSelect;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

public class SalaryYearForm extends AbstractForm<SalaryYear> {

    TextField email = new MTextField("email");

    TextField year = new MTextField("year");

    ElementCollectionField<SalaryMonth> months = new ElementCollectionField<>(
            SalaryMonth.class, SalaryMonthEditor.class);

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        email,
                        year,
                        months
                ).withMargin(false),
                getToolbar()
        );
    }

    public static class SalaryMonthEditor {

        EnumSelect month = new EnumSelect().withNullSelection(false);
        MTextField salary = new MTextField();
    }
}
