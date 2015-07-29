
package org.fgi.clearcase;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * To manipulate helpers as Views, Streams, etc.
 */
public abstract class ClearcaseHelper {
  /**
   * @param selector_p
   * @return <code>true</code> if object exists in CC/UCM, <code>false</code> otherwise
   * @throws IOException
   * @throws ClearcaseException
   */
  public static boolean exist(final Selector selector_p) throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "describe", "-short", selector_p.toString() }; //$NON-NLS-1$ //$NON-NLS-2$
    try {
      ClearcaseCli.execClearToolCommandString(clearToolCommand);
    } catch (final ClearcaseException exception_p) {
      final String error = exception_p.getError();
      if (StringUtils.containsIgnoreCase(error, " not found: ") || StringUtils.containsIgnoreCase(error, "cleartool: Error: Not a ")) { //$NON-NLS-1$ //$NON-NLS-2$
        return false;
      }
      throw exception_p;
    }
    return true;
  }

  /**
   * Get versions of files for which paths are given.
   * @param paths
   * @return An array of Strings containing the versions for all paths. Versions can have the following values :
   *         <ul>
   *         <li>{@link ClearcaseCli#VIEWPRIVATEFILE} if the specified file is view private,</li>
   *         <li>{@link ClearcaseCli#CHECKEDOUT} if the specified file is checked out,</li>
   *         <li>{@link ClearcaseCli#INVALIDPATH} if an error occurred (e.g. : given path doesn't exist),</li>
   *         <li>Else, the ClearCase version of the element.
   *         </ul>
   */
  public static String[] getVersions(final String paths[]) {
    final String result[] = new String[paths.length];
    for (int i = 0; i < paths.length; i++) {
      final StringBuilder sb = new StringBuilder("ls -d -s \""); //$NON-NLS-1$
      sb.append(paths[i]);
      sb.append("\""); //$NON-NLS-1$
      try {
        final String version = ClearcaseCli.execClearToolCommandString(sb.toString());
        if (version.equals(paths[i])) {
          result[i] = ClearcaseCli.VIEWPRIVATEFILE;
        } else if (version.contains("@@")) { //$NON-NLS-1$
          result[i] = version.substring(version.indexOf("@@") + 2, version.length()); //$NON-NLS-1$
          if (result[i].endsWith("CHECKEDOUT")) { //$NON-NLS-1$
            result[i] = ClearcaseCli.CHECKEDOUT;
          }
        } else {
          result[i] = ClearcaseCli.INVALIDPATH;
        }
      } catch (final Exception _ex) {
        result[i] = ClearcaseCli.INVALIDPATH;
      }
    }

    return result;
  }

  /**
   * Generate a config spec for a non UCM view in order to access to some baselines in a read-only way.
   * @param baselines_p the baseline selectors to be accessed
   * @return the config spec content
   * @throws IOException
   * @throws ClearcaseException
   */
  public static String makeReadOnlyCsOnBaselines(final List<Baseline> baselines_p) throws IOException, ClearcaseException {
    // TODO verify this function
    final String baselineSelectionFormat = "element \\{0} /main/0 -nocheckout\nelement \\{0}\\... {1} -nocheckout\n"; //$NON-NLS-1$
    final StringBuilder resultConfigSpec = new StringBuilder();
    for (final Baseline baseline : baselines_p) {
      final Component component = baseline.getComponent();
      final Vob componentVob = component.getPVob();
      resultConfigSpec.append(MessageFormat.format(baselineSelectionFormat, componentVob.getName(), baseline.getName()));
    }
    return resultConfigSpec.toString();
  }
}
