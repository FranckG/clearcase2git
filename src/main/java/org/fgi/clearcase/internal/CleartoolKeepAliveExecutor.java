
package org.fgi.clearcase.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.fgi.clearcase.ClearcaseException;
import org.fgi.clearcase.internal.CleartoolKeepAliveExecutor.ClearToolOutputHandler.OutputResult;

public class CleartoolKeepAliveExecutor implements IClearToolExecutor {

  private final ClearToolOutputHandler _clearToolOutputHandler;

  private final ProcessOutputHandler _processErrorHandler;

  private final BufferedWriter _processInput;

  /**
   * @throws IOException
   */
  public CleartoolKeepAliveExecutor() throws IOException {
    final Process process = Runtime.getRuntime().exec("cleartool -status"); //$NON-NLS-1$

    _clearToolOutputHandler = new ClearToolOutputHandler(process.getInputStream());
    _clearToolOutputHandler.start();

    _processErrorHandler = new ProcessOutputHandler(process.getErrorStream());
    _processErrorHandler.start();

    _processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

  }

  /**
   *
   */
  @Override
  public List<String> executeClearToolCommand(final String[] command_p, final Path workingDir_p) throws IOException, ClearcaseException {
    try {
      if (null != workingDir_p) {
        _clearToolOutputHandler.resetResult();
        _processErrorHandler.clearResultList();
        _processInput.write("cd " + workingDir_p + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
        _processInput.flush();

        _clearToolOutputHandler.waitForAResult();
        final OutputResult outputResult = _clearToolOutputHandler.getResult();
        if (0 != outputResult.getStatus()) {
          throw new ClearcaseException(command_p, outputResult.getOutputLines(), _processErrorHandler.getResult());
        }
      }

      _clearToolOutputHandler.resetResult();
      _processErrorHandler.clearResultList();
      // Give the given command to ClearTool.

      _processInput.write(StringUtils.join(Arrays.asList(command_p), " ") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
      _processInput.flush();

      _clearToolOutputHandler.waitForAResult();
      final OutputResult outputResult = _clearToolOutputHandler.getResult();
      if (0 != outputResult.getStatus()) {
        throw new ClearcaseException(command_p, outputResult.getOutputLines(), _processErrorHandler.getResult());
      }
    }
    catch (final InterruptedException exception_p) {
      final OutputResult outputResult = _clearToolOutputHandler.getResult();
      throw new ClearcaseException(command_p, outputResult.getOutputLines(), _processErrorHandler.getResult());
    }
    return _clearToolOutputHandler.getResult().getOutputLines();
  }

  /**
   * @throws IOException
   * @throws ClearcaseException
   */
  public void exit() throws IOException, ClearcaseException {
    executeClearToolCommand(new String[] { "exit" }, null); //$NON-NLS-1$
  }

  /**
   * Thread reading the output of the ClearTool process.
   */
  public static class ClearToolOutputHandler extends Thread {

    private static final Pattern _statusLinePattern = Pattern.compile("Command (\\d+) returned status (\\d)"); //$NON-NLS-1$

    private final List<String> _capturedLines;

    private String _numCommand;

    /**
     * Stream being read
     */
    private final BufferedReader _reader;

    private final Object _resultLock;

    private String _status;

    /**
     * Constructor.
     * @param processInputStream_p
     */
    public ClearToolOutputHandler(final InputStream processInputStream_p) {
      _reader = new BufferedReader(new InputStreamReader(processInputStream_p));
      _resultLock = new Object();
      _numCommand = null;
      _status = null;
      _capturedLines = new ArrayList<String>();

    }

    private void commandTerminated(final String numCommand_p, final String status_p) {
      _numCommand = numCommand_p;
      _status = status_p;
      _resultLock.notifyAll();
    }

    /**
     * @return output
     */
    public OutputResult getResult() {
      synchronized (_resultLock) {
        return new OutputResult(Integer.valueOf(_numCommand).intValue(), Integer.valueOf(_status).intValue(), new ArrayList<String>(_capturedLines));
      }
    }

    /**
     *
     */
    public void resetResult() {
      synchronized (_resultLock) {
        _numCommand = null;
        _status = null;
        _capturedLines.clear();
      }
    }

    /**
     * Stream the data.
     */
    @Override
    public void run() {
      try {
        String readLine = null;
        while (null != (readLine = _reader.readLine())) {
          synchronized (_resultLock) {
            final Matcher lineMatcher = _statusLinePattern.matcher(readLine);
            if (lineMatcher.matches()) {
              // It's the end of our command -> create a result object.
              commandTerminated(lineMatcher.group(1), lineMatcher.group(2));
            }
            else if (lineMatcher.find()) {
              _capturedLines.add(readLine.replace(lineMatcher.group(), "")); //$NON-NLS-1$
              commandTerminated(lineMatcher.group(1), lineMatcher.group(2));
            }
            else {
              _capturedLines.add(readLine);
            }
          }
        }
      }
      catch (final IOException ioException_p) {
        // TODO use a looger
      }
      finally {
        // Close quietly.
        try {
          _reader.close();
        }
        catch (final IOException exception_p) {
          // Nothing to do.
        }
      }
    }

    /**
     * @throws InterruptedException
     */
    public void waitForAResult() throws InterruptedException {
      synchronized (_resultLock) {
        while (null == _status) {
          _resultLock.wait();
        }
      }
    }

    public static class OutputResult {

      private final int _numCommand;
      private final List<String> _outputLines;
      private final int _status;

      /**
       * Constructor
       * @param numCommand_p
       * @param status_p
       * @param outputLines_p
       */
      public OutputResult(final int numCommand_p, final int status_p, final List<String> outputLines_p) {
        _numCommand = numCommand_p;
        _status = status_p;
        _outputLines = outputLines_p;
      }

      /**
       * @return the numCommand
       */
      public int getNumCommand() {
        return _numCommand;
      }

      /**
       * @return the outputLines
       */
      public List<String> getOutputLines() {
        return _outputLines;
      }

      /**
       * @return the status
       */
      public int getStatus() {
        return _status;
      }
    }
  }

}
