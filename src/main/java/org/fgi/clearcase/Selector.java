
package org.fgi.clearcase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper to handle CC/UCM selector.
 */
public class Selector {

  private String _selector;

  /**
   * @param selector_p activity:test-1@\Test_pvob
   */
  public Selector(final String selector_p) {
    final String selector = selector_p.trim();
    this.setSelector(selector);
  }

  /**
   * VOB
   * @param pVobTag_p
   * @param type_p
   */
  public Selector(final String pVobTag_p, final CcUcmObjectType type_p) {
    this.create(null, pVobTag_p, type_p);
  }

  /**
   * Create a clearcase selector from element name, a vob tag and a type (activity:TEST_1@\Test_pvob)
   * @param name_p
   * @param pVobTag_p
   * @param type_p
   */
  public Selector(final String name_p, final String pVobTag_p, final CcUcmObjectType type_p) {
    this.create(name_p, pVobTag_p, type_p);
  }

  private void create(final String name_p, final String pVobTag_p, final CcUcmObjectType type_p) {
    final String name = StringUtils.isBlank(name_p) ? StringUtils.EMPTY : name_p.trim();
    final String pVobTag = pVobTag_p.trim();

    final StringBuilder sb = new StringBuilder(type_p.getValue());
    sb.append(':');
    sb.append(name);
    if (StringUtils.isNotBlank(name)) {
      // vob selector: vob:\Test_pvob
      sb.append('@');
    }
    sb.append(pVobTag);
    this.setSelector(sb.toString());
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
    final Selector other = (Selector) toCompare_p;
    final String selector = this.getSelector();
    final String otherSelector = other.getSelector();
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
   * @return project Vob tag (\Test_pvob). MAY BE NULL OR EMPTY
   */
  public String getPVobTag() {
    final String selector = this.getSelector();
    final Pattern pattern = Pattern.compile("^\\w+:.*@{0,1}([\\\\/].*)$"); //$NON-NLS-1$
    final Matcher matcher = pattern.matcher(selector);
    String pVobTag = null;
    if (matcher.matches()) {
      pVobTag = matcher.group(1);
    }
    return pVobTag;
  }

  private String getSelector() {
    return this._selector;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_selector == null) ? 0 : _selector.hashCode());
    return result;
  }

  private void setSelector(final String selector_p) {
    this._selector = selector_p;
  }

  /**
   * @see java.lang.Object#toString() return activity:test-1@\Test_pvob or vob:\Test_pvob
   */
  @Override
  public String toString() {
    return this.getSelector();
  }
}
