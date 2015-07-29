package org.fgi.clearcase;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ClearcaseException extends Exception {
  private static final long serialVersionUID = 4792774739737568726L;
  private String[] _command;
  private final String _error;
  private final String _output;

  /**
   * @param command_p
   * @param outputLines_p
   * @param errorLines_p
   */
  public ClearcaseException(final String[] command_p, final List<String> outputLines_p, final List<String> errorLines_p) {
    this(command_p, StringUtils.join(outputLines_p, "\n"), StringUtils.join(errorLines_p, "\n")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * @param command_p
   * @param output_p
   * @param error_p
   */
  public ClearcaseException(final String[] command_p, final String output_p, final String error_p) {
    if (null == command_p) { // see http://stackoverflow.com/questions/11580948/sonar-violation-security-array-is-stored-directly
      _command = new String[0];
    } else {
      _command = Arrays.copyOf(command_p, command_p.length);
    }
    _output = output_p;
    _error = error_p;
  }

  /**
   * @return the command
   */
  private String[] getCommand() {
    return _command;
  }

  /**
   * @return error
   */
  public String getError() {
    return _error;
  }

  /**
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    String message = "Command:"; //$NON-NLS-1$
    for (final String token : getCommand()) {
      message += " " + token; //$NON-NLS-1$
    }
    message += "\n\nOutput:\n" + getOutput() + "\n\nError:\n" + getError() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return message;
  }

  /**
   * @return output
   */
  public String getOutput() {
    return _output;
  }

}
