package com.mystie.lightconfig;

import javafx.util.Pair;

import java.util.*;

/**
 * Implementation of key-value pair mapping feature of a configuration using a
 * hash-map.
 *
 * @author Samuel Longchamps
 * @version 1.0
 * @since 1.0
 */
public abstract class MapConfiguration implements Configuration {
    private Map<String, ConfigVariable> configVars = new HashMap<>();
    private Configurable owner;

    public MapConfiguration(Configurable owner) {
        this.owner = owner;
    }

    @Override
    public Configurable getOwner() {
        return owner;
    }

    @Override
    public void addVar(String key, ConfigVariable val) {
        configVars.put(key, val);
    }

    @Override
    public ConfigVariable removeVar(String key) {
        return configVars.remove(key);
    }

    @Override
    public ConfigVariable getVar(String key)
    {
        return configVars.get(key);
    }

    @Override
    public Collection<ConfigVariable> getVars() {
        return configVars.values();
    }

    @Override
    public Set<String> getKeys()
    {
        return configVars.keySet();
    }


    @Override
    public void resetObservers() {
        for(ConfigVariable var: configVars.values())
            var.deleteObservers();
    }

    @Override
    public void observeAll(Observer obs) {
        for(ConfigVariable var: configVars.values())
            var.addObserver(obs);
    }

    public Collection<Pair<Class, String>> getOmissions() {
        Collection<Pair<Class, String>> lst = Configuration.super.getOmissions();
        lst.add(new Pair<>(MapConfiguration.class, "owner"));
        return lst;
    }
}
