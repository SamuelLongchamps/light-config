package com.mystie.lightconfig;

import javafx.util.Pair;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Vector;

/**
 * Interface for specifying means of managing configured variables
 *
 * @author memo
 * @version 1.0
 * @since 1.0
 */
public interface Configurable<T extends Configuration> {
    /**
     * @return configuration of the configurable instance
     */
    T getConfiguration();

    /**
     * @return list of string representing field names to be omitted in saving
     */
    default Collection<Pair<Class, String>> getOmissions() {
        return new Vector<>();
    }


    default void setAndUpdate(String varStr, Object val) {
        try {
            Field f = getClass().getField(varStr);
            if(!f.get(this).equals(val)) {
                f.set(this, val);
                ConfigVariable cv = getConfiguration().getVar(varStr);
                assert(cv != null);
                cv.update();
                cv.notifyObservers();
            }
        } catch(IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update all the configuration variables and optionally notify their
     * observers if a change of value was detected.<br>
     * See {@link #updateAll()} for more usage information.
     *
     * @param notify true to notify their observers, false otherwise
     * @see #updateAll()
     */
    default void updateAll(boolean notify) {
        Configuration cfg = getConfiguration();
        for(ConfigVariable v : cfg.getVars()) {
            v.update();
            if(notify) v.notifyObservers();
        }
    }

    /**
     * Update all the configuration variables and notify their observers if a
     * change of value was detected. <br>
     * Use this method to push a mass changes made to fields annotated with
     * {@link Config} and avoid the overhead of the update of a large number of
     * fields through their usual setter methods.
     */
    default void updateAll() {
        updateAll(true);
    }

    /**
     * Delegate method for saving the configuration owned by the implementor.
     *
     * @return true if saved successfully, false otherwise.
     */
    default boolean save() {
        return getConfiguration().save();
    }

    /**
     * Delegate method for loading the configuration owned by the implementor.
     *
     * @return true if loaded successfully, false otherwise.
     */
    default boolean load() {
        return getConfiguration().load();
    }

    /**
     * Delegate method for loading or saving the configuration owned by the
     * implementor.
     *
     * @return true if loaded or saved successfully, false otherwise.
     */
    default boolean loadOrSave() {
        return getConfiguration().loadOrSave();
    }

    /**
     * Delegate method for deleting the configuration owned by the
     * implementor.
     *
     * @return true if deleted successfully, false otherwise.
     */
    default boolean delete() {
        return getConfiguration().delete();
    }
}
