
package org.fgi.clearcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class ProjectTest {

  private static Stream _integrationStream;
  private static Project _project;
  private static String _pVobTag = Vobs.getTag("Test_pvob"); //$NON-NLS-1$

  @Test
  public void testGetAttribute() throws IOException, ClearcaseException {
    final Selector selector = new Selector("JIRA_transition1"); //$NON-NLS-1$
    final AttributeType attributeType = new AttributeType(selector);
    assertEquals("Cannot get project attribute", "StartWork", _project.getAttribute(attributeType)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Test
  public void testGetFolder() throws IOException, ClearcaseException {
    assertEquals("Cannot get project folder", "JiraCCUCM", _project.getFolder().getName()); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Test
  public void testGetIntegrationStream() throws IOException, ClearcaseException {
    assertEquals("Cannot get project integration stream", _integrationStream, _project.getIntegrationStream()); //$NON-NLS-1$
  }

  @Test
  public void testGetStreams() throws IOException, ClearcaseException {
    final List<Stream> streams = _project.getStreams();
    assertTrue("Cannot get Vob streams", streams.contains(_integrationStream)); //$NON-NLS-1$
  }

  @Test
  public void testMakeStream() throws IOException, ClearcaseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); //$NON-NLS-1$
    final Date date = new Date(); // get current date time with Date()
    final Selector streamSelector = new Selector(dateFormat.format(date), _pVobTag, CcUcmObjectType.STREAM);
    _project.makeStream(streamSelector);
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    final Selector projectSelector = new Selector("jiracCCUCM", _pVobTag, CcUcmObjectType.PROJECT); //$NON-NLS-1$
    _project = new Project(projectSelector);
    final Selector integrationStreamSelector = new Selector("jiracCCUCM_int", _pVobTag, CcUcmObjectType.STREAM); //$NON-NLS-1$
    _integrationStream = new Stream(integrationStreamSelector);
  }
}
