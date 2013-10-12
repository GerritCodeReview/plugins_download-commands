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

import static com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadCommand.REPO_DOWNLOAD;

import com.google.common.base.CaseFormat;
import com.google.gerrit.extensions.config.DownloadCommand;
import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.inject.Inject;

import com.googlesource.gerrit.plugins.download.scheme.RepoScheme;

public class RepoCommand extends DownloadCommand {
  private final boolean commandAllowed;

  @Inject
  RepoCommand(DownloadConfig downloadConfig) {
    this.commandAllowed = downloadConfig.getDownloadCommands().contains(REPO_DOWNLOAD);
  }

  @Override
  public String getName() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, REPO_DOWNLOAD.name());
  }

  @Override
  public String getCommand(DownloadScheme scheme, String project) {
    if (!commandAllowed || !(scheme instanceof RepoScheme)) {
      return null;
    }

    return scheme.getUrl(project) + " ${ref}";
  }
}
