/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.examples;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.IOException;

public abstract class Tutorial extends TabSheet implements View {

    /**
     * This method should contain the real meat of the example.
     *
     * @return the result of the example visualized as a Vaadin component.
     * @throws Exception
     */
    public abstract Component execute() throws Exception;

    /**
     * @return A short description what is done in this code example.
     */
    public abstract String getShortDescription();

    private MVerticalLayout sourceCode = new MVerticalLayout().withCaption(
            "Source code");
    public Panel result = new Panel("Result");
    private MVerticalLayout example = new MVerticalLayout().withCaption(
            "Example").withFullHeight();
    
    private MButton execute = new PrimaryButton("Execute!", this::doExecute);
    private MHorizontalLayout controls = new MHorizontalLayout(execute).withMargin(false);

    public Tutorial() {
        setStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        prepareSourceCode();
        setSizeFull();
        addComponents(example, sourceCode);

        example.add(
                new MHorizontalLayout(controls).expand(
                        new RichText().withMarkDown(
                                "**" + getClass().getSimpleName().replaceAll(
                                        "([0-9]+)", " $1") + ":** *" + getShortDescription() + "*")).
                alignAll(Alignment.MIDDLE_LEFT)
        ).expand(result);

    }

    protected MHorizontalLayout getControls() {
        return controls;
    }

    private final void prepareSourceCode() {
        String source = "/" + getClass().getName().replaceAll("\\.", "/") + ".java";
        try {
            String code = StringUtils.replaceEach(IOUtils.toString(getClass().
                    getResourceAsStream(source)),
                    new String[]{"&", "<", ">", "\"", "'", "/"},
                    new String[]{"&amp;", "&lt;", "&gt;", "&quot;", "&#x27;", "&#x2F;"});
            Label c = new Label("<pre class='prettyprint'>" + code + "</pre>");
            c.setContentMode(ContentMode.HTML);
            c.setSizeUndefined();
            addSelectedTabChangeListener(e -> JavaScript.eval(
                    "setTimeout(function(){prettyPrint();},300);"));
            sourceCode.add(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doExecute(Button.ClickEvent e) {
        try {
            result.setContent(execute());
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        result.setContent(null);
    }


}
