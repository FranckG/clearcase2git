
package org.fgi.clearcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Project extends AbstractCcObject {

  /**
   * @param selector_p
   */
  public Project(final Selector selector_p) {
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
    final Project other = (Project) toCompare_p;
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
   * Get the ClearQuest database. If project is not ClearQuest enabled <code>null</code> is returned.
   * @return ClearQuestDatabase name
   * @throws IOException
   * @throws ClearcaseException
   */
  public String getClearQuestDataBaseName() throws IOException, ClearcaseException {
    final String clearQuestDataBase = ClearcaseCli.execClearToolCommandString("lsproject", "-fmt", "\"%[crm_database]p\"", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    // If this project is not CQ enabled -> ": CRM Suspended" is returned.
    // Actually, if returned value is empty or contains whitespace characters, consider the project as not CQ enabled.
    final Pattern containsWhitespacePattern = Pattern.compile("\\s"); //$NON-NLS-1$
    if (clearQuestDataBase.isEmpty() || containsWhitespacePattern.matcher(clearQuestDataBase).find()) {
      return null;
    }
    return clearQuestDataBase;
  }

  /**
   * Returns the parent folder
   * @return folder.
   * @throws IOException
   * @throws ClearcaseException
   */
  public Folder getFolder() throws IOException, ClearcaseException {
    final String folderSelector = ClearcaseCli.execClearToolCommandString("lsproject", "-fmt", "\"%[folder]Xp\"", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return new Folder(new Selector(folderSelector));
  }

  /**
   * Get integration stream.
   * @return integration stream.
   * @throws IOException
   * @throws ClearcaseException
   */
  public Stream getIntegrationStream() throws IOException, ClearcaseException {
    final String instegrationStreamSelector = ClearcaseCli.execClearToolCommandString("lsproject", "-fmt", "\"%[istream]Xp\"", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return new Stream(new Selector(instegrationStreamSelector));
  }

  /**
   * Get policies. ClearQuest policies are not returned.
   * @return Map of project policies
   * @throws IOException
   * @throws ClearcaseException
   */
  public Map<String, PolicySetting> getPolicies() throws IOException, ClearcaseException {
    // Get policies of given element.
    final String[] clearToolCommand = { "lsproject", "-fmt", "\"%[policies]Cp\"", this.getSelector().toString() }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final List<String> policyDescriptions = ClearcaseCli.execClearToolCommand(clearToolCommand);
    // Initialize result map.
    final Map<String, PolicySetting> result = new HashMap<String, PolicySetting>();
    // lsproject -fmt "%[policies]Cp" result (extract, tabulations/spaces are not identical to command line output) :
    // POLICY_CHSTREAM_UNRESTRICTED enabled
    // POLICY_DELIVER_NCO_SELACT disabled
    // POLICY_REBASE_CO disabled
    // clearquest policies:
    // "Perform ClearQuest Action Before WorkOn" disabled

    // Regular expression used to parse policies.
    // group(1) -> policy's name, group(2) -> policy's value.

    final String clearQuestPolicies = "clearquest policies:"; //$NON-NLS-1$
    final Pattern policyPattern = Pattern.compile("\\s*(\\w+)\\s+((?:\\w+\\s)*\\w+)\\s*"); //$NON-NLS-1$
    for (final String policyDescription : policyDescriptions) {
      // Ignore ClearQuest policies.
      if (policyDescription.contains(clearQuestPolicies)) {
        break;
      }
      final Matcher policyMatcher = policyPattern.matcher(policyDescription);
      final boolean matches = policyMatcher.matches();
      if (!matches) {
        throw new ClearcaseException(clearToolCommand, "", "A policy setting can''t be matched (current line : " + policyDescription + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
      final PolicySetting policySetting = PolicySetting.fromString(policyMatcher.group(2));
      if (null == policySetting) {
        throw new ClearcaseException(clearToolCommand, "", "A policy setting is unknown (current line : " + policyDescription + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
      result.put(policyMatcher.group(1), policySetting);
    }
    return result;
  }

  /**
   * Get streams.
   * @return streams.
   * @throws IOException
   * @throws ClearcaseException
   */
  public List<Stream> getStreams() throws IOException, ClearcaseException {
    final List<String> streamSelectors = ClearcaseCli.execClearToolCommand("lsstream", "-fmt", "\"%Xn\\n\"", "-in", this.getSelector().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final List<Stream> streams = new ArrayList<>();
    for (final String streamSelector : streamSelectors) {
      streams.add(new Stream(new Selector(streamSelector)));
    }
    return streams;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Create an integration stream.
   * @param integrationStreamSelector_p
   * @param baseline_p
   * @return created stream
   * @throws IOException
   * @throws ClearcaseException
   */
  public Stream makeIntegrationStream(final Selector integrationStreamSelector_p, final Baseline baseline_p) throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("mkstream", //$NON-NLS-1$
        "-integration", //$NON-NLS-1$
        "-in", this.getSelector().toString(), //$NON-NLS-1$
        "-baseline", baseline_p.getSelector().toString(), //$NON-NLS-1$
        integrationStreamSelector_p.toString());
    return new Stream(integrationStreamSelector_p);
  }

  /**
   * Create a stream
   * @param streamSelector_p
   * @return created stream
   * @throws IOException
   * @throws ClearcaseException
   */
  public Stream makeStream(final Selector streamSelector_p) throws IOException, ClearcaseException {
    ClearcaseCli.execClearToolCommand("mkstream", "-in", this.getSelector().toString(), streamSelector_p.toString()); //$NON-NLS-1$ //$NON-NLS-2$
    return new Stream(streamSelector_p);
  }

}
