
package org.fgi.clearcase.internal;

import java.util.List;


public class OneShotProcessResult {
  private final List<String> errorLines;
  private final List<String> outputLines;
  private final int status;

  /**
   * @param status_p
   * @param outputLines_p
   * @param errorLines_p
   */
  public OneShotProcessResult(final int status_p, final List<String> outputLines_p, final List<String> errorLines_p) {
    status = status_p;
    outputLines = outputLines_p;
    errorLines = errorLines_p;
  }

  /**
   * @return the errorLines
   */
  public List<String> getErrorLines() {
    return errorLines;
  }

  /**
   * @return the outputLines
   */
  public List<String> getOutputLines() {
    return outputLines;
  }

  /**
   * @return the status
   */
  public int getStatus() {
    return status;
  }

}
