package org.fgi.clearcase;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.fgi.clearcase.internal.CleartoolKeepAliveExecutor;
import org.fgi.clearcase.internal.CleartoolOneShotExecutor;
import org.fgi.clearcase.internal.IClearToolExecutor;

/**
 * ClearcaseCli manages only extended form for CCUCM objects
 */
public class ClearcaseCli {

  /**
   * Keep Alive executor
   */
  public static CleartoolKeepAliveExecutor __clearToolKeepAliveExecutor;

  /**
   * String used for a checked out path
   */
  public static final String CHECKEDOUT = "<checkedout file>"; //$NON-NLS-1$

  /**
   * cleartool command
   */
  public static final String CLEARTOOL = "cleartool"; //$NON-NLS-1$

  /**
   * String used for an invalid path
   */
  public static final String INVALIDPATH = "<invalid path>"; //$NON-NLS-1$

  /**
   * In a project vob, this is the root folder
   */
  public static final String ROOTFOLDER = "RootFolder"; //$NON-NLS-1$

  /**
   * String used for a private file
   */
  public static final String VIEWPRIVATEFILE = "<view private file>"; //$NON-NLS-1$

  // utility class
  private ClearcaseCli() {
  }

  /**
   * Change the working directory and execute the ClearTool command given in the String array argument. ClearTool executable name ("cleartool") must not be
   * given in the command.
   * @param workingdir the working directory to switch to, can be <code>null</code> or empty if no switch is needed
   * @param args the cleartool command
   * @return the command result as a List of Strings.
   * @throws IOException
   * @throws ClearcaseException
   */
  public static List<String> execClearToolCommand(final Path workingdir, final String... args) throws IOException, ClearcaseException {
    IClearToolExecutor clearToolExecutor = null;
    if (null != __clearToolKeepAliveExecutor) {
      clearToolExecutor = __clearToolKeepAliveExecutor;
    } else {
      clearToolExecutor = new CleartoolOneShotExecutor();
    }
    return clearToolExecutor.executeClearToolCommand(args, workingdir);
  }

  /**
   * Execute the ClearTool command given in the args argument. ClearTool executable name ("cleartool") must not be given in the command.
   * @param args the cleartool command
   * @return the command result as a List of Strings.
   * @throws IOException
   * @throws ClearcaseException
   */
  public static List<String> execClearToolCommand(final String... args) throws IOException, ClearcaseException {
    return execClearToolCommand(null, args);
  }

  /**
   * Change the working directory and execute the ClearTool command given in the String array argument. ClearTool executable name ("cleartool") must not be
   * given in the command.
   * @param workingdir the working directory to switch to, can be <code>null</code> or empty if no switch is needed
   * @param args the cleartool command
   * @return the command result as a String. Elements are separated by "\n".
   * @throws IOException
   * @throws ClearcaseException
   */
  public static String execClearToolCommandString(final Path workingdir, final String... args) throws IOException, ClearcaseException {
    return StringUtils.join(execClearToolCommand(workingdir, args), "\n"); //$NON-NLS-1$
  }

  /**
   * Execute the ClearTool command given in the varargs argument. ClearTool executable name ("cleartool") must not be given in the command.
   * @param args the cleartool command
   * @return the command result as String. Elements are separated by "\n".
   * @throws IOException
   * @throws ClearcaseException
   */
  public static String execClearToolCommandString(final String... args) throws IOException, ClearcaseException {
    return StringUtils.join(execClearToolCommand(null, args), "\n"); //$NON-NLS-1$
  }

  /**
   * @throws IOException
   */
  public static void startClearTool() throws IOException {
    __clearToolKeepAliveExecutor = new CleartoolKeepAliveExecutor();
  }

  /**
   * @throws IOException
   * @throws ClearcaseException
   */
  public static void stopClearTool() throws IOException, ClearcaseException {
    // Precondition.
    if (null != __clearToolKeepAliveExecutor) {
      // ClearTool is not started -> nothing to do.
      return;
    }
    // Stop ClearTool.
    __clearToolKeepAliveExecutor.exit();
  }
}