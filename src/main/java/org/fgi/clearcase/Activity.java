
package org.fgi.clearcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class Activity extends AbstractCcObject {
  private Boolean _isLeafActivity;
  private String _headline;
  private String _changeSet;

  /**
   * @param selector_p
   */
  public Activity(final Selector selector_p) {
    super(selector_p);
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
    if (this.getClass() != toCompare_p.getClass()) {
      return false;
    }
    final Activity other = (Activity) toCompare_p;
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
   * rely on being inside a view context to get changeset
   * @param view_p
   * @return changeset
   * @throws IOException
   * @throws ClearcaseException
   */
  public String getChangeSet(final View view_p) throws IOException, ClearcaseException {
    if (StringUtils.isBlank(this._changeSet)) {
      String formatedResult = StringUtils.EMPTY;
      final String[] clearToolCommand = new String[] { "lsact", "-fmt", "\"%[versions]CQp\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      final String result = ClearcaseCli.execClearToolCommandString(view_p.getPath(), clearToolCommand);

      final String viewPath = Views.getViewRoot(view_p.getPath()); // pwv needs to be in a view context !

      if (StringUtils.isNotBlank(result.trim())) {
        for (String token : result.split("\\s*,\\s+")) { //$NON-NLS-1$
          token = token.replace("\"", StringUtils.EMPTY); //$NON-NLS-1$
          token = token.replace(viewPath + "\\", StringUtils.EMPTY); //$NON-NLS-1$
          formatedResult += token + "\n"; //$NON-NLS-1$
        }
      }
      this._changeSet = formatedResult;
    }

    return this._changeSet;
  }

  /**
   * @return activities
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Activity> getContribActivities() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "describe", "-fmt", "\"%[contrib_acts]Xp\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final String result = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    final String[] activitySelectors = result.split(" "); //$NON-NLS-1$
    final List<Activity> activities = new ArrayList<Activity>();
    for (final String activitySelector : activitySelectors) {
      if (StringUtils.isNotBlank(activitySelector)) {
        activities.add(new Activity(new Selector(activitySelector)));
      }
    }
    return activities;
  }

  /**
   * @return headline
   * @throws ClearcaseException
   * @throws IOException
   */
  public String getHeadline() throws IOException, ClearcaseException {
    if (StringUtils.isBlank(this._headline)) {
      final String[] clearToolCommand = new String[] { "describe", "-fmt", "\"%[headline]p\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      this._headline = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    }
    return this._headline;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @param view_p
   * @return <code>true</code> if there is a change set, <code>false</code> otherwise.<br/>
   *         Relies on being in a view context
   * @throws ClearcaseException
   * @throws IOException
   */
  public boolean hasChangeSet(final View view_p) throws IOException, ClearcaseException {
    if (StringUtils.isBlank(this.getChangeSet(view_p))) {
      return false;
    }
    return true;
  }

  /**
   * Is this {@link Activity} a leaf activity? A leaf activity is an activity without contributors.
   * @return <code>true</code> if there is no contributors, <code>false</code> otherwise
   * @throws ClearcaseException
   * @throws IOException
   */
  public boolean isLeafActivity() throws IOException, ClearcaseException {
    if (this._isLeafActivity == null) {
      this._isLeafActivity = Boolean.TRUE;
      final List<Activity> contributors = this.getContribActivities();
      if (contributors.size() > 0) {
        this._isLeafActivity = Boolean.FALSE;
      }
    }
    return this._isLeafActivity.booleanValue();
  }
}
