package com.mystie.lightconfig;

import java.io.File;

/**
 * @author memo
 * @version 1.0
 * @since 1.0
 */
public interface ConfigFile extends Configuration {
    /**
     * @return file to which configuration is saved
     */
    File getFile();

    /**
     * @param saveFile file to linked to the configuration
     */
    void setFile(File saveFile);
}
