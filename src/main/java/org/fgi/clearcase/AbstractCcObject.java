
package org.fgi.clearcase;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import org.fgi.clearcase.internal.OneShotProcessExecutor;


public abstract class AbstractCcObject {

  private Selector _selector;
  private String _comment;
  private String _name;
  private Vob _pVob;
  private Boolean _isLocked;

  /**
   * @param selector_p
   */
  public AbstractCcObject(final Selector selector_p) {
    // on some CC versions it may have a leading white space
    this._selector = selector_p;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final AbstractCcObject other = (AbstractCcObject) obj;
    if (this._selector == null) {
      if (other._selector != null) {
        return false;
      }
    } else if (!this._selector.equals(other._selector)) {
      return false;
    }
    return true;
  }

  /**
   * @param attributeType_p
   * @return cleartool command output
   * @throws IOException
   * @throws ClearcaseException
   */
  public String getAttribute(final AttributeType attributeType_p) throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "describe", "-short", "-aattr", attributeType_p.getSelector().toString(), this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final String result = ClearcaseCli.execClearToolCommandString(clearToolCommand);

    // remove "
    return result.replaceAll("\"", StringUtils.EMPTY); //$NON-NLS-1$
  }

  /**
   * @return object name
   * @throws IOException
   * @throws ClearcaseException
   */
  public String getName() throws IOException, ClearcaseException {
    if (StringUtils.isBlank(this._name)) {
      final String[] clearToolCommand = new String[] { "describe", "-fmt", "\"%n\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      this._name = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    }
    return this._name;

  }

  /**
   * @return comment
   * @throws ClearcaseException
   * @throws IOException
   */
  public String getComment() throws IOException, ClearcaseException {
    if (StringUtils.isBlank(this._comment)) {
      final String[] clearToolCommand = new String[] { "describe", "-fmt", "\"%c\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      this._comment = ClearcaseCli.execClearToolCommandString(clearToolCommand);
    }
    return this._comment;
  }

  /**
   * @return project vob
   * @throws ClearcaseException
   * @throws IOException
   */
  public Vob getPVob() throws IOException, ClearcaseException {
    if (null == this._pVob) {
      final String pVobTag = this.getSelector().getPVobTag();
      final Selector pVobSelector = new Selector(pVobTag, CcUcmObjectType.VOB);
      this._pVob = new Vob(pVobSelector);
    }
    return this._pVob;
  }

  /**
   * Create a ccucm _selector from element name, a vob name and a type (activity:TEST_1@Test_pvob)
   * @return ccucm _selector
   */
  public Selector getSelector() {
    return this._selector;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this._selector == null ? 0 : this._selector.hashCode());
    return result;
  }

  /**
   * @return <code>true</code> if locked
   * @throws IOException
   * @throws ClearcaseException
   */
  public boolean isLocked() throws IOException, ClearcaseException {
    if (null == this._isLocked) {
      final String[] clearToolCommand = new String[] { "lslock", this.getSelector().toString() }; //$NON-NLS-1$
      final String result = ClearcaseCli.execClearToolCommandString(clearToolCommand);
      this._isLocked = result.equals(StringUtils.EMPTY) ? Boolean.FALSE : Boolean.TRUE;
    }
    return this._isLocked.booleanValue();

  }

  /**
   * @param obsolete_p
   * @return command output
   * @throws IOException
   * @throws ClearcaseException
   */
  public String lock(final boolean obsolete_p) throws IOException, ClearcaseException {
    String[] clearToolCommand;
    if (obsolete_p) {
      clearToolCommand = new String[] { "lock", "-obsolete", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$
    } else {
      clearToolCommand = new String[] { "lock", this.getSelector().toString() }; //$NON-NLS-1$
    }
    this._isLocked = Boolean.TRUE;
    return ClearcaseCli.execClearToolCommandString(clearToolCommand);
  }

  /**
   * @param attributeType_p
   * @param value_p
   * @param comment_p
   * @return <code>true</code> if attribute was set, <code>false</code> otherwise
   * @throws IOException
   * @throws ClearcaseException
   */
  public boolean setAttribute(final AttributeType attributeType_p, final String value_p, final String comment_p) throws IOException, ClearcaseException {
    if (SystemUtils.IS_OS_WINDOWS) {
      final String[] clearToolCommand = new String[] { "mkattr", //$NON-NLS-1$
                                                      "-c", //$NON-NLS-1$
                                                      comment_p, "-replace", //$NON-NLS-1$
                                                      attributeType_p.getSelector().toString(), "\\\"" + value_p + "\\\"", //$NON-NLS-1$ //$NON-NLS-2$
                                                      this.getSelector().toString() };
      ClearcaseCli.execClearToolCommand(clearToolCommand);
    } else {
      final StringBuilder sb = new StringBuilder(ClearcaseCli.CLEARTOOL);
      sb.append(" mkattr -c \""); //$NON-NLS-1$
      sb.append(comment_p);
      sb.append("\" -replace "); //$NON-NLS-1$
      sb.append(attributeType_p);
      sb.append(" \\\""); //$NON-NLS-1$
      sb.append(value_p);
      sb.append("\\\" "); //$NON-NLS-1$
      sb.append(this.getSelector());
      final String[] command = new String[] { "/bin/sh", "-c", sb.toString() }; //$NON-NLS-1$ //$NON-NLS-2$
      new OneShotProcessExecutor().executeCommand(command);
    }

    return true;
  }

  protected void setSelector(final Selector selector_p) {
    this._selector = selector_p;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getSelector().toString();
  }

  /**
   * @return command output
   * @throws IOException
   * @throws ClearcaseException
   */
  public String unlock() throws IOException, ClearcaseException {
    final String[] clearToolCommand = new String[] { "unlock", this.getSelector().toString() }; //$NON-NLS-1$
    return ClearcaseCli.execClearToolCommandString(clearToolCommand);
  }
}
