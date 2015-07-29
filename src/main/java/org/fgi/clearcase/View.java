
package org.fgi.clearcase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import org.fgi.clearcase.internal.OneShotProcessExecutor;

/**
 * To access Clearcase view
 */
public class View {

  private String _viewTag;

  /**
   * Construct a view from a view tag
   * @param viewTag_p
   */
  public View(final String viewTag_p) {
    setViewTag(viewTag_p);
  }

  /**
   * @return config spec (cleartool output)
   * @throws IOException
   * @throws ClearcaseException
   */
  public String getConfigSpec() throws IOException, ClearcaseException {
    return ClearcaseCli.execClearToolCommandString(new String[] { "catcs", "-tag", getTag() }); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * @return activity set into the view
   * @throws IOException
   * @throws ClearcaseException
   */
  public Activity getCurrentActivity() throws IOException, ClearcaseException {
    final String activitySelector = ClearcaseCli.execClearToolCommandString(new String[] { "lsact", "-cact", "-fmt", "%Xn", "-view", getTag() }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    return new Activity(new Selector(activitySelector));
  }

  /**
   * @return view path
   */
  public Path getPath() {
    // FIXME Auto-generated method stub
    return null;
  }

  /**
   * Get the UCM project to which this view belongs.
   * @return UCM project
   * @throws IOException
   * @throws ClearcaseException
   */
  public Project getProject() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "lsproject", "-fmt", "\"%[name]Xp\"", "-view", this.getTag() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final String projectSelector = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    return new Project(new Selector(projectSelector));
  }

  /**
   * Get the extended name of the stream linked to the given view.
   * @return Stream
   * @throws IOException
   * @throws ClearcaseException
   */
  public Stream getStream() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "lsstream", "-fmt", "\"%[name]Xp\"", "-view", getTag() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final String streamSelector = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    return new Stream(new Selector(streamSelector));
  }

  /**
   * @return the viewTag
   */
  public String getTag() {
    return _viewTag;
  }

  /**
   * Is given view dynamic ?
   * @return <code>true</code> if the view is dynamic, <code>false</code> if it is snapshot.
   * @throws IOException
   * @throws ClearcaseException
   */
  public boolean isDynamic() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "lsview", "-long", this.getTag() }; //$NON-NLS-1$ //$NON-NLS-2$

    final List<String> lsViewOutputLines = ClearcaseCli.execClearToolCommand(clearToolCommand);
    for (final String line : lsViewOutputLines) {
      if (StringUtils.containsIgnoreCase(line, "View attributes: ")) { //$NON-NLS-1$
        return !StringUtils.containsIgnoreCase(line, "snapshot"); //$NON-NLS-1$
      }
    }
    return true;
  }

  /**
   * Is ClearCase view started ?
   * @return <code>true</code> if the view is dynamic AND started, <code>false</code> otherwise (not started view or view tag identifying a snapshot view).
   * @throws IOException
   * @throws ClearcaseException if given view tag does not exist.
   */
  public boolean isStarted() throws IOException, ClearcaseException {
    final String lsViewResult = ClearcaseCli.execClearToolCommandString("lsview", getTag()); //$NON-NLS-1$
    // When a view is started, '*' starts the line describing it.
    if (lsViewResult.startsWith("*")) { //$NON-NLS-1$
      return true;
    }
    return false;
  }

  /**
   * @param activity_p
   * @param comment_p
   * @return boolean
   * @throws IOException
   * @throws ClearcaseException
   */
  public boolean setActivity(final Activity activity_p, final String comment_p) throws IOException, ClearcaseException {
    String result;
    // set activity as the current one
    if (SystemUtils.IS_OS_WINDOWS) {
      final String[] clearToolCommand =
          new String[] { "setactivity", "-c", "\"" + comment_p + "\"", "-view", this.getTag(), activity_p.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
      result = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    }
    else {
      final String[] setActivityCommand =
          new String[] { "/bin/sh", "-c", //$NON-NLS-1$ //$NON-NLS-2$
                         ClearcaseCli.CLEARTOOL + " setactivity -c \"" + comment_p + "\" -view " + this.getTag() + " " + activity_p.getSelector() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      result = StringUtils.join(new OneShotProcessExecutor().executeCommand(setActivityCommand).getOutputLines(), "\n"); //$NON-NLS-1$
    }
    if (result.isEmpty()) {
      return false;
    }
    return true;
  }

  /**
   * Set given config spec for given view.
   * @param configSpecContent_p
   * @throws IOException
   * @throws ClearcaseException
   */
  public void setConfigSpec(final String configSpecContent_p) throws IOException, ClearcaseException {
    // Create a temporary file to be used with "setcs".
    final File tempConfigSpecFile = File.createTempFile("ConfigSpec_" + this.getTag(), null); //$NON-NLS-1$
    // Write given content to created file.
    try (final FileWriter tempConfigSpecFileWriter = new FileWriter(tempConfigSpecFile)) {
      tempConfigSpecFileWriter.write(configSpecContent_p);
    }
    // Execute "setcs".
    ClearcaseCli.execClearToolCommand(new String[] { "setcs", "-tag", this.getTag(), tempConfigSpecFile.getAbsolutePath() }); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * @param viewTag_p the viewTag to set
   */
  private void setViewTag(String viewTag_p) {
    _viewTag = viewTag_p;
  }

  /**
   * Starts the view.
   * @throws IOException
   * @throws ClearcaseException
   */
  public void start() throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("startview", getTag()); //$NON-NLS-1$
  }

  /**
   * @return cleartool output
   * @throws IOException
   * @throws ClearcaseException
   */
  public String unsetActivity() throws IOException, ClearcaseException {
    final String result = ClearcaseCli.execClearToolCommandString(new String[] { "setact", "-nc", "-none", "-view", getTag() }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    return result;
  }

  /**
   * @throws ClearcaseException
   * @throws IOException
   */
  public void updateAddLoadRules() throws IOException, ClearcaseException {
    // get components
    final List<Component> components = this.getStream().getComponents();

    // Construct cleartool command line
    final List<String> cleartoolCommand = new ArrayList<String>();
    cleartoolCommand.add("update"); //$NON-NLS-1$
    cleartoolCommand.add("-force"); //$NON-NLS-1$
    cleartoolCommand.add("-add_loadrules"); //$NON-NLS-1$

    // get root for each component
    for (final Component component : components) {
      // We need the relative path to the component root dir (eg without the leading slash)
      final String componentRootDir = component.getRootDir();
      final String componentrelativeRootDir = StringUtils.removeStartIgnoreCase(componentRootDir, File.separator);
      cleartoolCommand.add(componentrelativeRootDir);
    }

    // update config spec
    ClearcaseCli.execClearToolCommand(this.getPath(), cleartoolCommand.toArray(new String[cleartoolCommand.size()]));
  }
}
