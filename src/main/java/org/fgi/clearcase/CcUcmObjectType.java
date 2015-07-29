
package org.fgi.clearcase;


public enum CcUcmObjectType {
  /**
   * Clearcase UCM Activity
   */
  ACTIVITY("activity"), //$NON-NLS-1$

  /**
   * Clearcase UCM Attribute type
   */
  ATTRIBUTE_TYPE("attype"), //$NON-NLS-1$

  /**
   * Clearcase UCM Baseline
   */
  BASELINE("baseline"), //$NON-NLS-1$

  /**
   * Clearcase UCM Component
   */
  COMPONENT("component"), //$NON-NLS-1$

  /**
   * Clearcase UCM Folder
   */
  FOLDER("folder"), //$NON-NLS-1$

  /**
   * Clearcase UCM Project
   */
  PROJECT("project"), //$NON-NLS-1$

  /**
   * Clearcase UCM Stream
   */
  STREAM("stream"), //$NON-NLS-1$

  /**
   * Clearcase UCM Vob
   */
  VOB("vob"); //$NON-NLS-1$

  private String value;

  private CcUcmObjectType(final String value_p) {
    this.value = value_p;
  }

  /**
   * @return {@link String}
   */
  public String getValue() {
    return this.value;
  }
}
