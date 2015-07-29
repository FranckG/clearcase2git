
package org.fgi.clearcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


@SuppressWarnings("javadoc")
public class StreamTest {

  private static String _pVobTag = Vobs.getTag("Test_pvob"); //$NON-NLS-1$
  private static String _stgloc = "orchestra_viewstore"; //$NON-NLS-1$
  private static Stream _stream;
  private static String _streamName = "fgi_jiracCCUCM_test"; //$NON-NLS-1$
  private static Selector _streamSelector;

  @Rule
  public TemporaryFolder _folder = new TemporaryFolder();

  @Test
  public void testGetActivities() throws IOException, ClearcaseException {
    final List<Activity> activities = _stream.getActivities();
    final Selector activitySelector = new Selector("testActivityName20150204100326", _pVobTag, CcUcmObjectType.ACTIVITY); //$NON-NLS-1$
    final Activity activity = new Activity(activitySelector);
    assertTrue("Cannot get all activies", activities.contains(activity)); //$NON-NLS-1$
  }

  @Test
  public void testGetBaselines() throws IOException, ClearcaseException {
    final List<Baseline> baselines = _stream.getBaselines();
    final Selector baselineSelector = new Selector("test1649", _pVobTag, CcUcmObjectType.BASELINE); //$NON-NLS-1$
    final Baseline baseline = new Baseline(baselineSelector);
    assertTrue("Cannot get all baselines", baselines.contains(baseline)); //$NON-NLS-1$
  }

  @Test
  public void testGetComponents() throws IOException, ClearcaseException {
    final List<Component> components = _stream.getComponents();
    final Selector componentSelector = new Selector("Test", _pVobTag, CcUcmObjectType.COMPONENT); //$NON-NLS-1$
    final Component component = new Component(componentSelector);
    assertTrue("Cannot get all components", components.contains(component)); //$NON-NLS-1$
  }

  @Test
  public void testGetFoundationBaselines() throws IOException, ClearcaseException {
    final List<Baseline> baselines = _stream.getFoundationBaselines();
    final Selector baselineSelector = new Selector("jiracCCUCM_25_09_2012", _pVobTag, CcUcmObjectType.BASELINE); //$NON-NLS-1$
    final Baseline baseline = new Baseline(baselineSelector);
    assertTrue("Cannot get stream foundation baslines", baselines.contains(baseline)); //$NON-NLS-1$
  }

  @Test
  public void testGetProject() throws IOException, ClearcaseException {
    final Selector projectSelector = new Selector("jiracCCUCM", _pVobTag, CcUcmObjectType.PROJECT); //$NON-NLS-1$
    final Project project = new Project(projectSelector);
    assertEquals("Cannot get stream project", project, _stream.getProject()); //$NON-NLS-1$
  }

  @Test
  public void testGetStreams() throws IOException, ClearcaseException {
    final List<Stream> streams = _stream.getStreams();
    final Selector streamSelector = new Selector("fgi_jiracCCUCM_test2", _pVobTag, CcUcmObjectType.STREAM); //$NON-NLS-1$
    final Stream stream = new Stream(streamSelector);
    assertTrue("Cannot get children streams", streams.contains(stream)); //$NON-NLS-1$
  }

  @Test
  public void testMakeActivity() throws IOException, ClearcaseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); //$NON-NLS-1$
    final Date date = new Date(); // get current date time with Date()
    final Selector activitySelector = new Selector(dateFormat.format(date), _pVobTag, CcUcmObjectType.ACTIVITY);
    final Activity activity = _stream.makeActivity(activitySelector, "test"); //$NON-NLS-1$
    assertTrue("Cannot create activity", ClearcaseHelper.exist(activitySelector)); //$NON-NLS-1$
    Activities.remove(activity);
  }

  @Test
  public void testMakeDynamicView() throws IOException, ClearcaseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); //$NON-NLS-1$
    final Date date = new Date(); // get current date time with Date()
    final String viewTag = dateFormat.format(date);
    final View view = _stream.makeDynamicView(viewTag, _stgloc);
    assertTrue("Cannot create view", Views.exist(viewTag)); //$NON-NLS-1$

    final List<String> viewTags = _stream.getViewTags();
    assertTrue("Cannot get view tags", viewTags.contains(viewTag)); //$NON-NLS-1$

    Views.remove(view);
  }

  @Test
  public void testMakeSnapshotView() throws IOException, ClearcaseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); //$NON-NLS-1$
    final Date date = new Date(); // get current date time with Date()
    final String viewTag = dateFormat.format(date);

    final Path snapshotViewLocation = Paths.get(_folder.getRoot().getAbsolutePath()).resolve(viewTag);
    final View view = _stream.makeSnapshotView(viewTag, snapshotViewLocation, _stgloc);
    assertTrue("Cannot create view", Views.exist(viewTag)); //$NON-NLS-1$
    Views.remove(view);
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    _streamSelector = new Selector(_streamName, _pVobTag, CcUcmObjectType.STREAM);
    _stream = new Stream(_streamSelector);
  }
}
