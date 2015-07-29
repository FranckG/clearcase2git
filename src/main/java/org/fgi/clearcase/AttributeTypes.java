
package org.fgi.clearcase;

import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;

import org.fgi.clearcase.internal.OneShotProcessExecutor;

/**
 * Helper to manage {@link AttributeType}s
 */
public final class AttributeTypes {
  private AttributeTypes() {
    // This is a helper: do not need a constructor
  }

  /**
   * Create a ccucm selector from element name, a vob name and a type (activity:TEST_1@Test_pvob)
   * @param attributeTypeSelector_p
   * @param comment_p
   * @return ccucm selector
   * @throws IOException
   * @throws ClearcaseException
   */
  public static boolean makeAttributeType(final Selector attributeTypeSelector_p, final String comment_p) throws IOException, ClearcaseException {
    if (SystemUtils.IS_OS_WINDOWS) {
      final String[] clearToolCommand = new String[] { "mkattype", //$NON-NLS-1$
                                                      "-c", //$NON-NLS-1$
                                                      comment_p, "-global", //$NON-NLS-1$
                                                      attributeTypeSelector_p.toString() };
      ClearcaseCli.execClearToolCommand(clearToolCommand);
    } else {
      final String[] command = new String[] { "/bin/sh", //$NON-NLS-1$
                                             "-c", //$NON-NLS-1$
                                             ClearcaseCli.CLEARTOOL, "mkattype", //$NON-NLS-1$
                                             "-c", //$NON-NLS-1$
                                             comment_p, "-global", //$NON-NLS-1$
                                             attributeTypeSelector_p.toString() };
      new OneShotProcessExecutor().executeCommand(command);
    }
    return true;
  }
}
