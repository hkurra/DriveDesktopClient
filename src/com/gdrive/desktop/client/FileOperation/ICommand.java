package com.gdrive.desktop.client.FileOperation;

public abstract class ICommand
{
  protected Object Result = null;

  public void DoExcute() throws  Exception
  {
    try
    {
      PreExecute();

      Execute();

      PostExecute();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
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