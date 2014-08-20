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

package com.googlesource.gerrit.plugins.download.scheme;

import static com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadScheme.REPO_DOWNLOAD;

import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;

import org.eclipse.jgit.lib.Config;

import java.util.Set;

public class RepoScheme extends DownloadScheme {
  private final boolean schemeAllowed;
  private final String[] mirrors;

  @Inject
  RepoScheme(@GerritServerConfig Config cfg, DownloadConfig downloadConfig) {
    this.schemeAllowed = downloadConfig.getDownloadSchemes().contains(REPO_DOWNLOAD);
    this.mirrors = cfg.getStringList("mirrors", null, "mirrorUrl");
  }

  @Override
  public String getUrl(String project) {
    return project;
  }

  @Override
  public boolean isEnabled() {
    return schemeAllowed;
  }

  @Override
  public boolean isAuthRequired() {
    return false;
  }

  @Override
  public boolean isAuthSupported() {
    return true;
  }

  @Override
  public String[] getMirrors(){
    return mirrors;
  }
}
