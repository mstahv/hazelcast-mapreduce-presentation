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

package com.hazelcast.examples.tutorials;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.examples.HazelcastService;
import com.hazelcast.examples.Tutorial;
import com.hazelcast.examples.model.SalaryYear;
import com.hazelcast.mapreduce.aggregation.Aggregations;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import org.vaadin.viritin.label.Header;

import javax.inject.Inject;

@CDIView
public class Tutorial7
        extends Tutorial {

    @Inject
    HazelcastService service;

    @Override
    public Component execute()
            throws Exception {

        HazelcastInstance hazelcastInstance = service.getHazelcastInstance();

        IMap<String, SalaryYear> map = hazelcastInstance.getMap("salaries");
        Supplier<String, SalaryYear, Integer> supplier = Supplier.all(SalaryYear::getAnnualSalary);

        int sum = map.aggregate(supplier, Aggregations.integerSum());
        return new Header("Salary sum: " + sum);
    }

    @Override
    public String getShortDescription() {
        return "Salary sum";
    }
}
