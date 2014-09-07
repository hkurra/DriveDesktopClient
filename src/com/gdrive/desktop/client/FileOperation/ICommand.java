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
public void DoExcute() throws  Exception
  {
    try
    {
      PreExecute();

      Execute();

    }
    catch (Exception e)
    {	
    	mStatus = false;
    	mException = e;
      e.printStackTrace();
      throw e;
    }
    finally {
    	PostExecute();
    }
  }

  /**
 * 
 */
public void DoExcute1()
  {
    try
    {
      PreExecute();

      Execute();

    }
    catch (Exception e)
    {	
    	mStatus = false;
    	mException = e;
      e.printStackTrace();
    }
    finally {
    	PostExecute();
    }
  }
  public abstract Boolean IsExecutable();

  protected abstract int PreExecute();

  protected abstract int Execute()
    throws Exception;

  protected abstract int PostExecute();

  public Object GetResult()
  {
    return this.mResult;
  }
  public String GetCommandType(){return mCommandType;}
}