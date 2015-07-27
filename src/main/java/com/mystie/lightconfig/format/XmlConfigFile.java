package com.mystie.lightconfig.format;

import com.mystie.lightconfig.*;
import com.mystie.lightconfig.annotation.Config;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Implementation of a configuration file using a hash-map with string key
 * for data holding as an xml file
 *
 * @author		Samuel Longchamps
 * @version		1.0
 * @since		1.0
 */
public class XmlConfigFile extends MapConfiguration
        implements ConfigFile
{
    @XStreamOmitField private static XStream xstrm = new XStream();
    @XStreamOmitField private File saveFile;

    static {
        xstrm.processAnnotations(XmlConfigFile.class);
    }

    /**
     * Omit a field from a specific class
     * @param cls class owning the field
     * @param name name of the field to be omitted
     */
    private synchronized static void omit(Class cls, String name) {
        xstrm.omitField(cls, name);
    }

    /**
     * Omit a collection of fields from classes
     * @param clsNamePairs collection of pairs
     */
    private static void omit(Collection<Pair<Class, String>> clsNamePairs) {
        for(Pair<Class, String> cnp : clsNamePairs)
            omit(cnp.getKey(), cnp.getValue());
    }

    /**
     * Constructor for a configuration file bound to a configurable instance
     * defining certain config fields.
     *
     * @param owner configurable object by which the configuration file is
     *              owned. Must not be null.
     * @param filePath full path to the xml file being used as save file
     */
    public XmlConfigFile(Configurable<ConfigFile> owner,
                         String filePath) {
        super(owner);
        omit(getOmissions());
        setFile(new File(filePath));
        omit(owner.getOmissions());
        adaptFrom(owner);
    }

    /**
     * Adapt the configuration file from a configurable instance, using its
     * annotated fields to create {@link ConfigVariable} instances.<br>
     * Note that because a configurable instance knows not of the persistence
     * mechanism used by the configuration implementation, the save file is not
     * populated in this method.
     *
     * @param adaptedObj configurable instance to adapt from
     */
    private void adaptFrom(Configurable adaptedObj) {
        for(Field f : adaptedObj.getClass().getDeclaredFields()) {
            if(f.isAnnotationPresent(Config.class)) {
                f.setAccessible(true);
                Config an = f.getAnnotation(Config.class);
                String key = f.getName();
                String lbl = an.value().isEmpty() ? f.getName() : an.value();

                try {
                    ConfigVariable cfgVar = new ConfigVariable(
                            f.get(adaptedObj).getClass(), lbl,
                            ()->{
                                try {
                                    return f.get(adaptedObj);
                                } catch(IllegalAccessException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                    );
                    addVar(key, cfgVar);
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Save the configuration file as an xml file
     *
     * @return true if file was saved, false otherwise
     */
    @Override
    public boolean save()
    {
        try {
            File oFile = getFile();
            if(!oFile.isFile()) {
                boolean success = true;
                File oParentDir = oFile.getParentFile();
                if(!oParentDir.isDirectory())
                    success = oParentDir.mkdirs();
                success &= oFile.createNewFile();

                if(!success)
                    return false;
            }
            FileOutputStream stream = new FileOutputStream(oFile);
            xstrm.toXML(this, stream);
            stream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean load() {
        File f = getFile();
		if(f.isFile() && f.canRead()) {
            try {
				FileInputStream stream = new FileInputStream(f);
				ConfigFile loadedConfig = ConfigFile.class.cast(
						xstrm.fromXML(stream));
				stream.close();

				copyFrom(loadedConfig);
				resetObservers();
				setFile(f);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return false;
    }

    @Override
    public boolean delete() {
        File f = getFile();
        return !f.exists() || (f.isFile() && f.canWrite() && f.delete());
    }

	@Override
	public boolean copyFrom(Configuration cfg) {
		if(cfg == null) return false;
        Configurable owner = getOwner();

        for(String key : getKeys()) {
            ConfigVariable cvSrc = cfg.getVar(key);
            ConfigVariable cvDst = getVar(key);
            if(cvSrc != null && cvDst != null) {
                try {
                    Field f = owner.getClass().getDeclaredField(key);
                    f.setAccessible(true);
                    if(f.isAnnotationPresent(Config.class)) {
                        f.set(owner, cvSrc.getValue());
                        cvDst.update();
                    }
                } catch(NoSuchFieldException | IllegalAccessException ignored) {}
            }
        }
		return true;
	}

	@Override
	public File getFile()
	{
		return saveFile;
	}

	@Override
	public void setFile(File saveFile) {
		this.saveFile = saveFile;
	}
}
