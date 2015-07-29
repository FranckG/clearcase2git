
package org.fgi.clearcase;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClearcaseCliTest.class, StreamTest.class, ActivityTest.class, VobTest.class, ViewTest.class })
public class AllClearcaseTests {
  // All tests
}
