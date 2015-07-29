
package org.fgi.clearcase.internal;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.fgi.clearcase.ClearcaseCli;
import org.fgi.clearcase.ClearcaseException;

public class CleartoolOneShotExecutor implements IClearToolExecutor {
  /**
   * @param clearToolCommand_p
   * @param workingDir_p
   * @return command output
   * @throws IOException
   * @throws ClearcaseException
   */
  @Override
  public List<String> executeClearToolCommand(final String[] clearToolCommand_p, final Path workingDir_p) throws IOException, ClearcaseException {
    final String[] command = new String[clearToolCommand_p.length + 1];
    System.arraycopy(clearToolCommand_p, 0, command, 1, clearToolCommand_p.length);
    command[0] = ClearcaseCli.CLEARTOOL;

    final OneShotProcessExecutor oneShotProcessExecutor = new OneShotProcessExecutor();
    final OneShotProcessResult processResult = oneShotProcessExecutor.executeCommand(command, workingDir_p);
    if (0 != processResult.getStatus()) {
      throw new ClearcaseException(clearToolCommand_p, processResult.getOutputLines(), processResult.getErrorLines());
    }
    return processResult.getOutputLines();
  }
}
