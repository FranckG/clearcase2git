
package org.fgi.clearcase.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handle Process output or Error.
 */
public class ProcessOutputHandler extends Thread {
  /**
   * List holding the captured lines.
   */
  private final List<String> _capturedLines;

  /**
   * Stream being read.
   */
  private final BufferedReader _reader;

  /**
   * Constructor.
   */
  ProcessOutputHandler(final InputStream processInputStream_p) {
    _reader = new BufferedReader(new InputStreamReader(processInputStream_p));
    // Use StringBuffer because it is thread-safe.
    _capturedLines = Collections.synchronizedList(new ArrayList<String>());
  }

  public void clearResultList() {
    _capturedLines.clear();
  }

  public List<String> getResult() {
    return new ArrayList<String>(_capturedLines);
  }

  /**
   * Stream the data.
   */
  @Override
  public void run() {
    try {
      String readLine = null;
      // null is returned when the stream is closed (when cleartool exits).
      while (null != (readLine = _reader.readLine())) {
        _capturedLines.add(readLine);
      }
    } catch (final IOException ioException_p) {
      // TODO use a logger
    } finally {
      // Close quietly.
      try {
        _reader.close();
      } catch (final IOException exception_p) {
        // Nothing to do.
      }
    }
  }
}
