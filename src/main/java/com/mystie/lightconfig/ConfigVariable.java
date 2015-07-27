package com.mystie.lightconfig;

import java.util.Observable;
import java.util.function.Supplier;

/**
 * Configuration variable which is stored in a configuration object
 *
 * @author		Samuel Longchamps
 * @version		1.0
 * @since		1.0
 */
public class ConfigVariable extends Observable
{
    private final Class<?> type;
	private final String label;
	private Supplier<Object> supplier;
    private Object value;

	/**
	 * Construct a configuration variable
	 *
     * @param type type of the object value
	 * @param label string representation of the variable (not its value)
	 * @param supplier supplier supplier for the variable
     */
	public ConfigVariable(Class<?> type, String label,
                          Supplier<Object> supplier) {
		super();
        this.type = type;
		this.label = label;
        this.supplier = supplier;
        update();
	}

	/**
	 * @return type of the variable
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @return variable value
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return value object
	 */
	public Object getValue() {
		return value;
	}

    private Supplier<Object> getSupplier() {
        return supplier;
    }

    public void update() {
        value = supplier.get();
    }

    /**
     * Duplicate the config variable as a new instance.
     *
     * @return duplicated instance of the current instance
     * @throws NullPointerException if the supplier is null
     */
	public ConfigVariable duplicate() throws NullPointerException {
        if(getSupplier() == null)
            throw new NullPointerException(
                    "Supplier is null and therefore cannot be used to " +
                    "duplicate the instance!");

		return new ConfigVariable(
				getType(),
				getLabel(),
				getSupplier()
        );
	}
}
