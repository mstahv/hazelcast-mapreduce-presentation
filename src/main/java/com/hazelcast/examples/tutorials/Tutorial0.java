/*
 * Copyright (c) 2008-2014, Hazelcast, Inc. All Rights Reserved.
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
import com.hazelcast.core.IMap;
import com.hazelcast.examples.HazelcastService;
import com.hazelcast.examples.Tutorial;
import com.hazelcast.examples.tutorials.impl.wordcount.TokenizerMapper;
import com.hazelcast.examples.tutorials.impl.wordcount.WordcountCombinerFactory;
import com.hazelcast.examples.tutorials.impl.wordcount.WordcountReducerFactory;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;

import javax.inject.Inject;
import java.util.Map;

@CDIView
public class Tutorial0
        extends Tutorial {

    @Inject
    private HazelcastService service;

    @Override
    public Component execute()
            throws Exception {

        HazelcastInstance instance = service.getHazelcastInstance();

        JobTracker tracker = instance.getJobTracker("default");

        IMap<String, String> map = instance.getMap("articles");

        KeyValueSource<String, String> source = KeyValueSource.fromMap(map);

        Job<String, String> job = tracker.newJob(source);

        ICompletableFuture<Map<String, Integer>> future = job //
                .mapper(new TokenizerMapper()) //
                .combiner(new WordcountCombinerFactory()) //
                .reducer(new WordcountReducerFactory()).submit();

        return getChart(future.get());
    }

    @Override
    public String getShortDescription() {
        return "Wordcount - Hello World of MapReduce";
    }

    protected Component getChart(Map<String, Integer> frequencies) {
        Chart chart = new Chart(ChartType.PIE);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("Browser market shares at a specific website, 2010");

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setCursor(Cursor.POINTER);
        Labels dataLabels = new Labels();
        dataLabels.setEnabled(true);
        dataLabels.setColor(SolidColor.BLACK);
        dataLabels.setConnectorColor(SolidColor.BLACK);
        dataLabels.setFormatter("''+ this.point.name +': '+ this.percentage +' %'");
        plotOptions.setDataLabels(dataLabels);
        conf.setPlotOptions(plotOptions);
        final DataSeries series = new DataSeries();

        for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
            series.add(new DataSeriesItem(entry.getKey(), entry.getValue()));
        }

        conf.setSeries(series);

        chart.drawChart();
        return chart;
    }
}
