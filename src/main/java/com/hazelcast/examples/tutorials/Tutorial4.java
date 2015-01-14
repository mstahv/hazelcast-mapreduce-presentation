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
import com.hazelcast.examples.tutorials.impl.SalaryCombinerFactory;
import com.hazelcast.examples.tutorials.impl.SalaryMapper;
import com.hazelcast.examples.tutorials.impl.SalaryReducerFactory;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;

import javax.inject.Inject;
import java.util.Map;
import java.util.Map.Entry;

@CDIView
public class Tutorial4 extends Tutorial {

    HazelcastInstance hazelcastInstance;
    private HazelcastService service;

    @Inject
    public void setService(HazelcastService service) {
        this.service = service;
        hazelcastInstance = service.getHazelcastInstance();
    }

    @Override
    public Component execute() throws Exception {
        JobTracker jobTracker = hazelcastInstance.getJobTracker(
                "default");

        IList<Person> list = hazelcastInstance.getList("persons");
        KeyValueSource<String, Person> source = KeyValueSource.fromList(list);

        Job<String, Person> job = jobTracker.newJob(source);

        JobCompletableFuture<Map<String, Integer>> future
                = job.mapper(new SalaryMapper()) //
                .combiner(new SalaryCombinerFactory()) //
                .reducer(new SalaryReducerFactory()) //
                .submit();

        return wrapAsBarChart(future.get());
    }

    public Chart wrapAsBarChart(Map<String, Integer> result) {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.getConfiguration().getChart().setBackgroundColor(new SolidColor(0,
                0, 0, 0));
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getyAxis().setTitle("Salary per state");
        chart.getConfiguration().getxAxis().setType(AxisType.CATEGORY);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().getLabels().setRotation(-45);
        DataSeries ds = new DataSeries();
        for(Entry<String,Integer> e : result.entrySet()) {
            ds.add(new DataSeriesItem(e.getKey(), e.getValue()));
        }
        chart.getConfiguration().addSeries(ds);
        chart.getConfiguration().getLegend().setEnabled(false);
        return chart;
    }

    @Override
    public String getShortDescription() {
        return "Average salary in various states.";
    }
}
