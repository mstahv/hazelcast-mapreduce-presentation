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
import com.hazelcast.examples.model.Person;
import com.hazelcast.examples.tutorials.impl.PersonMapper;
import com.hazelcast.examples.tutorials.impl.Utils;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@CDIView
public class Tutorial1
        extends Tutorial {

    @Inject
    private HazelcastService service;

    @Override
    public Component execute() {
        HazelcastInstance hazelcastInstance = service.getHazelcastInstance();

        JobTracker jobTracker = hazelcastInstance.getJobTracker("default");

        IList<Person> list = hazelcastInstance.getList("persons");

        KeyValueSource<String, Person> source = KeyValueSource.fromList(list);

        Job<String, Person> job = jobTracker.newJob(source);

        // Find all people named James
        ICompletableFuture<Map<String, List<Person>>> future = job.mapper(new PersonMapper("James")).submit();

        try {
            Map<String, List<Person>> stringListMap = future.get();
            List<Person> persons = stringListMap.entrySet().iterator().next().
                    getValue();
            return Utils.listInTable(persons);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getShortDescription() {
        return "A very simple example finding all persons with firstName 'James'.";
    }
}
