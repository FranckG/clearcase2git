
package org.fgi.clearcase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import org.fgi.clearcase.internal.OneShotProcessExecutor;
import org.fgi.clearcase.internal.OneShotProcessResult;

/**
 * Static class to create / remove / manage ClearCase {@link View}s
 */
public class Views {
  /**
   * String used if not in a view context
   */
  public static final String NOTAVIEWPATH = "<not a view path>"; //$NON-NLS-1$

  private Views() {
    // this is a helper: do not need a constructor
  }

  /**
   * check existence of the view against ClearCase UCM
   * @param viewTag_p
   * @return <code>true</code> if view exists, <code>false</code> otherwise
   * @throws ClearcaseException
   * @throws IOException
   */
  public static boolean exist(final String viewTag_p) throws ClearcaseException, IOException {
    if (StringUtils.isBlank(viewTag_p)) {
      return false;
    }
    try {
      ClearcaseCli.execClearToolCommand("lsview", "-short", viewTag_p); //$NON-NLS-1$ //$NON-NLS-2$
      return true;
    } catch (final ClearcaseException | IOException exception_p) {
      if (!StringUtils.containsIgnoreCase(exception_p.getMessage(), "Error: No matching entries found for view tag")) { //$NON-NLS-1$
        throw exception_p;
      }
    }
    return false;
  }

  /**
   * Get the view tag of the view got from the given path.
   * @param path_p
   * @return view tag, return <code>null</code> if given path is not a view path
   * @throws IOException
   * @throws ClearcaseException
   */
  public static View getView(final Path path_p) throws IOException, ClearcaseException {
    final String[] command = { "pwv", "-short" }; //$NON-NLS-1$ //$NON-NLS-2$
    if (null == path_p) {
      throw new ClearcaseException(command, StringUtils.EMPTY, "No path specified"); //$NON-NLS-1$
    }
    final String pwv = ClearcaseCli.execClearToolCommandString(path_p, command);
    if (pwv.equals("** NONE **")) { //$NON-NLS-1$
      return null;
    }
    return new View(pwv);
  }

  /**
   * Get the ClearCase dynamic views root. Under Windows, it's commonly "M:".
   * @return view root [M:]. May return null!
   */
  public static Path getViewRoot() {
    if (!SystemUtils.IS_OS_WINDOWS) {
      return Paths.get("/view/"); //$NON-NLS-1$
    }
    try {
      final Pattern pattern = Pattern.compile(Pattern.quote(File.separator + "view") + "\\s+"); // default win32 value //$NON-NLS-1$ //$NON-NLS-2$
      final String[] command = new String[] { "net", "use" }; //$NON-NLS-1$ //$NON-NLS-2$
      final OneShotProcessResult oneShotProcessResult = new OneShotProcessExecutor().executeCommand(command);
      if (0 != oneShotProcessResult.getStatus()) {
        return Paths.get(null);
      }
      for (final String outputLine : oneShotProcessResult.getOutputLines()) {
        final Matcher outputLineMatcher = pattern.matcher(outputLine);
        if (outputLineMatcher.find()) {
          final int pos = outputLine.indexOf(":"); //$NON-NLS-1$
          return Paths.get(outputLine.substring(pos - 1, pos + 1) + "\\"); //$NON-NLS-1$
        }
      }
    } catch (final IOException e) {
      // TODO use a logger
    }
    return Paths.get(null);
  }

  /**
   * Retrieve the root path of a view using a path in this view.
   * @param path_p
   * @return view root
   * @throws IOException
   * @throws ClearcaseException
   */
  public static String getViewRoot(final Path path_p) throws IOException, ClearcaseException {
    final String command[] = { "pwv", "-root" }; //$NON-NLS-1$ //$NON-NLS-2$
    if ((path_p == null) || path_p.equals(StringUtils.EMPTY)) {
      throw new ClearcaseException(command, StringUtils.EMPTY, "No path specified"); //$NON-NLS-1$
    }
    String pwv = StringUtils.EMPTY;
    try {
      pwv = ClearcaseCli.execClearToolCommandString(path_p, command);
    } catch (final ClearcaseException exception_p) {
      if (StringUtils.equalsIgnoreCase(exception_p.getError(), "cleartool: Error: operation requires a view")) { //$NON-NLS-1$
        return NOTAVIEWPATH;
      }
      throw exception_p;
    }
    return pwv;
  }

