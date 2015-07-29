
package org.fgi.clearcase;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class VobTest {

  private static Vob _pVob;
  private static String _pVobTag = Vobs.getTag("Test_pvob"); //$NON-NLS-1$

  @Test
  public void testExists() throws IOException, ClearcaseException {
    final Selector selector = new Selector(_pVobTag, CcUcmObjectType.VOB);
    assertTrue("Cannot test vob " + _pVobTag + " if exists", ClearcaseHelper.exist(selector)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Test
  public void testGetComponents() throws IOException, ClearcaseException {
    final List<Component> components = _pVob.getComponents();
    final Selector componentSelector = new Selector("Test", _pVobTag, CcUcmObjectType.COMPONENT); //$NON-NLS-1$
    final Component component = new Component(componentSelector);
    assertTrue("Cannot get Vob components", components.contains(component)); //$NON-NLS-1$
  }

  @Test
  public void testGetFolders() throws IOException, ClearcaseException {
    final List<Folder> folders = _pVob.getFolders();
    final Selector folderSelector = new Selector("JiraCCUCM", _pVobTag, CcUcmObjectType.FOLDER); //$NON-NLS-1$
    final Folder folder = new Folder(folderSelector);
    assertTrue("Cannot get Vob folders", folders.contains(folder)); //$NON-NLS-1$
  }

  @Test
  public void testGetProjects() throws IOException, ClearcaseException {
    final List<Project> projects = _pVob.getProjects();
    final Selector projectSelector = new Selector("jiracCCUCM", _pVobTag, CcUcmObjectType.PROJECT); //$NON-NLS-1$
    final Project project = new Project(projectSelector);
    assertTrue("Cannot get Vob projects", projects.contains(project)); //$NON-NLS-1$
  }

  @Test
  public void testGetStreams() throws IOException, ClearcaseException {
    final List<Stream> streams = _pVob.getStreams();
    final Selector streamSelector = new Selector("jiracCCUCM_int", _pVobTag, CcUcmObjectType.STREAM); //$NON-NLS-1$
    final Stream stream = new Stream(streamSelector);
    assertTrue("Cannot get Vob projects", streams.contains(stream)); //$NON-NLS-1$
  }

  @Test
  public void testIsMounted() throws IOException, ClearcaseException {
    if (!_pVob.isMounted()) {
      _pVob.mounts();
    }
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    final Selector vobSelector = new Selector(_pVobTag, CcUcmObjectType.VOB);
    _pVob = new Vob(vobSelector);
  }

}
