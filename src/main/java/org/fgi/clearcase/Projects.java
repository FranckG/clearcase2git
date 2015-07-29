

package org.fgi.clearcase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class Projects {

  private Projects() {
    // this is a helper: do not need a constructor
  }

  /**
   * Create a new project in CC UCM.
   * @param comment_p comment to add to the project, can be <code>null</code> or empty
   * @param modifiableComponents_p simple name of modifiable components, can't be <code>null</code>
   * @param parentFolder_p parent folder simple name for the new project, can be <code>null</code> or empty
   * @param enabledPolicies_p list of policies to enable for this project, can be <code>null</code> or empty
   * @param disabledPolicies_p list of policies to disable for this project, can be <code>null</code> or empty
   * @param streamPolicies_p list of policies for which the per-stream configuration has to be used, can be <code>null</code> or empty
   * @param clearQuestDataBaseName_p the ClearQuest data base name, can be <code>null</code> or empty
   * @param projectSelector_p
   * @throws IOException
   * @throws ClearcaseException
   */
  public static void makeProject(final String comment_p, final List<Component> modifiableComponents_p, final Folder parentFolder_p,
      final List<String> enabledPolicies_p, final List<String> disabledPolicies_p, final List<String> streamPolicies_p, final String clearQuestDataBaseName_p,
      final Selector projectSelector_p) throws IOException, ClearcaseException {
    File tempCommentFile = null;

    final List<Selector> modifiableComponentSelectors = new ArrayList<>();
    for (final Component component : modifiableComponents_p) {
      modifiableComponentSelectors.add(component.getSelector());
    }

    try {
      // Fill command line arguments list.
      final List<String> commandLineArguments = new ArrayList<String>();
      commandLineArguments.add("mkproject"); //$NON-NLS-1$
      // Comments
      if (StringUtils.isNotBlank(comment_p)) {
        tempCommentFile = File.createTempFile("CcUcmEnvMkProjectCommentFile", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
        try (final FileWriter tempCommentFileWriter = new FileWriter(tempCommentFile)) {
          tempCommentFileWriter.write(comment_p);
        }
        commandLineArguments.add("-cfile"); //$NON-NLS-1$
        commandLineArguments.add(tempCommentFile.getAbsolutePath());
      }
      commandLineArguments.add("-modcomp"); //$NON-NLS-1$
      commandLineArguments.add(StringUtils.join(modifiableComponentSelectors, ','));
      commandLineArguments.add("-in"); //$NON-NLS-1$
      commandLineArguments.add(parentFolder_p.getSelector().toString());
      // Add policies settings.
      // Enabled policies.
      if (null != enabledPolicies_p && !enabledPolicies_p.isEmpty()) {
        commandLineArguments.add("-policy"); //$NON-NLS-1$
        commandLineArguments.add(StringUtils.join(enabledPolicies_p, ",")); //$NON-NLS-1$
      }
      // Disabled policies.
      if (null != disabledPolicies_p && !disabledPolicies_p.isEmpty()) {
        commandLineArguments.add("-npolicy"); //$NON-NLS-1$
        commandLineArguments.add(StringUtils.join(disabledPolicies_p, ",")); //$NON-NLS-1$
      }
      // Stream policies.
      if (null != streamPolicies_p && !streamPolicies_p.isEmpty()) {
        commandLineArguments.add("-spolicy"); //$NON-NLS-1$
        commandLineArguments.add(StringUtils.join(streamPolicies_p, ",")); //$NON-NLS-1$
      }
      // Add the ClearQuest database name to use, if it is given.
      if (null != clearQuestDataBaseName_p && !clearQuestDataBaseName_p.isEmpty()) {
        commandLineArguments.add("-crmenable"); //$NON-NLS-1$
        commandLineArguments.add(clearQuestDataBaseName_p);
      }
      commandLineArguments.add(projectSelector_p.toString());

      ClearcaseCli.execClearToolCommand(commandLineArguments.toArray(new String[commandLineArguments.size()]));

    }
    finally {
      // Whatever happens, don't forget to delete comment file, if it was created.
      if (null != tempCommentFile) {
        tempCommentFile.delete();
      }
    }
  }
}
