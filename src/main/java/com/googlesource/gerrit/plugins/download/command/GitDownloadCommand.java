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
import com.google.gerrit.server.config.DownloadConfig;

import com.googlesource.gerrit.plugins.download.scheme.RepoScheme;

import org.eclipse.jgit.transport.URIish;

import java.net.URISyntaxException;

abstract class GitDownloadCommand extends DownloadCommand {
  private final boolean commandAllowed;

  GitDownloadCommand(
      DownloadConfig downloadConfig, AccountGeneralPreferences.DownloadCommand cmd) {
    this.commandAllowed = downloadConfig.getDownloadCommands().contains(cmd)
        || downloadConfig.getDownloadCommands().contains(DEFAULT_DOWNLOADS);
  }

  @Override
  public final String getCommand(DownloadScheme scheme, String project,
      String ref) {
    if (commandAllowed && isRecognizedScheme(scheme)) {
      String url = scheme.getUrl(project);
      if (url != null && isValidUrl(url)) {
        return getCommand(url, ref);
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

  abstract String getCommand(String url, String ref);
}
