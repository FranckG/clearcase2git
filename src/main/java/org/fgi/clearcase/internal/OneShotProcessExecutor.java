
package org.fgi.clearcase.internal;

import java.io.IOException;
import java.nio.file.Path;


public class OneShotProcessExecutor {

  /**
   * @param command
   * @return command output
   * @throws IOException
   */
  public OneShotProcessResult executeCommand(final String[] command) throws IOException {
    return executeCommand(command, null);
  }

  /**
   * @param command
   * @param workingDir_p
   * @return command output
   * @throws IOException
   */
  public OneShotProcessResult executeCommand(final String[] command, final Path workingDir_p) throws IOException {
    final ProcessBuilder processBuilder = new ProcessBuilder(command);
    if (null != workingDir_p) {
      processBuilder.directory(workingDir_p.toFile());
    }
    final Process launchedProcess = processBuilder.start();
    final ProcessOutputHandler standardOutput = new ProcessOutputHandler(launchedProcess.getInputStream());
    standardOutput.start();
    final ProcessOutputHandler errorOutput = new ProcessOutputHandler(launchedProcess.getErrorStream());
    errorOutput.start();

    try {
      launchedProcess.waitFor();
      standardOutput.join();
      errorOutput.join();
    } catch (final InterruptedException interruptedException_p) {
      // TODO use a logger
    }

    return new OneShotProcessResult(launchedProcess.exitValue(), standardOutput.getResult(), errorOutput.getResult());
  }
}
