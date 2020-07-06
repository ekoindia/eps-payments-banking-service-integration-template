/*
 * 
 */
package in.eko.service.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class SessionHolder {
	private final Session session;
	private Transaction transaction = null;

	public SessionHolder(final Session session)
	{
		this.session = session;
		if (session == null)
		{
			throw new NullPointerException("session is required");
		}
	}

	public Transaction startTransaction()
		{
			if (this.transaction == null)
			{
				this.transaction = this.session.beginTransaction();
			}
			return this.transaction;
		}

	public void setTranasction(final Transaction transaction)
		{
			this.transaction = transaction;
		}

	public Transaction getTransaction()
		{
			return this.transaction;
		}

	public Session getSession()
		{
			return this.session;
		}

	public void close()
		{
		}
}
