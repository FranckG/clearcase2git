
package org.fgi.clearcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Folder extends AbstractCcObject {

  /**
   * @param selector_p
   */
  public Folder(final Selector selector_p) {
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
    final Folder other = (Folder) toCompare_p;
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
   * Get folders.
   * @return folders. If no project are found, an empty result is returned.
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Folder> getFolders() throws IOException, ClearcaseException {
    final List<String> folderSelectors = ClearcaseCli.execClearToolCommand("lsfolder", "-fmt", "\"%Xn\\n\"", "-in", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<Folder> folders = new ArrayList<>();
    for (final String folderSelector : folderSelectors) {
      folders.add(new Folder(new Selector(folderSelector)));
    }
    return folders;
  }

  /**
   * Get projects.
   * @return projects. If no project, an empty result is returned.
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Project> getProjects() throws IOException, ClearcaseException {
    final List<String> projectSelectors = ClearcaseCli.execClearToolCommand("lsproject", "-fmt", "\"%Xn\\n\"", "-in", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<Project> projects = new ArrayList<>();
    for (final String projectSelector : projectSelectors) {
      projects.add(new Project(new Selector(projectSelector)));
    }
    return projects;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
