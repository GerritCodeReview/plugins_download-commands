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

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.gerrit.server.ssh.SshAdvertisedAddresses;
import com.google.inject.Inject;

import java.util.List;

@Listen
public class SshScheme extends DownloadScheme {
  private final String sshUrl;
  private final String sshdAddress;
  private final boolean schemeAllowed;

  @Inject
  SshScheme(@SshAdvertisedAddresses List<String> sshAddresses,
      DownloadConfig downloadConfig) {
    this.sshUrl =
        !sshAddresses.isEmpty() ? ensureSlash(sshAddresses.get(0)) : null;
    if (sshUrl != null) {
      String sshAddr = sshUrl;
      StringBuilder r = new StringBuilder();
      r.append("ssh://${username}@");
      if (sshAddr.startsWith("*")) {
        sshAddr = sshAddr.substring(1);
      }
      r.append(sshAddr);
      sshdAddress = r.toString();
    } else {
      sshdAddress = null;
    }
    this.schemeAllowed = downloadConfig.getDownloadSchemes().contains(
        com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadScheme.SSH)
        || downloadConfig.getDownloadSchemes().contains(
        com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadScheme.DEFAULT_DOWNLOADS);
  }

  @Override
  public String getName() {
    return "SSH";
  }

  @Override
  public String getUrl(String project) {
    if (!isEnabled()) {
      return null;
    }

    StringBuilder r = new StringBuilder();
    r.append(ensureSlash(sshdAddress));
    r.append(project);
    return r.toString();
  }

  @Override
  public boolean isEnabled() {
    return schemeAllowed && sshUrl != null;
  }

  private static String ensureSlash(String in) {
    if (in != null && !in.endsWith("/")) {
      return in + "/";
    }
    return in;
  }
}
