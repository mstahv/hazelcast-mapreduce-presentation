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

package com.hazelcast.examples.tutorials.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.examples.model.Person;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import org.vaadin.viritin.fields.MTable;

import java.util.List;

public class Utils {

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static MTable<Person> listInTable(List<Person> persons) {
        return new MTable<>(persons).withFullWidth().withFullHeight().
                withProperties("firstName", "lastName", "city", "email");
    }

    private Utils() {
    }

    public static String toString(Object value) {
        return gson.toJson(value);
    }

    public static Label toLabel(Object value) {
        Label label = new Label(toString(value));
        label.setContentMode(ContentMode.PREFORMATTED);
        return label;
    }

}
