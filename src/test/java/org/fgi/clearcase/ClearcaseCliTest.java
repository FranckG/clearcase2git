
package org.fgi.clearcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class ClearcaseCliTest {

  private static String _stgLoc = "orchestra_viewstore"; //$NON-NLS-1$
  private static String _viewTag = "jenkins_OcmsUnitTests"; //$NON-NLS-1$

  /**
   * @throws IOException
   * @throws ClearcaseException
   */
  @Test
  public void testBaselineGetDependencies() throws IOException, ClearcaseException {
    final Baseline baseline = new Baseline(new Selector("baseline:tempo-1.0.0.201408281034@\\ChangeMgt_pvob")); //$NON-NLS-1$
    final List<Baseline> dependencies = baseline.getDependencies();
    System.out.println(baseline.getSelector() + " dependencies:"); //$NON-NLS-1$
    for (final Baseline dependency : dependencies) {
      System.out.println("\t" + dependency.getSelector()); //$NON-NLS-1$
    }
    assertEquals("Error getting baseline dependency!", 4, dependencies.size()); //$NON-NLS-1$
  }

  /**
   * Test getVersions with spaces in file names
   */
  @Test
  public void testGetVersions() {
    Path dirPath = Views.getViewRoot().resolve(_viewTag);
    Path documentPath = dirPath;
    dirPath = dirPath.resolve("Test_comp/Test/confmgt"); //$NON-NLS-1$

    documentPath = documentPath.resolve("Test_comp/Test/confmgt/New Text Document(2).txt"); //$NON-NLS-1$

    final String[] testPaths = new String[] { dirPath.toString(), documentPath.toString() };
    final String[] result = ClearcaseHelper.getVersions(testPaths);
    for (int i = 0; i < testPaths.length; i++) {
      System.out.println(testPaths[i] + ": " + result[i]); //$NON-NLS-1$
    }

    assertEquals(testPaths.length, result.length);
  }

  /**
   * @throws IOException
   * @throws ClearcaseException
   */
  @Test
  public void testViewExist() throws IOException, ClearcaseException {
    final boolean result = Views.exist(_viewTag);
    assertTrue("View " + _viewTag + " does not exist!", result); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Removes activity in Clearcase
   * @param activity_p
   * @return cleartool output
   * @throws ClearcaseException
   * @throws IOException
   */
  public static String rmActivity(final Activity activity_p) throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "rmactivity", "-force", activity_p.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$
    final String result = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    return result;
  }

  /**
   * @throws ClearcaseException
   * @throws IOException
   */
  @BeforeClass
  public static void setUpBeforeClass() throws IOException, ClearcaseException {
    // Create view
    Views.makeDynamicView(_viewTag, _stgLoc);
  }

  @AfterClass
  public static void tearDown() throws IOException, ClearcaseException {
    // Delete view
    Views.remove(new View(_viewTag));
  }
}