  /**
   * get current view root path
   * @return view root path
   * @throws IOException
   * @throws ClearcaseException
   */
  public static String getViewRootPath() throws IOException, ClearcaseException {
    final String command[] = { "pwv", "-root" }; //$NON-NLS-1$ //$NON-NLS-2$
    String viewRootPath = StringUtils.EMPTY;
    try {
      viewRootPath = ClearcaseCli.execClearToolCommandString(command);
    } catch (final ClearcaseException exception_p) {
      if (StringUtils.equalsIgnoreCase(exception_p.getError(), "cleartool Error: operation requires to be inside a view")) { //$NON-NLS-1$
        return NOTAVIEWPATH;
      }
      throw exception_p;
    }
    return viewRootPath;
  }

  /**
   * Get the list of all ClearCase views.
   * @return {@link View}
   * @throws IOException
   * @throws ClearcaseException
   */
  public static List<View> getViews() throws IOException, ClearcaseException {
    final List<String> viewtags = ClearcaseCli.execClearToolCommand("lsview", "-short"); //$NON-NLS-1$ //$NON-NLS-2$
    final List<View> views = new ArrayList<View>();
    for (final String viewTag : viewtags) {
      views.add(new View(viewTag));
    }
    return views;
  }

  /**
   * Is given path a path to a snapshot view ?
   * @param path_p
   * @return <code>true</code> if given path leads to a snapshot view, <code>false</code> else.
   * @throws IOException
   * @throws ClearcaseException
   */
  public static boolean isSnapshotView(final Path path_p) throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "pwv", "-short" }; //$NON-NLS-1$ //$NON-NLS-2$
    if (null == path_p) {
      throw new ClearcaseException(clearToolCommand, StringUtils.EMPTY, "No path specified"); //$NON-NLS-1$
    }
    final String output = ClearcaseCli.execClearToolCommandString(path_p, clearToolCommand);
    return !StringUtils.containsIgnoreCase(output, "** NONE **"); //$NON-NLS-1$
  }

  /**
   * Make a dynamic view.
   * @param dynamicViewTag_p
   * @param stgLoc_p
   * @return created view
   * @throws IOException
   * @throws ClearcaseException
   */
  public static View makeDynamicView(final String dynamicViewTag_p, final String stgLoc_p) throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("mkview", "-tag", dynamicViewTag_p, "-stgloc", stgLoc_p); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return new View(dynamicViewTag_p);
  }

  /**
   * Make a dynamic view at UNC path.
   * @param dynamicViewTag_p
   * @param uncPath_p
   * @return created view
   * @throws IOException
   * @throws ClearcaseException
   */
  public static View makeDynamicViewAtPath(final String dynamicViewTag_p, final Path uncPath_p) throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("mkview", "-tag", dynamicViewTag_p, uncPath_p.toString()); //$NON-NLS-1$ //$NON-NLS-2$
    return new View(dynamicViewTag_p);
  }

  /**
   * remove a snapshot view (given its path)
   * @param snapshotViewLocation_p
   * @return cleartool command output
   * @throws ClearcaseException
   * @throws IOException
   */
  public static String remove(Path snapshotViewLocation_p) throws IOException, ClearcaseException {
    final String result = ClearcaseCli.execClearToolCommandString(new String[] { "rmview", "-force", snapshotViewLocation_p.toString() }); //$NON-NLS-1$ //$NON-NLS-2$
    return result;
  }

  /**
   * remove a dynamic view (given by its view tag)
   * @param view_p
   * @return cleartool output
   * @throws ClearcaseException
   * @throws IOException
   */
  public static String remove(final View view_p) throws IOException, ClearcaseException {
    final String result = ClearcaseCli.execClearToolCommandString(new String[] { "rmview", "-force", "-tag", view_p.getTag() }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return result;
  }
}
