# LightConfig ![light-config-icon-small](https://cloud.githubusercontent.com/assets/5256911/8899875/e3bcda9e-3407-11e5-984f-ddec45542118.png)
LightConfig is a lightweight and easy to use java library for configuration data persistence

### Supported platform
Java 8, distributed as jar library

### Status
Alpha test, still experimental. Use at your own risks.

## How to use it?
By using special annotations on objects to be persisted, a class implementing the 
Configurable interface and specifying a backend (such as an XML configuration file) can 
use save() and load() methods which will respectively write the file or load an existing one.

### Example
Setup the configurable class and annotate fields to be persisted using @Config.
```java
public propertiesPanel implements Configurable<ConfigFile> {
    ConfigFile cfg = new XmlConfigFile(this, "~/cfgPnl.xml");
    @Override public ConfigFile getConfiguration() { return cfg; }
    
    @Config int cfgVar;
    
    public void getCfgVar() { return cfgVar; }
    public void setCfgVar(int v) { setAndUpdate("cfgVar", v); }
}
```
Use an instance of the class to save and load the annotated variables values.
```java
propertiesPanelInstance.save();
propertiesPanelInstance.load();
propertiesPanelInstance.loadOrSave();
```

## Building
The project uses Maven for packaging, additionally providing both source and javadoc.
Simply run the following command from the project root directory:
```
mvn package
```