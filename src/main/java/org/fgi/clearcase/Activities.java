
package org.fgi.clearcase;

import java.io.IOException;

/**
 * Static helper to manage {@link Activity} objects
 */
public class Activities {
  private Activities() {
    // this is a helper: do not need a constructor
  }

  /**
   * remove given activity in Clearcase 
   * @param activity_p
   * @throws IOException
   * @throws ClearcaseException
   */
  public static void remove(final Activity activity_p) throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("rmactivity", "-nc", "-force", activity_p.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
}
