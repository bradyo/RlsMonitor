package monitor;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.apache.commons.configuration.*;

/*
 * This program monitors the RLS disection data and experiment keys for changes.
 * When changes are detected, the program regenerates reports and updates any
 * downstream analysis data.
 */
public class Main
{  
    /**
     * time in milli-seconds between polling
     */
    public static final int SLEEP_INTERVAL = 1000;
    
    /**
     * Set up a monitor and poll file system continuously.
     */
    public static void main(String[] args) throws Exception {
        // get global application configuration
        XMLConfiguration config = null;
        try {
            String jarPath = (new File(".")).getAbsolutePath();
            config = new XMLConfiguration(jarPath 
                    + File.separator + "config"
                    + File.separator + "application.xml");
        }
        catch (ConfigurationException ex) {
            System.err.println("could not open config file: " + ex.getMessage());
            return;
        }
        
        // set up monitor data sources
        Collection<DataSource> dataSources = new ArrayList();
        List dataSourceConfigs = config.configurationsAt("data.dataSource");
        for(Iterator it = dataSourceConfigs.iterator(); it.hasNext();) {
            HierarchicalConfiguration node = (HierarchicalConfiguration) it.next();
            String facility = node.getString("facility");
            File folder = new File(node.getString("folder"));
            DataSource ds = new DataSource(facility, folder);
            dataSources.add(ds);
        }
        
        // set up datbase connection
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String host = config.getString("database.host");
            String db = config.getString("database.name");
            String username = config.getString("database.username");
            String password = config.getString("database.password");
            String url = "jdbc:mysql://" + host + "/" + db;
            connection = DriverManager.getConnection(url, username, password);
        }
        catch (Exception ex) {
            System.err.println("could not connect to database: " + ex.getMessage());
            return;
        }
        
        // set up template folder        
        File templateFolder = new File(config.getString("reports.templatesFolder"));
        
        // set up report folder
        File reportFolder = new File(config.getString("reports.folder"));
        
        // set up core service
        String host = config.getString("coreService.host");
        String apiKey = config.getString("coreService.apiKey");
        CoreService coreService = new CoreService(host, apiKey); 
        
        // create monitor
        Monitor monitor = new Monitor(dataSources, connection, templateFolder, reportFolder, coreService);
        
        // run continuously, checking for changes
        while (true) {
            monitor.update();

            // garbage collect to prevent memory leaks
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            runtime.freeMemory();
            
            // delay between polling
            try {
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                System.err.println("timer interrupted: " + e.getMessage());
            }
        }
    }
}
