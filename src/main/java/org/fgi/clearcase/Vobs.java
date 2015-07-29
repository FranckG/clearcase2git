
package org.fgi.clearcase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Static class to manipulate {@link Vob}s
 */
public class Vobs {
  private Vobs() {
    // This is a helper: do not need a constructor
  }

  /**
   * @param vobName_p
   * @return a vobTag (\Test_pvob)
   */
  public static String getTag(final String vobName_p) {
    final StringBuilder sb = new StringBuilder(File.separator);
    sb.append(vobName_p);
    return sb.toString();
  }

  /**
   * Get ClearCase VOBs. This method can return CCUCM project VOBs, ClearCase VOBs or both.
   * @param askedVobType_p can be <code>null</code> if all VOBs of all types are needed.
   * @return Simple VOB names without leading backslash.
   * @throws IOException
   * @throws ClearcaseException
   */
  public static List<Vob> getVobs(final CcUcmVobType askedVobType_p) throws IOException, ClearcaseException {

    // VOB name extraction pattern (find a string starting from \ and ending with a whitespace character).
    // group(1) -> VOB name without its leading backslash.
    final Pattern pattern = Pattern.compile("(\\\\[^\\\\\\s]+)\\s"); //$NON-NLS-1$
    // UCM VOB identifier.
    final String UCM_VOB = "ucmvob"; //$NON-NLS-1$
    final List<Vob> vobs = new ArrayList<>();
    // Execute cleartool command.
    final List<String> vobTags = ClearcaseCli.execClearToolCommand("lsvob"); //$NON-NLS-1$
    for (final String vobTagString : vobTags) {
      // Keep VOB name if :
      // - No VOB type is asked,
      // - UCM Project VOB type is asked and current line contains "ucmvob",
      // - NON UCM Project VOB is asked and currentLine doesn't contain "ucmvob".
      if ((null == askedVobType_p) || ((CcUcmVobType.UCM_PROJECT_VOB == askedVobType_p) && (StringUtils.contains(vobTagString, UCM_VOB)))
          || ((CcUcmVobType.NON_UCM_PROJECT_VOB == askedVobType_p) && (!StringUtils.contains(vobTagString, UCM_VOB)))) {
        final Matcher matcher = pattern.matcher(vobTagString);
        if (matcher.find()) {
          final String vobTag = matcher.group(1);
          final Selector vobSelector = new Selector(vobTag, CcUcmObjectType.VOB);
          vobs.add(new Vob(vobSelector));
        }
      }
    }
    return vobs;
  }

}
