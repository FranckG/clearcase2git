
package org.fgi.clearcase;


public class AttributeType extends AbstractCcObject {

  /**
   * @param selector_p
   */
  public AttributeType(final Selector selector_p) {
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
    final AttributeType other = (AttributeType) toCompare_p;
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
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
