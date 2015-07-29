
package org.fgi.clearcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class StorageLocations {
  private StorageLocations() {
    // This is a helper: do not need a constructor
  }

  /**
   * @param stgloc_p
   * @return <code>true</code> if storage location exists, <code>false</code> otherwise
   * @throws IOException
   * @throws ClearcaseException
   */
  public static boolean exist(final String stgloc_p) throws IOException, ClearcaseException {
    boolean existsStgLoc = false;
    try {
      if (null != getStgLocInfo(stgloc_p)) {
        existsStgLoc = true;
      }
    } catch (final ClearcaseException exception_p) {
      final String erreurMessage = "No matching entries found for storage location"; //$NON-NLS-1$
      if (StringUtils.containsIgnoreCase(exception_p.getMessage(), erreurMessage)) {
        existsStgLoc = false;
      } else {
        throw exception_p;
      }
    }
    return existsStgLoc;
  }

  /**
   * Get storage locations of given type.
   * @param stgLocType_p
   * @return storage location list
   * @throws IOException
   * @throws ClearcaseException
   */
  public static List<String> getStgLoc(final StgLocType stgLocType_p) throws IOException, ClearcaseException {
    String stgLocTypeParameter = StringUtils.EMPTY;
    if (StgLocType.VIEW == stgLocType_p) {
      stgLocTypeParameter = "-view"; //$NON-NLS-1$
    } else if (StgLocType.VOB == stgLocType_p) {
      stgLocTypeParameter = "-vob"; //$NON-NLS-1$
    }
    final List<String> lsStgLoc = ClearcaseCli.execClearToolCommand("lsstgloc", "-short", stgLocTypeParameter); //$NON-NLS-1$ //$NON-NLS-2$
    return lsStgLoc;
  }

  /**
   * Retrieve information on a given storage location
   * @param stgLocName_p name of the storage location
   * @return list of storage location information (type, region, storage location uuid, global path, server host, server host path)
   * @throws IOException
   * @throws ClearcaseException
   */
  public static List<String> getStgLocInfo(final String stgLocName_p) throws IOException, ClearcaseException {
    final List<String> stgLocInfoList = ClearcaseCli.execClearToolCommand("lsstgloc", "-long", stgLocName_p); //$NON-NLS-1$ //$NON-NLS-2$

    // Remove prefix from output
    final List<String> lsStgLoc = new ArrayList<String>();
    final Pattern pattern = Pattern.compile("^.*?:\\s([^\\s]+)"); //$NON-NLS-1$
    for (final String stgLocInfo : stgLocInfoList) {
      final Matcher matcher = pattern.matcher(stgLocInfo);
      if (matcher.find()) {
        lsStgLoc.add(matcher.group(1));
      }
    }
    // Remove StgLoc name from result because we already have it
    lsStgLoc.remove(0);

    return lsStgLoc;
  }

  /**
   * Test whether the storage location is hosted on a Windows host
   * @param stgLoc_p name of the storage location
   * @return <code>true</code> if the storage location is a Windows host, <code>false</code> otherwise
   * @throws IOException Error accessing the view
   * @throws ClearcaseException Error ClearCase
   */
  public static boolean isStgLocOnWindowsHost(final String stgLoc_p) throws IOException, ClearcaseException {
    final List<String> stgLocInfoList = getStgLocInfo(stgLoc_p);

    final String serverHostPath = stgLocInfoList.get(5);
    final Pattern pattern = Pattern.compile("^[a-zA-Z]:\\\\"); //$NON-NLS-1$
    final Matcher matcher = pattern.matcher(serverHostPath);
    if (matcher.find()) {
      return true;
    }
    return false;
  }
}
