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
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.examples.HazelcastService;
import com.hazelcast.examples.Tutorial;
import com.hazelcast.examples.model.Crime;
import com.hazelcast.examples.model.CrimeCategory;
import com.hazelcast.examples.model.Person;
import com.hazelcast.examples.tutorials.impl.CrimeMapper;
import com.hazelcast.examples.tutorials.impl.CrimeReducerFactory;
import com.hazelcast.examples.tutorials.impl.SalaryCollator;
import com.hazelcast.examples.tutorials.impl.SalaryCombinerFactory;
import com.hazelcast.examples.tutorials.impl.SalaryMapper;
import com.hazelcast.examples.tutorials.impl.SalaryReducerFactory;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@CDIView
public class Tutorial5
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

        Job<String, Person> job = jobTracker.newJob(source);

        ICompletableFuture<List<Map.Entry<String, Integer>>> future = job //
                .mapper(new SalaryMapper()) //
                .combiner(new SalaryCombinerFactory()) //
                .reducer(new SalaryReducerFactory()) //
                .submit(new SalaryCollator());

        // Intermediate result
        List<Map.Entry<String, Integer>> orderedSalariesByState = future.get();
        Map.Entry<String, Integer> topSalary = orderedSalariesByState.get(0);

        IList<Crime> crimesList = hazelcastInstance.getList("crimes");
        KeyValueSource<String, Crime> crimeSource = KeyValueSource.fromList(crimesList);

        Job<String, Crime> crimeJob = jobTracker.newJob(crimeSource);

        ICompletableFuture<Map<CrimeCategory, Integer>> crimeFuture = //
                crimeJob.mapper(new CrimeMapper(topSalary.getKey())) //
                        .reducer(new CrimeReducerFactory()) //
                        .submit();

        Map<CrimeCategory, Integer> result = crimeFuture.get();
        return wrapAsBarChart(result);
    }

    public Chart wrapAsBarChart(Map<CrimeCategory, Integer> result) {
        Chart chart = new Chart(ChartType.BAR);
        chart.getConfiguration().getChart().setBackgroundColor(new SolidColor(0, 0, 0, 0));
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getyAxis().setTitle("Crimes per year");
        chart.getConfiguration().getxAxis().getLabels().setEnabled(false);
        for (Map.Entry<CrimeCategory, Integer> entry : result.entrySet()) {
            chart.getConfiguration().addSeries(new ListSeries(entry.getKey().toString(), entry.getValue()));
        }
        return chart;
    }

    @Override
    public String getShortDescription() {
        return "Crimes in the best earning state? TODO Should we create a UI to choose the state from a list ordered by salaries?";
    }
}
