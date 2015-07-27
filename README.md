# LightConfig
LightConfig is a lightweight and easy to use java library for configuration data persistence

## Supported platform
Java 8, distributed as jar library

## How to use it?
By using special annotations on objects to be persisted, a class implementing the 
Configurable interface and specifying a backend (such as an XML configuration file) can 
use save() and load() methods which will respectively write the file or load an existing one.

### Example
```java
public propertiesPanel implements Configurable<ConfigFile> {
    ConfigFile cfg = new XmlConfigFile(this, "~/cfgPnl.xml");
    @Override public ConfigFile getConfiguration() { return cfg; }
    
    @Config int cfgVar;
    
    public void getCfgVar() { cfgVar; }
    public void setVar(int v) { setAndUpdate("cfgVar", v); }
}
...
propertiesPanelInstance.save();
propertiesPanelInstance.load();
propertiesPanelInstance.loadOrSave();
```

## How does it work?
Annotations allow the implementor of the Configuration interface to retrieve which 
fields are to be persisted. Using reflection, field names are retrieved and are 
used as keys in a map, where the value is a decorator class ConfigVariable allows 
to store any object and which other parts of the software can observe for changes. 
The implementor provides a way to save and load the map.

It is important to note that the ConfigVariable doesn't own any data, but rather 
is provided a supplier which is used to update its value object reference. 
All modifications to the annotated objects can be reflected to the configuration 
variables either directly by using a special setter setAndUpdate() or by modifying 
the objects directly and then pushing the changes for all of them using updateAll().