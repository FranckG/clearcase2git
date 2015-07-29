
package org.fgi.clearcase;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class Stream extends AbstractCcObject {

  /**
   * @param selector_p
   */
  public Stream(final Selector selector_p) {
    super(selector_p);
  }

  /**
   * @return cleartool command output
   * @throws IOException
   * @throws ClearcaseException
   */
  public String deliver() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "deliver", "-graphical", "-stream", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return ClearcaseCli.execClearToolCommandString(clearToolCommand);
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object toCompare_p) {
    if (this == toCompare_p) {
      return true;
    }
    if (null == toCompare_p) {
      return false;
    }
    if (getClass() != toCompare_p.getClass()) {
      return false;
    }
    final Stream other = (Stream) toCompare_p;
    final Selector selector = this.getSelector();
    final Selector otherSelector = other.getSelector();
    if (null == selector) {
      if (null != otherSelector) {
        return false;
      }
    } else if (!selector.equals(otherSelector)) {
      return false;
    }
    return true;
  }

  /**
   * @return activities
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Activity> getActivities() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "describe", "-fmt", "\"%[activities]Xp\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final String result = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    final List<Activity> activities = new ArrayList<>();
    if (StringUtils.isNotBlank(result) && result.startsWith(CcUcmObjectType.ACTIVITY.getValue() + ":")) { //$NON-NLS-1$
      final String[] activitySelectors = result.split(" "); //$NON-NLS-1$
      for (final String activitySelector : activitySelectors) {
        activities.add(new Activity(new Selector(activitySelector)));
      }
    }
    return activities;
  }

  /**
   * @return baselines
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Baseline> getBaselines() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "lsbl", "-stream", this.getSelector().toString(), "-fmt", "\"%Xn\\n\"" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<Baseline> baselines = new ArrayList<>();
    for (final String baselineSelector : ClearcaseCli.execClearToolCommand(clearToolCommand)) {
      baselines.add(new Baseline(new Selector(baselineSelector)));
    }
    return baselines;
  }

  /**
   * Get components
   * @return components
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Component> getComponents() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "lsstream", "-fmt", "\"%[components]Xp\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final List<String> componentSelectors = ClearcaseCli.execClearToolCommand(clearToolCommand);
    final List<Component> components = new ArrayList<>();
    for (final String componentSelector : componentSelectors) {
      components.add(new Component(new Selector(componentSelector)));
    }
    return components;
  }

  /**
   * Get foundation baselines.
   * @return baselines.
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Baseline> getFoundationBaselines() throws IOException, ClearcaseException {
    final List<String> baselineSelectors = ClearcaseCli.execClearToolCommand("lsstream", "-fmt", "\"%[found_bls]Xp\"", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final List<Baseline> baselines = new ArrayList<>();
    for (final String baselineSelector : baselineSelectors) {
      baselines.add(new Baseline(new Selector(baselineSelector)));
    }
    return baselines;
  }

  /**
   * Get project.
   * @return project.
   * @throws IOException
   * @throws ClearcaseException
   */
  public Project getProject() throws IOException, ClearcaseException {
    final String projectSelector = ClearcaseCli.execClearToolCommandString("lsstream", "-fmt", "\"%[project]Xp\"", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return new Project(new Selector(projectSelector));
  }

  /**
   * Get children streams
   * @return streams.
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Stream> getStreams() throws IOException, ClearcaseException {
    final List<String> streamSelectors =
        ClearcaseCli.execClearToolCommand(new String[] { "lsstream", "-fmt", "\"%Xn\\n\"", "-in", this.getSelector().toString() }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<Stream> streams = new ArrayList<>();
    for (final String streamSelector : streamSelectors) {
      streams.add(new Stream(new Selector(streamSelector)));
    }
    return streams;
  }

  /**
   * Get views' tags linked to the stream.
   * @return stream's view
   * @throws IOException
   * @throws ClearcaseException
   */

  public List<String> getViewTags() throws IOException, ClearcaseException {
    return ClearcaseCli.execClearToolCommand("lsstream", "-fmt", "\"%[views]Np\"", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return <code>true</code> if a deliver is in progress
   * @throws IOException
   * @throws ClearcaseException
   */
  public boolean isDeliverInProgress() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "deliver", "-status", "-stream", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final String result = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    return !StringUtils.startsWithIgnoreCase(result, "No deliver operation in progress on stream"); //$NON-NLS-1$
  }

  /**
   * @param activitySelector_p
   * @param activityHeadline_p
   * @return boolean
   * @throws IOException
   * @throws ClearcaseException
   */
  public Activity makeActivity(final Selector activitySelector_p, final String activityHeadline_p) throws IOException, ClearcaseException {
    final String streamSelector = this.getSelector().toString();
    final String activitySelector = activitySelector_p.toString();
    final String[] clearToolCmd = new String[] { "mkactivity", "-nc", "-headline", "\"" + activityHeadline_p + "\"", "-in", streamSelector, activitySelector }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    final String result = ClearcaseCli.execClearToolCommandString(clearToolCmd);
    if (result.isEmpty()) {
      return null;
    }
    return new Activity(new Selector(activitySelector));
  }

  /**
   * Make a CC UCM dynamic view.
   * @param viewTag_p
   * @param stgLoc_p
   * @return created view
   * @throws IOException
   * @throws ClearcaseException
   */
  public View makeDynamicView(final String viewTag_p, final String stgLoc_p) throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("mkview", //$NON-NLS-1$ 
                                      "-tag", ////$NON-NLS-1$ 
                                      viewTag_p, "-stgloc", //$NON-NLS-1$ 
                                      stgLoc_p, "-stream", //$NON-NLS-1$ 
                                      this.getSelector().toString());
    return new View(viewTag_p);
  }

  /**
   * Make a CC UCM dynamic view at UNC path.
   * @param viewTag_p
   * @param uncPath_p Global path to view storage
   * @return created view
   * @throws IOException
   * @throws ClearcaseException
   */
  public View makeDynamicViewWithViewStorage(final String viewTag_p, final Path uncPath_p) throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("mkview", //$NON-NLS-1$
                                      "-tag", //$NON-NLS-1$
                                      viewTag_p, "-stream", //$NON-NLS-1$
                                      this.getSelector().toString(), uncPath_p.toString());
    return new View(viewTag_p);
  }

  /**
   * Make a snapshot view.
   * @param viewTag_p
   * @param viewLocation_p Path to snapshot view
   * @param stgLoc_p
   * @return created view
   * @throws IOException
   * @throws ClearcaseException
   */
  public View makeSnapshotView(final String viewTag_p, final Path viewLocation_p, final String stgLoc_p) throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("mkview", //$NON-NLS-1$
                                      "-snapshot", //$NON-NLS-1$
                                      "-tag", viewTag_p, //$NON-NLS-1$
                                      "-stgloc", stgLoc_p, //$NON-NLS-1$
                                      "-stream", this.getSelector().toString(), //$NON-NLS-1$
                                      viewLocation_p.toString());
    return new View(viewTag_p);
  }

  /**
   * Make a snapshot view.
   * @param snapshotViewTag_p
   * @param viewLocation_p Path to snapshot view
   * @param uncPath_p Global path to view storage
   * @return created view
   * @throws IOException
   * @throws ClearcaseException
   */
  public View makeSnapshotViewWithViewStorage(final String snapshotViewTag_p, final Path viewLocation_p, final Path uncPath_p)
      throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("mkview", //$NON-NLS-1$
                                      "-snapshot", //$NON-NLS-1$
                                      "-tag", snapshotViewTag_p, //$NON-NLS-1$
                                      "-vws", uncPath_p.toString(), //$NON-NLS-1$
                                      "-stream", this.getSelector().toString(), //$NON-NLS-1$
                                      viewLocation_p.toString());
    return new View(snapshotViewTag_p);
  }
}
