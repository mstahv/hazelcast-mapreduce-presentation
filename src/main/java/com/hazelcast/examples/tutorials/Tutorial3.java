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
import com.hazelcast.core.IList;
import com.hazelcast.examples.HazelcastService;
import com.hazelcast.examples.Tutorial;
import com.hazelcast.examples.model.Person;
import com.hazelcast.examples.tutorials.impl.CountCombinerFactory;
import com.hazelcast.examples.tutorials.impl.CountReducerFactory;
import com.hazelcast.examples.tutorials.impl.StateBasedCountMapper;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.inject.Inject;
import java.util.Map;

@CDIView
public class Tutorial3
        extends Tutorial {

    @Inject
    private HazelcastService service;

    @Override
    public Component execute()
            throws Exception {

        HazelcastInstance hazelcastInstance = service.getHazelcastInstance();

        JobTracker jobTracker = hazelcastInstance.getJobTracker("default");

        IList<Person> list = hazelcastInstance.getList("persons");
        KeyValueSource<String, Person> source = KeyValueSource.fromList(list);

        // Count people by state
        JobCompletableFuture<Map<String, Integer>> countByState = jobTracker.newJob(source) //
                .mapper(new StateBasedCountMapper()) //
                .reducer(new CountReducerFactory())    //
                .submit();

        // Same as above but with precalculation per node
        JobCompletableFuture<Map<String, Integer>> countByStateWithPrecalculation = jobTracker.newJob(source) //
                .mapper(new StateBasedCountMapper()) //
                .combiner(new CountCombinerFactory()) //
                .reducer(new CountReducerFactory()) //
                .submit();

        return visualizeResults(countByState.get(), countByStateWithPrecalculation.get());
    }

    private Component visualizeResults(Map<String, Integer> countByState, Map<String, Integer> withPrecalculation) {
        Chart chart = wrapAsBarChart(countByState);
        chart.setCaption("Count by State");
        Chart chartWithPrecalculation = wrapAsBarChart(withPrecalculation);
        chartWithPrecalculation.setCaption("With precalculation");
        return new MVerticalLayout(chart, chartWithPrecalculation);
    }

    public Chart wrapAsBarChart(Map<String, Integer> result) {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.getConfiguration().getChart().setBackgroundColor(new SolidColor(0, 0, 0, 0));
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getyAxis().setTitle("Persons per state");
        chart.getConfiguration().getxAxis().getLabels().setEnabled(false);
        result.entrySet().stream().
                forEach((entry) -> {
                    chart.getConfiguration().addSeries(new ListSeries(entry.getKey(), entry.getValue()));
                });
        return chart;
    }

    @Override
    public String getShortDescription() {
        return "Count people by state";
    }
}
