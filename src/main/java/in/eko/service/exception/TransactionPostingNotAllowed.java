package in.eko.service.exception;

public class TransactionPostingNotAllowed extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public TransactionPostingNotAllowed(String cause){
		super(cause);
	}

}
