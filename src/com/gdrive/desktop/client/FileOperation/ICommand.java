package com.gdrive.desktop.client.FileOperation;

/**
 * Base class for Command implementation 
 * @author harsh
 *
 */
public abstract class ICommand
{
  protected Object mResult = null;
  protected Boolean mStatus = true;
  protected Exception mException = null;
  protected String mCommandType = "NONE";

  /**
   * <p>Use to execute Command Which don't provide post listener(post Execute callback)
   * or if you dont register your post listener and still want to know execption</p>
 * @throws Exception
 */
public void doExcute() throws  Exception
  {
    try
    {
      preExecute();

      execute();

    }
    catch (Exception e)
    {	
    	mStatus = false;
    	mException = e;
      e.printStackTrace();
      throw e;
    }
    finally {
    	postExecute();
    }
  }

  /**
 * 
 */
public void doExcute1()
  {
    try
    {
      preExecute();

      execute();

    }
    catch (Exception e)
    {	
    	mStatus = false;
    	mException = e;
      e.printStackTrace();
    }
    finally {
    	postExecute();
    }
  }
  public abstract Boolean isExecutable();

  protected abstract int preExecute();

  protected abstract int execute()
    throws Exception;

  protected abstract int postExecute();

  public Object getResult()
  {
    return this.mResult;
  }
  public String getCommandType(){return mCommandType;}
}