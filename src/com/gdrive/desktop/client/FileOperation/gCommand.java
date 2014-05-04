package com.gdrive.desktop.client.FileOperation;
/**
 *  
 * @author harsh
 * 
 * <p>Base class for all file related operation</p> 
 */

public abstract class gCommand {

	/**
	 * Result of command 
	 */
	protected Object Result = null;
	/**
	 * user only have to call this to execute there command 
	 */
	public void DoExcute(){
		try {
			PreExecute();
			//always throw your exception in each command's execute method after taking necessary step
			//otherwise postexecute will execute 
			Execute();
			//if execute throw exception in that case post execute will not execute
			PostExecute();
		}
		catch (Exception e) {
			
			e.printStackTrace();
		}
		finally {
			
		}
		
		
	}
	
	/**
	 * command is executable or not
	 * user must call this before executing there command 
	 * @return bool 
	 */
	public abstract Boolean IsExecutable() ;
	/**
	 * @return err
	 */
	protected abstract int PreExecute();
	/**
	 * @return
	 * @throws Exception
	 */
	protected abstract int Execute() throws Exception ;
	/**
	 * @return
	 */
	protected abstract int PostExecute();

	/**
	 * @return the result
	 */
	public Object getResult() {
		return Result;
	}
}
