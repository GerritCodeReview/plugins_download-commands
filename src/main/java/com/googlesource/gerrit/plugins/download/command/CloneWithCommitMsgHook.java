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

import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.gerrit.server.CurrentUser;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.googlesource.gerrit.plugins.download.scheme.SshScheme;

public class CloneWithCommitMsgHook extends CloneCommand {
  private final SshScheme sshScheme;
  private final Provider<CurrentUser> userProvider;

  @Inject
  CloneWithCommitMsgHook(SshScheme sshScheme,
      Provider<CurrentUser> userProvider) {
    this.sshScheme = sshScheme;
    this.userProvider = userProvider;
  }

  @Override
  public String getCommand(DownloadScheme scheme, String project) {
    String username = userProvider.get().getUserName();
    if (!sshScheme.isEnabled() || username == null) {
      return null;
    }

    StringBuilder b = new StringBuilder();
    b.append(super.getCommand(scheme, project))
     .append(" && scp -p -P ")
     .append(sshScheme.getSshdPort())
     .append(" ")
     .append(username)
     .append("@")
     .append(sshScheme.getSshdHost())
     .append(":hooks/commit-msg ")
     .append(project)
     .append("/.git/hooks/");
    return b.toString();
  }
}
