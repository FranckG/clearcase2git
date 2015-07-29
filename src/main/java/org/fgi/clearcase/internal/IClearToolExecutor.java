
package org.fgi.clearcase.internal;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.fgi.clearcase.ClearcaseException;

public interface IClearToolExecutor {
  /**
   * @param command_p
   * @param workingDir_p
   * @return command output
   * @throws IOException
   * @throws ClearcaseException
   */
  List<String> executeClearToolCommand(final String[] command_p, final Path workingDir_p) throws IOException, ClearcaseException;
}
