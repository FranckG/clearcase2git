
package org.fgi.clearcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Component extends AbstractCcObject {

  /**
   * @param selector_p
   */
  public Component(final Selector selector_p) {
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
    final Component other = (Component) toCompare_p;
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
   * Returns the root_dir.
   * @return root dir
   * @throws IOException
   * @throws ClearcaseException
   */
  public String getRootDir() throws IOException, ClearcaseException {
    return ClearcaseCli.execClearToolCommandString(new String[] { "lscomp", "-fmt", "\"%[root_dir]p\"", this.getSelector().toString() }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return all baselines made on this component
   * @throws ClearcaseException
   * @throws IOException
   */
  public List<Baseline> getBaselines() throws IOException, ClearcaseException {
    final List<String> baselineSelectors =
        ClearcaseCli.execClearToolCommand(new String[] { "lsbl", "-fmt", "\"%Xn\\n\"", "-component", this.getSelector().toString() }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<Baseline> baselines = new ArrayList<>();
    for (final String baselineSelector : baselineSelectors) {
      baselines.add(new Baseline(new Selector(baselineSelector)));
    }
    return baselines;

  }
}
