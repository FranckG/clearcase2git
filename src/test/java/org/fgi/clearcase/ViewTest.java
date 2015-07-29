
package org.fgi.clearcase;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class ViewTest {

  private static String _viewTag = "jenkins_OcmsUnitTests"; //$NON-NLS-1$

  @Test
  public void testGetViewRoot() {
    assertEquals(Views.getViewRoot(), Paths.get("M:\\")); //$NON-NLS-1$
  }

  /**
   * @return the _viewTag
   */
  public static String getViewTag() {
    return _viewTag;
  }
}
