// Copyright (C) 2013 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.download.command;

import static com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadCommand.DEFAULT_DOWNLOADS;

import com.google.gerrit.extensions.config.DownloadCommand;
import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.gerrit.reviewdb.client.AccountGeneralPreferences;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.reviewdb.client.RefNames;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.gerrit.server.git.GitRepositoryManager;

import com.googlesource.gerrit.plugins.download.scheme.RepoScheme;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

abstract class GitDownloadCommand extends DownloadCommand {
  private static final Logger log =
      LoggerFactory.getLogger(GitDownloadCommand.class);

  private static final String UPLOADPACK = "uploadpack";
  private static final String KEY_HIDE_REFS = "hideRefs";
  private static final String KEY_ALLOW_TIP_SHA1_IN_WANT = "allowTipSha1InWant";

  private final boolean commandAllowed;
  private final GitRepositoryManager repoManager;

  GitDownloadCommand(DownloadConfig downloadConfig,
      AccountGeneralPreferences.DownloadCommand cmd,
      GitRepositoryManager repoManager) {
    this.commandAllowed = downloadConfig.getDownloadCommands().contains(cmd)
        || downloadConfig.getDownloadCommands().contains(DEFAULT_DOWNLOADS);
    this.repoManager = repoManager;
  }

  @Override
  public final String getCommand(DownloadScheme scheme, String project,
      String ref) {
    if (commandAllowed && isRecognizedScheme(scheme)) {
      String url = scheme.getUrl(project);
      if (url != null && isValidUrl(url)) {
        ref = resolveRef(project, ref);
        if (ref != null) {
          return getCommand(url, ref);
        }
      }
    }
    return null;
  }

  private static boolean isRecognizedScheme(DownloadScheme scheme) {
    return !(scheme instanceof RepoScheme);
  }

  private static boolean isValidUrl(String url) {
    try {
      new URIish(url);
      return true;
    } catch (URISyntaxException e) {
      return false;
    }
  }

  private String resolveRef(String project, String ref) {
    if (project.startsWith("$") || ref.startsWith("$")) {
      // No real value but placeholders are being used.
      return ref;
    }

    try (Repository repo = repoManager.openRepository(new Project.NameKey(project))) {
      Config cfg = repo.getConfig();
      if (cfg.getBoolean(UPLOADPACK, KEY_ALLOW_TIP_SHA1_IN_WANT, false)
          && Arrays.asList(cfg.getStringList(UPLOADPACK, null, KEY_HIDE_REFS))
              .contains(RefNames.REFS_CHANGES)) {
        ObjectId id = repo.resolve(ref);
        if (id != null) {
          return id.name();
        } else {
          log.error(String.format("Cannot resolve ref %s in project %s.", ref,
              project));
          return null;
        }
      } else {
        return ref;
      }
    } catch (RepositoryNotFoundException e) {
      log.error(String.format("Missing project: %s",  project), e);
      return null;
    } catch (IOException e) {
      log.error(
          String.format("Failed to lookup project %s from cache.", project), e);
      return null;
    }
  }

  abstract String getCommand(String url, String ref);
}