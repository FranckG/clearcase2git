
package org.fgi.clearcase;

/**
 * This enum represents CC UCM projects and streams policies states.
 */
public enum PolicySetting {
  /**
   * Policy is disabled.
   */
  DISABLED("disabled"), //$NON-NLS-1$

  /**
   * Policy is enabled.
   */
  ENABLED("enabled"), //$NON-NLS-1$ 

  /**
   * Policy state is resolved at stream level (available only at project level !).
   */
  ENABLED_BY_STREAM("enabled by stream"); //$NON-NLS-1$

  private final String _policySettingName;

  /**
   * Constructor.
   * @param policySettingName_p
   */
  private PolicySetting(final String policySettingName_p) {
    _policySettingName = policySettingName_p;
  }

  /**
   * @return the policySettingName
   */
  public String getPolicySettingName() {
    return _policySettingName;
  }

  /**
   * Get an enum entry from its String representation.
   * @param policySettingName_p
   * @return an enum value or <code>null</code> if an enum value can't be found from given String representation.
   */
  public static PolicySetting fromString(final String policySettingName_p) {
    for (final PolicySetting value : values()) {
      if (value.getPolicySettingName().equals(policySettingName_p)) {
        return value;
      }
    }
    return null;
  }
}
