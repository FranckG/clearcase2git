package org.fgi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.junit.Test;

/**
 * Unit test for simple Clearcase2Git.
 */
public class ArgsParserTest {
	@Test
	public void parserShouldFailOnNoRepository() {
		OptionParser parser = new OptionParser();
		parser.accepts("stream");
		parser.accepts("dry");
		OptionSet options = parser.parse("--stream", "fgiTest@/Test_pvob", "--dry", "--repository", "1");
		assertTrue(options.has("stream"));
		assertFalse(options.has("verbose"));
	}
}
