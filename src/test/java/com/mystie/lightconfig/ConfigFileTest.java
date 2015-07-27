package com.mystie.lightconfig;

import junit.framework.TestCase;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author memo
 * @version 1.0
 * @since 1.0
 */
public class ConfigFileTest extends TestCase {
    public final String CFG_FOLDER = System.getProperty("user.home") +
            File.separator + ".unittests" + File.separator +
            "light-config" + File.separator;

    private ConfiguredPanel panel;
    private ConfiguredPanel panel2;

    protected void setUp() {
        panel = new ConfiguredPanel(
                CFG_FOLDER + "testConfig.xml");
        panel2 = new ConfiguredPanel(
                CFG_FOLDER + "testConfig2.xml");
    }

    @Override
    protected void tearDown() {
        boolean success = panel.delete();
        success &= panel2.delete();

        File dir = new File(CFG_FOLDER);
        if(dir.exists() && dir.isDirectory()) {
            success &= dir.setWritable(true);
            success &= dir.delete();
        }
        assertTrue(success);
    }

    public ConfigFileTest() {
        super("Configuration variable test");
    }

    /**
     * Test detection of the configuration annotated fields
     */
    public void testNbAnnotatedFields() {
        int i = 0;
        for(Field f : ConfiguredPanel.class.getDeclaredFields()) {
            if(f.isAnnotationPresent(Config.class)) ++i;
        }
        assertEquals(6, i);
    }

    /**
     * Test suppliers of variables for correct values
     */
    public void testConfigSuppliedValues() {
        ConfigVariable cv = panel.getConfiguration().getVar("intVar");
        assertNotNull(cv);
        assertEquals(1, cv.getValue());
        panel.intVar = 2;
        cv.update();
        assertEquals(2, cv.getValue());
        panel.intVar = 1;
        cv.update();

        cv = panel.getConfiguration().getVar("boxedDoubleVar");
        assertNotNull(cv);
        assertEquals(4.2, cv.getValue());
        panel.boxedDoubleVar = 4.3;
        cv.update();
        assertEquals(4.3, cv.getValue());
        panel.boxedDoubleVar = 4.2;
        cv.update();
    }

    /**
     * Test setters for correct update of values
     */
    public void testConfigUpdate() {
        double oldVal = panel.doubleVar;
        panel.setDoubleVar(5.9);
        assertEquals(5.9, panel.doubleVar);
        ConfigVariable cv = panel.getConfiguration().getVar("doubleVar");
        assertEquals(5.9, cv.getValue());
        panel.setDoubleVar(oldVal);
    }

    public void testSave() {
        panel.save();
        File f = panel.getConfiguration().getFile();
        assertTrue(f.exists());
    }

    /**
     * Test loading of configuration
     * Depends on: saving and mass update tests
     */
    public void testLoad() {
        String filePath = CFG_FOLDER + "testLoadConfig.xml";
        panel2 = new ConfiguredPanel(filePath);
        assertFalse(panel2.load());         // No file
        assertTrue(panel2.loadOrSave());    // Create file
        assertTrue(panel2.delete());        // Delete file
        assertFalse(panel2.load());         // No file, again

        // Modify some fields
        double doubleOldVal = panel2.doubleVar;
        double doubleNewVal = 8.7;
        panel2.doubleVar = doubleNewVal;
        Integer boxedIntOldVal = panel2.boxedIntVar;
        Integer boxedIntNewVal = 11;
        panel2.boxedIntVar = boxedIntNewVal;
        String strOldVal = panel2.strVar;
        String strNewVal = "Modified value";
        panel2.strVar = strNewVal;
        panel2.updateAll();
        assertTrue(panel2.save());          // Save file

        // New instance, refer to same configuration file
        ConfiguredPanel panel3 = new ConfiguredPanel(filePath);
        // Assert default fields value
        assertEquals(doubleOldVal, panel3.doubleVar);
        assertEquals(boxedIntOldVal, panel3.boxedIntVar);
        assertEquals(strOldVal, panel3.strVar);
        // Load new values from file
        assertTrue(panel3.load());
        // Assert updated fields value
        assertEquals(doubleNewVal, panel3.doubleVar);
        assertEquals(boxedIntNewVal, panel3.boxedIntVar);
        assertEquals(strNewVal, panel3.strVar);
    }

    /**
     * Test labels existence
     */
    public void testLabels() {
        Configuration cfg = panel.getConfiguration();
        try {
            // Specified label
            String fieldName = "fltLblVal";
            Field f = ConfiguredPanel.class.getDeclaredField(fieldName);
            assertTrue(f.isAnnotationPresent(Config.class));
            Config a = f.getAnnotation(Config.class);
            assertEquals(ConfiguredPanel.lbl, a.value());
            ConfigVariable cv = cfg.getVar(fieldName);
            assertNotNull(cv);
            assertEquals(ConfiguredPanel.lbl, cv.getLabel());

            // Unspecified label
            fieldName = "boxedIntVar";
            f = ConfiguredPanel.class.getDeclaredField(fieldName);
            assertTrue(f.isAnnotationPresent(Config.class));
            a = f.getAnnotation(Config.class);
            assertTrue(a.value().equals(""));
            cv = cfg.getVar(fieldName);
            assertNotNull(cv);
            assertEquals(fieldName, cv.getLabel());

        } catch(NoSuchFieldException e) {
            e.printStackTrace();
            fail("Field name incorrect");
        }
    }

    /**
     * Test mass update of variables all at once
     */
    public void testMassUpdate() {
        int oldIntVar = panel.intVar;
        panel.intVar = 2;
        Integer oldBoxedIntVar = panel.boxedIntVar;
        panel.boxedIntVar = 8;
        String oldStrVar = panel.strVar;
        panel.strVar = "Changed value";

        // Assert no change in the configuration
        Configuration cfg = panel.getConfiguration();
        assertEquals(oldIntVar, cfg.getVar("intVar").getValue());
        assertEquals(oldBoxedIntVar, cfg.getVar("boxedIntVar").getValue());
        assertEquals(oldStrVar, cfg.getVar("strVar").getValue());

        // Push changes and assert they are reflected
        panel.updateAll();
        assertEquals(2, cfg.getVar("intVar").getValue());
        assertEquals(8, cfg.getVar("boxedIntVar").getValue());
        assertEquals("Changed value", cfg.getVar("strVar").getValue());

        // Change back to original
        panel.intVar = oldIntVar;
        panel.boxedIntVar = oldBoxedIntVar;
        panel.strVar = oldStrVar;
        panel.updateAll();
    }

    @SuppressWarnings("unused")
    private class ConfiguredPanel implements Configurable<ConfigFile> {
        public static final String lbl = "A primitive float value";

        @Config public int intVar = 1;
        @Config public double doubleVar = 2.1;
        @Config public Integer boxedIntVar = 3;
        @Config public Double boxedDoubleVar = 4.2;
        @Config public String strVar = "Hello World!";
        @Config(ConfiguredPanel.lbl)
                public float fltLblVal = 3.4f;
        public int notCfgVar = -1;

        private ConfigFile cfg;
        public ConfiguredPanel(String savePath) {
            cfg = new XmlConfigFile(this, savePath);
        }


        @Override public ConfigFile getConfiguration() {
            return cfg;
        }

        public void setDoubleVar(double v) {
            setAndUpdate("doubleVar", v);
        }
    }
}