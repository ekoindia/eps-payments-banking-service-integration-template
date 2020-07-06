package in.eko.service.hibernate;

import org.hibernate.cfg.Configuration;

public class HibernateInitializer {

	private static Configuration config = null;

	/**
	 * This method is called by ApplicationInilializer class, this will Create
	 * the hibernate configuration object from the defined hibernate mapping
	 * files and build Hibernate session factory.
	 */
	public static Configuration initialize(String hibernateConfigPath) {
		config = new Configuration();
		config.configure(hibernateConfigPath);
		return config;
	}

	public static Configuration getConfiguration() {
		return config;
	}

}
