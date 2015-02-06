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
import com.hazelcast.examples.model.State;
import com.hazelcast.examples.tutorials.impl.StateBasedMapper;
import com.hazelcast.examples.tutorials.impl.Utils;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.vaadin.cdi.CDIView;
import com.vaadin.ui.Component;
import org.vaadin.viritin.fields.TypedSelect;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@CDIView
public class Tutorial2
        extends Tutorial {

    @Inject
    private HazelcastService service;

    private TypedSelect<State> stateSelect;

    @Override
    public Component execute()
            throws Exception {

        HazelcastInstance hazelcastInstance = service.getHazelcastInstance();

        JobTracker jobTracker = hazelcastInstance.getJobTracker("default");

        IList<Person> list = hazelcastInstance.getList("persons");

        KeyValueSource<String, Person> source = KeyValueSource.fromList(list);

        Job<String, Person> job = jobTracker.newJob(source);

        // Find all people grouped by state
        JobCompletableFuture<Map<String, List<Person>>> future = job //
                .mapper(new StateBasedMapper(getSelectedState())) //
                .submit();

        List<Person> resultList = future.get().get(getSelectedState());
        return Utils.listInTable(resultList).withProperties("firstName", "lastName", "state");
    }

    @PostConstruct
    void init() {
        final List<State> states = service.getStates();
        stateSelect = new TypedSelect<>(State.class) //
                .setNullSelectionAllowed(false).setCaptionGenerator(State::getName).setOptions(states);

        stateSelect.selectFirst();
        getControls().addComponentAsFirst(stateSelect);
    }

    private String getSelectedState() {
        return stateSelect.getValue().getAbbreviation();
    }

    @Override
    public String getShortDescription() {
        return "Finds all people grouped by state and then filter them for only specific state.";
    }
}
