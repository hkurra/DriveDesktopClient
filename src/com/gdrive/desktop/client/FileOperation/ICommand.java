package com.gdrive.desktop.client.FileOperation;

public abstract class ICommand
{
  protected Object Result = null;

  public void DoExcute()
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