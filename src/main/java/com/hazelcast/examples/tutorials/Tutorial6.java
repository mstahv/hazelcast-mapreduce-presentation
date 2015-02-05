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
import com.hazelcast.examples.tutorials.impl.SalarySumCombinerFactory;
import com.hazelcast.examples.tutorials.impl.SalarySumMapper;
import com.hazelcast.examples.tutorials.impl.SalarySumReducerFactory;
import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import org.vaadin.viritin.label.Header;

import javax.inject.Inject;
import java.util.Map;

@CDIView
public class Tutorial6
        extends Tutorial {

    @Inject
    HazelcastService service;

    @Override
    public Component execute()
            throws Exception {

        HazelcastInstance hazelcastInstance = service.getHazelcastInstance();

        JobTracker jobTracker = hazelcastInstance.getJobTracker("default");

        IMap<String, SalaryYear> map = hazelcastInstance.getMap("salaries");
        KeyValueSource<String, SalaryYear> source = KeyValueSource.fromMap(map);

        Job<String, SalaryYear> job = jobTracker.newJob(source);

        JobCompletableFuture<Integer> future = job //
                .mapper(new SalarySumMapper()) //
                .combiner(new SalarySumCombinerFactory()) //
                .reducer(new SalarySumReducerFactory()) //
                .submit(new SalarySumCollator());

        return new Header(future.get().toString());
    }

    @Override
    public String getShortDescription() {
        return "Salary sum";
    }

    private static class SalarySumCollator
            implements Collator<Map.Entry<String, Integer>, Integer> {

        @Override
        public Integer collate(Iterable<Map.Entry<String, Integer>> values) {
            int sum = 0;
            for (Map.Entry<String, Integer> value : values) {
                sum += value.getValue();
            }
            return sum;
        }
    }
}
