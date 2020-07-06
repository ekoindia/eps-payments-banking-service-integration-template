/*
 * 
 */
package in.eko.service.hibernate;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateDataAccess {

	private static Logger logger = Logger.getLogger(HibernateDataAccess.class);
	private static SessionFactory sessionFactory = null;
	public static final ThreadLocal<SessionHolder> threadLocal = new ThreadLocal<SessionHolder>();

	public static void buildSessionFactory(final String cfgFile) {
		
		String filePath = "/etc/db_config/service_provider/service_template_db.properties";
		try {
			File propertiesPath = new File(filePath);
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure()
					.loadProperties(propertiesPath).build();
			HibernateDataAccess.sessionFactory = new Configuration().buildSessionFactory(serviceRegistry);
		} catch (Exception ex) {
			HibernateDataAccess.logger.fatal("Initial SessionFactory creation failed: " + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}

	public static void setThreadLocal(final SessionHolder holder) {
		HibernateDataAccess.threadLocal.set(holder);
	}

	public static SessionFactory getSessionFactory() {
		return HibernateDataAccess.sessionFactory;
	}

	public static SessionHolder getOrCreateSessionHolder() throws HibernateException {
		HibernateDataAccess.logger.debug("getOrcreatedSessionHolder called ");
		SessionHolder sessionHolder = null;
		if (HibernateDataAccess.threadLocal.get() == null) {
			SessionFactory factory = HibernateDataAccess.sessionFactory;
			if (factory == null) {
				HibernateDataAccess.logger.error("Invalid Key requested in DataAccess");
				throw new HibernateException("Invalid Key asking for Session.");
			}
			sessionHolder = new SessionHolder(factory.openSession());
			sessionHolder.getSession().setFlushMode(FlushMode.COMMIT);
			HibernateDataAccess.setThreadLocal(sessionHolder);
		} else {
			sessionHolder = HibernateDataAccess.threadLocal.get();
			sessionHolder.getSession().setFlushMode(FlushMode.COMMIT);
		}
		return sessionHolder;
	}

	public static Session getSessionTL() {
		HibernateDataAccess.getOrCreateSessionHolder();
		return HibernateDataAccess.threadLocal.get().getSession();
	}

	private static SessionHolder getSessionHolder() {
		if (null == HibernateDataAccess.threadLocal.get()) {
			HibernateDataAccess.logger.warn("getSessionHolder invoken when threadlocal was null");
		}
		return HibernateDataAccess.threadLocal.get();
	}

	public static Transaction startTransaction() {
		return HibernateDataAccess.getSessionHolder().startTransaction();
	}

	public static Transaction getTransaction() {
		if (HibernateDataAccess.getSessionHolder() == null) {
			return null;
		}
		return HibernateDataAccess.getSessionHolder().getTransaction();
	}

	public static boolean isSessionOpen() {
		if (HibernateDataAccess.getSessionHolder() == null) {
			return false;
		}
		Session session = HibernateDataAccess.getSessionHolder().getSession();
		if (session == null || !session.isOpen()) {
			return false;
		}
		return true;
	}

	public static void closeSession() {
		HibernateDataAccess.logger.debug("Closing current TL session from flushandClosesession");
		SessionHolder sessionHolder = HibernateDataAccess.getSessionHolder();
		if (sessionHolder != null) {
			Session session = sessionHolder.getSession();
			session.close();
			session = null;
			HibernateDataAccess.threadLocal.set(null);
		}
	}

	public static void closeSessionTL() {
		SessionHolder sessionHolder = HibernateDataAccess.getSessionHolder();
		if (sessionHolder != null) {
			Session session = sessionHolder.getSession();
			if (session.isOpen()) {
				session.close();
			}
			session = null;
			HibernateDataAccess.threadLocal.set(null);
		}
	}

	public static void flushSession() {
		HibernateDataAccess.logger.debug("Flush current TL session");
		SessionHolder sessionHolder = HibernateDataAccess.getSessionHolder();
		if (sessionHolder != null) {
			Session session = sessionHolder.getSession();
			session.flush();
		}
	}

	public static void commitTransaction() {
		if (HibernateDataAccess.getTransaction() != null) {
			HibernateDataAccess.getTransaction().commit();
			HibernateDataAccess.getSessionHolder().setTranasction(null);
		} else {
			throw new RuntimeException("Trying to commit null transaction.");
		}
	}

	public static void rollbackTransaction() {
		if (HibernateDataAccess.getTransaction() != null) {
			HibernateDataAccess.getTransaction().rollback();
			HibernateDataAccess.getSessionHolder().setTranasction(null);
		} else {
			throw new RuntimeException("Trying to rollback null transaction.");
		}
	}

	public void save(final Object object) throws HibernateException {
		try {
			HibernateDataAccess.getSessionTL();
			if (HibernateDataAccess.getTransaction() == null) {
				HibernateDataAccess.startTransaction();
			}
			HibernateDataAccess.threadLocal.get().getSession().saveOrUpdate(object);
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void delete(final Object object) throws HibernateException {
		try {
			HibernateDataAccess.getSessionTL();
			if (HibernateDataAccess.getTransaction() == null) {
				HibernateDataAccess.startTransaction();
			}
			HibernateDataAccess.threadLocal.get().getSession().delete(object);
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Object load(final Class objectClass, final Integer id) throws HibernateException {
		try {
			HibernateDataAccess.getSessionTL();
			if (HibernateDataAccess.getTransaction() == null) {
				HibernateDataAccess.startTransaction();
			}
			return HibernateDataAccess.threadLocal.get().getSession().load(objectClass, id);
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Object load(final Class objectClass, final String id) throws HibernateException {
		try {
			HibernateDataAccess.getSessionTL();
			if (HibernateDataAccess.getTransaction() == null) {
				HibernateDataAccess.startTransaction();
			}
			return HibernateDataAccess.threadLocal.get().getSession().load(objectClass, id);
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static List execNamedQuery(final String queryName, final Map queryParameters, final String key) {
		Query query = HibernateDataAccess.getSessionTL().getNamedQuery(queryName);
		return HibernateDataAccess.execQuery(query, queryParameters, key);
	}

	public static List execQuery(final Query query, final Map queryParameters, final String key) {
		if (query != null) {
			try {
				HibernateDataAccess.getSessionTL();
				HibernateDataAccess.setParametersInQuery(query, queryParameters);
				return query.list();
			} catch (HibernateException he) {
				he.printStackTrace();
			}
		}
		return null;
	}

	public static Object execUniqueResultNamedQuery(final String queryName, final Map queryParameters) {
		try {
			Query query = HibernateDataAccess.getSessionTL().getNamedQuery(queryName);
			HibernateDataAccess.setParametersInQuery(query, queryParameters);
			if (null != query) {
				return query.uniqueResult();
			}
		} catch (HibernateException he) {
			he.printStackTrace();
		}
		return null;
	}

	public static void setParametersInQuery(final Query query, final Map queryParameters) throws HibernateException {
		if (queryParameters != null) {
			Set queryParamKeys = queryParameters.keySet();
			Iterator queryParamIterator = queryParamKeys.iterator();
			while (queryParamIterator.hasNext()) {
				String key = queryParamIterator.next().toString();
				if (queryParameters.get(key) instanceof Collection) {
					query.setParameterList(key, (Collection) queryParameters.get(key));
				} else {
					query.setParameter(key, queryParameters.get(key));
				}
			}
		}
	}

	/*public void saveAndCommitObject(final Object object) {

		HibernateDataAccess.save(object);
		HibernateDataAccess.commitTransaction();
		HibernateDataAccess.getSessionTL();
		HibernateDataAccess.startTransaction();

	}*/
	public void commit(){
		HibernateDataAccess.commitTransaction();
		HibernateDataAccess.getSessionTL();
		HibernateDataAccess.startTransaction();
	}
	
}
