/**
 * SwingWorker3.java 0.6
 */
package javax.swing;

/**
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on and examples of using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 *
 * @author Sun
 * @version 3
 */
public abstract class SwingWorker3 {
  private Object value;

  /**
   * <p>Class to maintain reference to current worker thread
   * under separate synchronization control.</p>
   */
  private static class ThreadVar {
    private Thread thread;
    ThreadVar(Thread t) {
      thread = t;
    }

    synchronized Thread get() {
      return thread;
    }

    synchronized void clear() {
      thread = null;
    }
  }

  private ThreadVar threadVar;

  /**
   * <p>Get the value produced by the worker thread, or null if it
   * hasn't been constructed yet.</p>
   *
   * @return Object
   */
  protected synchronized Object getValue() {
    return value;
  }

  /**
   * <p>Set the value produced by worker thread</p>
   *
   * @param x Object
   */
  private synchronized void setValue(Object x) {
    value = x;
  }

  /**
   * <p>Compute the value to be returned by the <code>get</code> method.</p>
   *
   * @return Object
   */
  public abstract Object construct();

  /**
   * <p>Called on the event dispatching thread (not on the worker thread)
   * after the <code>construct</code> method has returned.</p>
   */
  public void finished() {
  }

  /**
   * <p>A new method that interrupts the worker thread.  Call this method
   * to force the worker to stop what it's doing.</p>
   */
  public void interrupt() {
    Thread t = threadVar.get();
    if (t != null) {
      t.interrupt();
    }
    threadVar.clear();
  }

  /**
   * <p>Return the value created by the <code>construct</code> method.
   * Returns null if either the constructing thread or the current
   * thread was interrupted before a value was produced.</p>
   *
   * @return the value created by the <code>construct</code> method
   */
  public Object get() {
    while (true) {
      Thread t = threadVar.get();
      if (t == null) {
        return getValue();
      }
      try {
        t.join();
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // propagate
        return null;
      }
    }
  }


  /**
   * <p>Start a thread that will call the <code>construct</code> method
   * and then exit.</p>
   */
  public SwingWorker3() {
    final Runnable doFinished = new Runnable() {
      public void run() {
        finished();
      }
    };

    Runnable doConstruct = new Runnable() {
      public void run() {
        try {
          setValue(construct());
        }
        finally {
          threadVar.clear();
        }

        SwingUtilities.invokeLater(doFinished);
      }
    };

    Thread t = new Thread(doConstruct);
    threadVar = new ThreadVar(t);
  }

  /**
   * <p>Start the worker thread.</p>
   */
  public void start() {
    Thread t = threadVar.get();
    if (t != null) {
      t.start();
    }
  }
}
