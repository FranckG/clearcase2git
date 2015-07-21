package org.fgi;

import java.io.File;
import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fuel a Git repository with Clearcase/UCM baselines
 */
public class Clearcase2Git {
	final Logger _logger = LoggerFactory.getLogger(Clearcase2Git.class);

	public Logger getLogger() {
		return _logger;
	}

	public static void main(String[] args) {
		Clearcase2Git app = new Clearcase2Git();

		/*
		 * Parse options
		 */
		OptionParser parser = new OptionParser();
		OptionSpec<File> repositoryOption = parser.accepts("repository").withRequiredArg().ofType(File.class);
		parser.accepts("stream").withRequiredArg();
		parser.accepts("dry");
		OptionSet options = parser.parse(args);
		app.getLogger().info("stream is '{}'.", options.valueOf("stream"));
		app.getLogger().info("Git repository is '{}'.", repositoryOption.value(options));
		app.getLogger().info("dry is '{}'.", options.has("dry"));

		/*
		 * Git repository
		 */
		final FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = null;
		try {
			repository = builder.setGitDir(repositoryOption.value(options))
					.readEnvironment() // scan environment GIT_* variables
					.findGitDir() // scan up the file system tree
					.build();
		} catch (IOException e) {
			app.getLogger().error("Cannot build Git repository '{}'.", repositoryOption.value(options));
			app.getLogger().error(e.getMessage());
			System.exit(1);
		}
		repository.close();
		System.exit(0);
	}

}
