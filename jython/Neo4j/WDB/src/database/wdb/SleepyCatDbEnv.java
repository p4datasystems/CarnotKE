/*
 * Created on Feb 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package wdb;

import java.io.File;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * @author Bo Li
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SleepyCatDbEnv {
	private Environment environment = null;
	private Database database = null;
	private Database classDB = null;
	private StoredClassCatalog classCatalog = null;
	
	public SleepyCatDbEnv()
	{
	}
	
	public void openEnv(File envHome, boolean readOnly) throws DatabaseException
	{
		//Setup the environment
		EnvironmentConfig envConfig = new EnvironmentConfig();
	    envConfig.setAllowCreate(!readOnly);

	    environment = new Environment(envHome, envConfig);
	    
	    //Setup the classcatalog
	    DatabaseConfig dbConfig = new DatabaseConfig();
	    dbConfig.setAllowCreate(!readOnly);
	    
	    classDB  = environment.openDatabase(null, "ClassCatalog", dbConfig); 

	    // Instantiate the class catalog
	    classCatalog = new StoredClassCatalog(classDB);
	}
	
	public void openDb(String name, boolean readOnly) throws DatabaseException
	{
	    DatabaseConfig dbConfig = new DatabaseConfig();
	    dbConfig.setAllowCreate(!readOnly);
	    database = environment.openDatabase(null, name, dbConfig); 
	}
	
	public void closeDb(String name) throws DatabaseException
	{
		if (database != null) {
			database.close();
			database = null;
		}
	}

	public void closeEnv() throws DatabaseException
	{
	    if (database != null) {
            database.close();
        }
        
        if (classDB != null) {
        	classDB.close();
        }

        if (environment != null) {
            environment.close();
        }
	}
	
	/**
	 * @return Returns the classCatalog.
	 */
	public StoredClassCatalog getClassCatalog() {
		return classCatalog;
	}
	/**
	 * @return Returns the database.
	 */
	public Database getDatabase() {
		return database;
	}
	/**
	 * @return Returns the environment.
	 */
	public Environment getEnvironment() {
		return environment;
	}
}
