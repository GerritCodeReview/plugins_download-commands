// Copyright (C) 2015 The Android Open Source Project
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

import com.google.common.flogger.FluentLogger;
import com.google.gerrit.common.Nullable;
import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.gerrit.server.config.CanonicalWebUrl;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.googlesource.gerrit.plugins.download.scheme.AnonymousHttpScheme;
import com.googlesource.gerrit.plugins.download.scheme.HttpScheme;
import com.googlesource.gerrit.plugins.download.scheme.SshScheme;
import org.eclipse.jgit.lib.Config;

public class CloneWithCommitMsgHook extends CloneCommand {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String HOOK = "commit-msg";
  private static final String HOOKS_DIR = " `git rev-parse --git-dir`/hooks/";

  private final String configCommand;
  private final String extraCommand;
  private final String canonicalWebUrl;

  @Inject
  CloneWithCommitMsgHook(
      @GerritServerConfig Config config, @CanonicalWebUrl @Nullable Provider<String> urlProvider) {
    this.configCommand = config.getString("gerrit", null, "installCommitMsgHookCommand");
    this.extraCommand = config.getString("gerrit", null, "installCommitExtraCommand");
    this.canonicalWebUrl = urlProvider != null ? urlProvider.get() : null;
  }

  @Nullable
  @Override
  public String getCommand(DownloadScheme scheme, String project) {
    String projectName = getBaseName(project);
    StringBuilder command = null;

    if (configCommand != null) {
      command =
          new StringBuilder()
              .append(super.getCommand(scheme, project))
              .append(" && (cd ")
              .append(QuoteUtil.quote(projectName))
              .append(" && ")
              .append(configCommand)
              .append(")");
    }

    if (scheme instanceof HttpScheme
        || scheme instanceof AnonymousHttpScheme
        || scheme instanceof SshScheme) {
      return new StringBuilder()
          .append(super.getCommand(scheme, project))
          .append(" && (cd ")
          .append(QuoteUtil.quote(projectName))
          .append(" && mkdir -p ")
          .append(HOOKS_DIR)
          .append(" && curl -Lo")
          .append(HOOKS_DIR)
          .append(HOOK)
          .append(" ")
          .append(getHookUrl())
          .append("; chmod +x")
          .append(HOOKS_DIR)
          .append(HOOK)
          .append(")")
          .toString();
    }

    if (extraCommand != null && command != null) {
      command
          .append(" && (cd ")
          .append(QuoteUtil.quote(projectName))
          .append(" && ")
          .append(extraCommand)
          .append(")");
    }
    return null;
  }

  private StringBuilder getHookUrl() {
    StringBuilder hookUrl = new StringBuilder();
    if (canonicalWebUrl != null) {
      hookUrl.append(canonicalWebUrl);
      if (!canonicalWebUrl.endsWith("/")) {
        hookUrl.append("/");
      }
      hookUrl.append("tools/hooks/").append(HOOK);
    } else {
      logger.atWarning().log(
          "Cannot add commit-msg hook URL since gerrit.canonicalWebUrl isn't configured.");
    }
    return hookUrl;
  }

  private static String getBaseName(String project) {
    return project.substring(project.lastIndexOf('/') + 1);
  }
}
