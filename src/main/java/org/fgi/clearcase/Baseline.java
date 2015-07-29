
package org.fgi.clearcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class Baseline extends AbstractCcObject {

  private String _labelStatus;
  private String _promotionLevel;
  private Boolean _isComposite;
  private final String INITIAL_SUFFIX = "_INITIAL"; //$NON-NLS-1$
  private Boolean _isInitial;
  
  /**
   * @param selector_p
   */
  public Baseline(final Selector selector_p) {
    super(selector_p);
  }

  /**
   * @param stream_p
   * @return cleartool command output
   * @throws IOException
   * @throws ClearcaseException
   */
  public String deliver(final Stream stream_p) throws IOException, ClearcaseException {
    return ClearcaseCli.execClearToolCommandString("deliver", //$NON-NLS-1$
                                                   "-baseline", this.getSelector().toString(), //$NON-NLS-1$
                                                   "-target", stream_p.getSelector().toString(), //$NON-NLS-1$
                                                   "-complete", //$NON-NLS-1$
                                                   "-force"); //$NON-NLS-1$
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
    final Baseline other = (Baseline) toCompare_p;
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
   * @return component
   * @throws ClearcaseException
   * @throws IOException
   */
  public Component getComponent() throws IOException, ClearcaseException {
    final String componentSelector = ClearcaseCli.execClearToolCommandString("desc", //$NON-NLS-1$
                                                                             "-fmt", "\"%[component]Xp\"", //$NON-NLS-1$ //$NON-NLS-2$
                                                                             this.getSelector().toString());
    return new Component(new Selector(componentSelector));
  }

  /**
   * @return list of baselines dependencies
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Baseline> getDependencies() throws IOException, ClearcaseException {
    // depends_on_closure only returns one string: cannot rely on \n to split
    final String[] cleartoolCommand = new String[] { "desc", "-fmt", "\"%[depends_on_closure]Cp\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final String result = ClearcaseCli.execClearToolCommandString(cleartoolCommand);
    final List<Baseline> baselines = new ArrayList<>();
    if (StringUtils.isBlank(result)) {
      return baselines;
    }
    final String[] baselineSelectors = result.split(", "); //$NON-NLS-1$
    for (final String baselineSelector : baselineSelectors) {
      baselines.add(new Baseline(new Selector(baselineSelector)));
    }
    return baselines;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return the promotion level
   * @throws ClearcaseException
   * @throws IOException
   */
  public String getPromotionLevel() throws IOException, ClearcaseException {
    if (StringUtils.isBlank(this.getPromotionLevel())) {
      final String[] cleartoolCommand = new String[] { "desc", "-fmt", "\"%[plevel]p\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      final String promotionLevel = ClearcaseCli.execClearToolCommandString(cleartoolCommand);
      this.setPromotionLevel(promotionLevel);
    }
    return this._promotionLevel;
  }

  /**
   * Is the {@link Baseline} composite?
   * @return <code>true</code> if baseline is composite, <code>false</code> otherwise
   * @throws ClearcaseException
   * @throws IOException
   */
  public boolean isComposite() throws IOException, ClearcaseException {
    if (this._isComposite == null) {
      this._isComposite = this.getDependencies().size() > 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    return this._isComposite.booleanValue();
  }

  /**
   * Is the {@link Baseline} an initial baseline?
   * @return <code>true</code> if baseline is composite, <code>false</code> otherwise
   * @throws ClearcaseException 
   * @throws IOException 
   */
  public boolean isInitial() throws IOException, ClearcaseException  {
    if (this._isInitial == null) {
      this._isInitial = StringUtils.endsWith(this.getName(), INITIAL_SUFFIX) ? Boolean.TRUE : Boolean.FALSE;
    }					
    return this._isInitial.booleanValue();
  }
  
  /**
   * @return the label status (full, incremental, or unlabeled)
   * @throws ClearcaseException
   * @throws IOException
   */
  public String getLabelstatus() throws IOException, ClearcaseException {
    if (StringUtils.isBlank(this.getPromotionLevel())) {
      final String[] cleartoolCommand = new String[] { "desc", "-fmt", "\"%[label_status]p\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      final String labelStatus = ClearcaseCli.execClearToolCommandString(cleartoolCommand);
      this.setLabelStatus(labelStatus);
    }
    return this._labelStatus;
  }

  /**
   * @param labelStatus_p
   */
  public void setLabelStatus(final String labelStatus_p) {
    this._labelStatus = labelStatus_p;
  }

  /**
   * @param promotionLevel_p
   */
  public void setPromotionLevel(final String promotionLevel_p) {
    this._promotionLevel = promotionLevel_p;
  }
}
