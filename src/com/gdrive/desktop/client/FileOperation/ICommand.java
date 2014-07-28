package com.gdrive.desktop.client.FileOperation;

/**
 * Base class for Command implementation 
 * @author harsh
 *
 */
public abstract class ICommand
{
  protected Object Result = null;
  protected Boolean status = true;
  protected Exception exception = null;

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
    	status = false;
    	exception = e;
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
    	status = false;
    	exception = e;
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

  public Object getResult()
  {
    return this.Result;
  }
}