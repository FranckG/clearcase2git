
package org.fgi.clearcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class ActivityTest {

  private static Activity _activity;
  private static String _pVobTag = Vobs.getTag("Test_pvob"); //$NON-NLS-1$

  @Test
  public void testGetContribActivities() throws IOException, ClearcaseException {
    List<Activity> activities = _activity.getContribActivities();
    Selector activitySelector = new Selector("TestDEv1", _pVobTag, CcUcmObjectType.ACTIVITY); // activity:TestDEv1@\Test_pvob //$NON-NLS-1$
    Activity activity = new Activity(activitySelector);
    assertTrue("Cannot get all contributing activities", activities.contains(activity)); //$NON-NLS-1$

    // 2 contributing activities: activity:deliver.fgi_ProjectC.20140604.154053@\Test_pvob
    activitySelector = new Selector("deliver.fgi_ProjectC.20140604.154053", _pVobTag, CcUcmObjectType.ACTIVITY); //$NON-NLS-1$
    activity = new Activity(activitySelector);
    activities = activity.getContribActivities();
    assertEquals(2, activities.size());
  }

  @Test
  public void testGetHeadline() throws IOException, ClearcaseException {
    assertEquals("Cannot get activity headline", "deliver fgi_jiracCCUCM_test on 04/02/2013 16:41:56.", _activity.getHeadline()); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    final Selector activitySelector = new Selector("deliver.fgi_jiracCCUCM_test.20130204.164156", _pVobTag, CcUcmObjectType.ACTIVITY); //$NON-NLS-1$
    _activity = new Activity(activitySelector);
  }

}
