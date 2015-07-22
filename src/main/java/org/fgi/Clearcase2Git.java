package org.fgi;

import java.io.File;
import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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
		parser.accepts("branch").withRequiredArg();
		parser.accepts("dry");
		OptionSet options = parser.parse(args);
		final String streamSelector = options.valueOf("stream").toString();
		final File repo = repositoryOption.value(options);
		final String branch = options.valueOf("branch").toString();
		final boolean dry = options.has("dry");
		app.getLogger().info("stream is '{}'.", streamSelector);
		app.getLogger().info("Git repository is '{}'.", repo.toString());
		app.getLogger().info("Git branch is '{}'.", branch);
		app.getLogger().info("dry is '{}'.", dry);

		/*
		 * Git repository
		 */
		final FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = null;
		try {
			repository = builder.setGitDir(repo)
													.readEnvironment() // scan environment GIT_* variables
													.findGitDir() // scan up the file system tree
													.build();
		} catch (IOException exception_p) {
			app.getLogger().error("Cannot build Git repository '{}'.", repo.toString());
			app.getLogger().error(exception_p.getMessage());
			System.exit(1);
		}

		// pull repository
		String currentBranch = null;
		try {
			currentBranch = repository.getBranch();
		} catch (IOException exception_p) {
			app.getLogger().error("Cannot get current branch.");
			app.getLogger().error(exception_p.getMessage());
			System.exit(1);
		}

		Git git = new Git(repository);

		if (!StringUtils.equals(branch, currentBranch)) {
			// checkout branch
			try {
				git.checkout().setName(branch).call();
			} catch (GitAPIException exception_p) {
				app.getLogger().error("Cannot checkout branch '{}'.", branch);
				app.getLogger().error(exception_p.getMessage());
				System.exit(1);
			}
		}

		/*
		 * WORKFLOW
		 * loop over baselines
		 * 	rebase using view tag
		 * 	get baseline attributes
		 * 	git rm -rf
		 * 	copy from view to repository
		 * 	git add
		 * 	git commit with baseline attributes
		 * git garbage
		 */

		// Remove working directory and index: git rm -rf -- .
		try {
			git.rm().call();
		} catch (GitAPIException exception_p) {
			app.getLogger().error("Cannot checkout branch '{}'.", branch);
			app.getLogger().error(exception_p.getMessage());
			System.exit(1);
		}

		// Close resources
		git.close();
		repository.close();

		System.exit(0);
	}
}
