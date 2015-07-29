
package org.fgi.clearcase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class Vob extends AbstractCcObject {
  /**
   * @param selector_p
   */
  public Vob(final Selector selector_p) {
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
    if (getClass() != toCompare_p.getClass()) {
      return false;
    }
    final Vob other = (Vob) toCompare_p;
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
   * @return components selectors
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Component> getComponents() throws IOException, ClearcaseException {
    final String[] cleartoolCommand = new String[] { "lscomp", "-fmt", "\"%Xn\\n\"", "-invob", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<String> componentSelectors = ClearcaseCli.execClearToolCommand(cleartoolCommand);
    final List<Component> components = new ArrayList<>();
    for (final String componentSelector : componentSelectors) {
      components.add(new Component(new Selector(componentSelector)));
    }
    return components;
  }

  /**
   * Get folders included in vob.
   * @return folders. If no project are found, an empty result is returned
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Folder> getFolders() throws IOException, ClearcaseException {
    final List<String> folderSelectors = ClearcaseCli.execClearToolCommand("lsfolder", "-fmt", "\"%Xn\\n\"", "-invob", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<Folder> folders = new ArrayList<>();
    for (final String folderSelector : folderSelectors) {
      folders.add(new Folder(new Selector(folderSelector)));
    }
    return folders;
  }

  /**
   * @return the vob name and not the vob tag
   */
  @Override
  public String getName() throws IOException, ClearcaseException {
    final String vobTag = super.getName(); // get vob tag (\Test_pvob)
    return StringUtils.removeStart(vobTag, File.separator);
  }

  /**
   * Get all projects
   * @return projects. If no project in a P_VOB, an empty result is returned.
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Project> getProjects() throws IOException, ClearcaseException {
    final List<String> projectSelectors = ClearcaseCli.execClearToolCommand("lsproject", "-fmt", "\"%Xn\\n\"", "-invob", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<Project> projects = new ArrayList<>();
    for (final String projectSelector : projectSelectors) {
      projects.add(new Project(new Selector(projectSelector)));
    }
    return projects;
  }

  /**
   * Get streams.
   * @return streams selectors
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Stream> getStreams() throws IOException, ClearcaseException {
    final String[] cleartoolCommand = new String[] { "lsstream", "-fmt", "\"%Xn\\n\"", "-invob", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<String> streamSelectors = ClearcaseCli.execClearToolCommand(cleartoolCommand);
    final List<Stream> streams = new ArrayList<>();
    for (final String streamSelector : streamSelectors) {
      streams.add(new Stream(new Selector(streamSelector)));
    }
    return streams;
  }

  /**
   * @return vob tag (\Test_pvob)
   * @throws ClearcaseException
   * @throws IOException
   */
  public String getTag() throws IOException, ClearcaseException {
    return super.getName(); // result of describe -fmt "%n"
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Is given VOB mounted ?
   * @return <code>true</code> if the VOB is mounted, <code>false</code> else.
   * @throws IOException
   * @throws ClearcaseException if given VOB name doesn't exist
   */
  public boolean isMounted() throws IOException, ClearcaseException {
    final String lsViewResult = ClearcaseCli.execClearToolCommandString("lsvob", this.getTag()); //$NON-NLS-1$
    // When a view is started, '*' starts the line describing it.
    if (lsViewResult.startsWith("*")) { //$NON-NLS-1$
      return true;
    }
    return false;
  }

  /**
   * Mount a VOB.
   * @throws IOException
   * @throws ClearcaseException
   */
  public void mounts() throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("mount", this.getTag()); //$NON-NLS-1$
  }

}
