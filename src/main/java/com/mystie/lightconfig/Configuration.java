package com.mystie.lightconfig;

import javafx.util.Pair;

import java.util.*;

/**
 * Interface to load, save and keep various configuration settings and variables
 *
 * @author		Samuel Longchamps
 * @version		1.0
 * @since		1.0
 */
public interface Configuration
{
    /**
     * @return configurable which owns the configuration
     */
    Configurable<?> getOwner();

	/**
	 * Add an object linked to the passed key string
	 *
	 * @param key string key linked to the value
	 * @param val object linked to the key
	 */
	void addVar(String key, ConfigVariable val);

	/**
	 * Removes a key-object pair object linked to the passed key string
	 *
	 * @param key string key linked to the value
	 * @return variable linked to the key, null if the key is not found
	 */
	ConfigVariable removeVar(String key);

	/**
	 * @param key string key linked to the value
	 * @return variable linked to the key, null if the key is not found
	 */
	ConfigVariable getVar(String key);

    /**
     * @return collection of a all configuration variables
     */
    Collection<ConfigVariable> getVars();

	/**
	 * Get a set of keys to which variables currently held by the configuration
	 * object are bound
	 *
	 * @return set of keys bound to configuration's variables
	 */
	Set<String> getKeys();

	/**
	 * Copy configuration variables from another configuration object, given
	 * that they are of the same type.
	 *
	 * @param oConfig configuration object to copy variables from
	 * @return true if the copy was successful, false otherwise
	 */
	boolean copyFrom(Configuration oConfig);

	/**
	 * Reset observers for all the variables to none.
	 */
	void resetObservers();

	/**
	 * Set observer object argument as observer for all the variables
	 *
	 * @param obs observer to be notified by all variables
	 */
	void observeAll(Observer obs);

    /**
     * @return list of string representing field names to be omitted in saving
     */
    default Collection<Pair<Class, String>> getOmissions() {
        Collection<Pair<Class, String>> lst = new Vector<>();
        lst.add(new Pair<>(Observable.class, "obs"));
        lst.add(new Pair<>(Observable.class, "changed"));
        lst.add(new Pair<>(ConfigVariable.class, "supplier"));
        lst.add(new Pair<>(ConfigVariable.class, "key"));
        return lst;
    }

    /**
     * Save the configuration in order to persist its data
     *
     * @return true if saving was successful, false otherwise
     */
    boolean save();

    /**
     * Load the configuration from persisted data
     * Note that observers cannot be persisted and therefore observers will need
     * to register after this method returns.
     *
     * @return true if loading was successful, false otherwise
     */
    boolean load();

    /**
     * Load the configuration from persisted data, if it cannot be loaded,
     * save the current state.
     *
     * @return true if loading or creation was successful, false otherwise
     * @see #load()
     * @see #save()
     */
    default boolean loadOrSave() {
        return load() || save();
    }

    /**
     * Delete the persistence information if any exists
     *
     * @return true if deleted successfully or if persistence information
     * didn't exist, false otherwise
     */
    boolean delete();
}
