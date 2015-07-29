
package org.fgi.clearcase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Static class to manage {@link Baseline}s
 */
public final class Baselines {
  private Baselines() {
    // Helper: do not need a constructor
  }

  /**
   * Make a baseline using given view path. All components (and composite components) in the given view are baselined. Wished baseline name is cleaned from
   * characters other than word characters {@link Pattern}, '.' and '-', then it is used by ClearCase UCM as a base name for the baseline (a numeric extension
   * can be added by ClearCase UCM, see mkbl manual).
   * @param comment_p
   * @param wishedBlName_p baseline wished name
   * @param view_p
   * @return A map of (Component, Baseline).
   * @throws IOException
   * @throws ClearcaseException
   */
  public static Map<Component, Baseline> makeBaseline(final String comment_p, final String wishedBlName_p, final View view_p) throws IOException,
      ClearcaseException {
    File tempCommentFile = null;
    try {
      // Example of a mkbl call on a multi component stream :
      //
      // cleartool> mkbl -identical BLTestClearCaseCLI
      // Created baseline "BLTestClearCaseCLI.7678" in component "CompFirst".
      // Created baseline "BLTestClearCaseCLI.7681" in component "CompSecond".
      // Created baseline "BLTestClearCaseCLI.7684" in component "CompGroup".
      // Created baseline "BLTestClearCaseCLI.8267" in component "CompThird".
      // Created baseline "BLTestClearCaseCLI" in component "CompBigGroup".
      // Begin incrementally labelling baseline "BLTestClearCaseCLI.7678".
      // Done incrementally labelling baseline "BLTestClearCaseCLI.7678".
      // Begin incrementally labelling baseline "BLTestClearCaseCLI.7681".
      // Done incrementally labelling baseline "BLTestClearCaseCLI.7681".
      // Begin incrementally labelling baseline "BLTestClearCaseCLI.8267".
      // Done incrementally labelling baseline "BLTestClearCaseCLI.8267".

      // => Unable to get pVob (case a component from pVobA is used in stream pVobB)
      // => we need to loop through components

      // Allowed characters are : word characters (a-z, A-Z, 0-9 and '_'), '.' and '-'. Remove others.
      final String cleanedWishedBlName = wishedBlName_p.replaceAll("[^\\w\\.\\-]", StringUtils.EMPTY); //$NON-NLS-1$
      final Map<Component, Baseline> result = new HashMap<>();

      // Get stream
      final Stream stream = view_p.getStream();

      // Get components
      final List<Component> components = stream.getComponents();

      // construct command line
      final List<String> cleartoolCommand = new ArrayList<>();
      cleartoolCommand.add("mkbl"); //$NON-NLS-1$

      if (StringUtils.isNotBlank(comment_p)) {
        tempCommentFile = File.createTempFile("CcUcmEnvMkBlCommentFile", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
        try (final FileWriter tempCommentFileWriter = new FileWriter(tempCommentFile)) {
        	tempCommentFileWriter.write(comment_p);
        }
        cleartoolCommand.add("-cfile"); //$NON-NLS-1$
        cleartoolCommand.add(tempCommentFile.getAbsolutePath());
      }
      cleartoolCommand.add("-identical"); //$NON-NLS-1$

      for (final Component component : components) {
        final List<String> cleartoolCommandComponent = new ArrayList<>();
        cleartoolCommandComponent.addAll(cleartoolCommand);
        cleartoolCommandComponent.add(cleanedWishedBlName);

        final String mkBlResult = ClearcaseCli.execClearToolCommandString(view_p.getPath(),
                                                                          cleartoolCommandComponent.toArray(new String[cleartoolCommandComponent.size()]));
        // Pattern to get baseline name (group(1)) and component name (group(2)).
        final Pattern createdBaseLinePattern = Pattern.compile("[^\"\n]+\"([^\"\n]+)\"[^\"\n]+\"([^\"\n]+)\"[^\"\n]+\n"); //$NON-NLS-1$
        final Matcher createdBaseLineMatcher = createdBaseLinePattern.matcher(mkBlResult);
        while (createdBaseLineMatcher.find()) {
          final String baselineName = createdBaseLineMatcher.group(1);
          final Selector baselineSelector = new Selector(baselineName, component.getPVob().getTag(), CcUcmObjectType.BASELINE);
          result.put(component, new Baseline(baselineSelector));
        }
      }
      return result;
    } finally {
      // Whatever happens, don't forget to delete comment file, if it was created.
      if (null != tempCommentFile) {
        tempCommentFile.delete();
      }
    }
  }
}
