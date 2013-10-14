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

import static com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadScheme.DEFAULT_DOWNLOADS;
import static com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadScheme.SSH;

import com.google.common.base.Strings;
import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.gerrit.server.ssh.SshAdvertisedAddresses;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.List;

@Listen
public class SshScheme extends DownloadScheme {
  private final String sshdAddress;
  private final Provider<CurrentUser> userProvider;
  private final boolean schemeAllowed;

  @Inject
  SshScheme(@SshAdvertisedAddresses List<String> sshAddresses, Provider<CurrentUser> userProvider,
      DownloadConfig downloadConfig) {
    this.sshdAddress = Strings.emptyToNull(!sshAddresses.isEmpty() ? sshAddresses.get(0) : null);
    this.userProvider = userProvider;
    this.schemeAllowed = downloadConfig.getDownloadSchemes().contains(SSH)
        || downloadConfig.getDownloadSchemes().contains(DEFAULT_DOWNLOADS);
  }

  @Override
  public String getName() {
    return "ssh";
  }

  @Override
  public String getUrl(String project) {
    if (!isEnabled() || !userProvider.get().isIdentifiedUser()) {
      return null;
    }

    StringBuilder r = new StringBuilder();
    r.append("ssh://");
    r.append(userProvider.get().getUserName());
    r.append("@");
    r.append(ensureSlash(sshdAddress));
    r.append(project);
    return r.toString();
  }

  @Override
  public boolean isEnabled() {
    return schemeAllowed && sshdAddress != null;
  }

  @Override
  public boolean isAuthRequired() {
    return true;
  }

  private static String ensureSlash(String in) {
    if (in != null && !in.endsWith("/")) {
      return in + "/";
    }
    return in;
  }
}
