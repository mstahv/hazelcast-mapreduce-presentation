package com.hazelcast.examples;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.examples.csv.ReaderHelper;
import com.hazelcast.examples.model.SalaryYear;
import com.hazelcast.examples.model.State;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A service class for the UI layer to access Hazelcast cluster.
 * Starts/stops the cluster when the applications starts.
 * <p>
 * Normally you'd want to hide pretty much all backend logic to this kind of facade,
 * but as the purpose of this app is to demonstrate Hazelcast development
 * it most essentially provides a raw HazelcastInstance to the UI layer.
 * </p>
 */
@ApplicationScoped
public class HazelcastService {

    HazelcastInstance hazelcastInstance;

    @PostConstruct
    void init() {

        // Prepare Hazelcast cluster
        hazelcastInstance = buildCluster(2);

        // Read CSV data
        try {
            ReaderHelper.read(hazelcastInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PreDestroy
    void shutdownCluster() {
        // Shutdown cluster
        Hazelcast.shutdownAll();
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    /**
     * @return all states stored into the data crid
     */
    public List<State> getStates() {
        IMap<Integer, State> map = getHazelcastInstance().getMap("states");
        return new ArrayList<>(map.values());
    }

    public List<SalaryYear> getSalaries() {
        return new ArrayList(hazelcastInstance.getMap("salaries").values());
    }

    public List<SalaryYear> getSalaries(String filter) {
        // TODO filter list by key (= email)
        return new ArrayList(hazelcastInstance.getMap("salaries").values());
    }

    /**
     * Saves or replaces salary information
     * @param s the salary data to be stored in the data grid
     */
    public void saveSalary(SalaryYear s) {
        hazelcastInstance.getMap("salaries").put(s.getEmail(), s);
    }

    private static HazelcastInstance buildCluster(int memberCount) {
        Config config = new Config();
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.getJoin().getMulticastConfig().setEnabled(false);
        networkConfig.getJoin().getTcpIpConfig().setEnabled(true);
        networkConfig.getJoin().getTcpIpConfig().setMembers(Arrays.asList(
                new String[]{"127.0.0.1"}));

        HazelcastInstance[] hazelcastInstances = new HazelcastInstance[memberCount];
        for (int i = 0; i < memberCount; i++) {
            hazelcastInstances[i] = Hazelcast.newHazelcastInstance(config);
        }

        return hazelcastInstances[0];
    }

}
