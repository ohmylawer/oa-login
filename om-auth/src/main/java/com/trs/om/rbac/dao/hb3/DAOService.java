/**
 * 
 */
package com.trs.om.rbac.dao.hb3;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

import com.trs.om.rbac.dao.IDAOAccessor;
import com.trs.om.rbac.dao.IDAOService;

/**
 * @author Administrator
 *
 */
public class DAOService implements IDAOService {

	/**
	 * 
	 */
	private final static Logger logger = Logger.getLogger(DAOService.class);
	/**
	 * 
	 */
	private static final String[] accessorClasses={
		"com.trs.om.rbac.dao.hb3.PermissionAccessor",
		"com.trs.om.rbac.dao.hb3.PrivilegeAccessor",
		"com.trs.om.rbac.dao.hb3.RoleAccessor",
		"com.trs.om.rbac.dao.hb3.SessionAccessor"};
	
    /** 
     * Location of hibernate.cfg.xml file.
     * Location should be on the classpath as Hibernate uses  
     * #resourceAsStream style lookup for its configuration file. 
     * The default classpath location of the hibernate config file is 
     * in the default package. Use #setConfigFile() to update 
     * the location of the configuration file for the current session.   
     */
    private static String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";
	private Configuration configuration = new Configuration();    
    private org.hibernate.SessionFactory sessionFactory;
    private String configFile = CONFIG_FILE_LOCATION;
	
	/**
	 * 
	 */
	private Map accessors = null;
	/* (non-Javadoc)
	 * @see com.trs.om.rbac.dao.IDAOService#getAccessor(java.lang.String)
	 */
	public IDAOAccessor getAccessor(String accessorName) {
		return (IDAOAccessor)accessors.get(accessorName.toLowerCase());
	}
	
	/**
	 * 
	 */
	public void start(Properties properties){
		startHibernateContext(properties);
		startAccessorContext();
	}

	/**
	 * 
	 */
	private void startAccessorContext() {
		accessors = new HashMap();
		for ( int i = 0 ; i < accessorClasses.length ; i++ ){
			IHb3DAOAccessor accessor = (IHb3DAOAccessor)initAccessor(accessorClasses[i]);
			if ( accessor == null ) {
				logger.error("accessor initialized failed.(accessorClass)="+accessorClasses[i]);
				continue;
			}
			//
			accessor.setSessionFactory(getSessionFactory());
			accessors.put(accessor.getAccessorName().toLowerCase(), accessor);
		}
	}

	/**
	 * 
	 * @param accessorClass
	 * @return
	 */
	private IDAOAccessor initAccessor(String accessorClass){
		try {
			return (IDAOAccessor)Class.forName(accessorClass).newInstance();
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 
	 */
	public void stop() {
		accessors.clear();
		accessors = null;
		//
		getSessionFactory().close();
	}

	/**
	 * 
	 */
	private void startHibernateContext(Properties properties){
    	try {
			configuration.configure(configFile);
			if (properties != null){
				for ( Enumeration e = properties.keys(); e.hasMoreElements() ; ){
					String key = (String)e.nextElement();
					if ( key.startsWith("hibernate.") ){
						configuration.setProperty(key, properties.getProperty(key));
					}
				}
			}
			sessionFactory = configuration.buildSessionFactory();
		} catch (Exception e) {
			System.err
					.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}
	/**
     *  return session factory
     *
     */
	public org.hibernate.SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/**
     *  return session factory
     *
     *	session factory will be rebuilded in the next call
     */
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
		sessionFactory = null;
	}

	/**
     *  return hibernate configuration
     *
     */
	public Configuration getConfiguration() {
		return configuration;
	}
}