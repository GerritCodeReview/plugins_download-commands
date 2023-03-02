// Copyright (C) 2022 The Android Open Source Project
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

import static com.google.common.truth.Truth.assertThat;

import com.googlesource.gerrit.plugins.download.DownloadCommandTest;
import org.junit.Test;

public class SchemeTest extends DownloadCommandTest {
  @Test
  public void ensureHttpSchemeEncodedInUrl() throws Exception {
    assertThat(httpScheme.getUrl(ENV.projectName))
        .isEqualTo(
            String.format(
                "https://%s@%s/a/%s", ENV.urlEncodedUserName(), ENV.fqdn, ENV.projectName));
  }

  @Test
  public void ensureSshSchemeEncodedInUrl() throws Exception {
    assertThat(sshScheme.getUrl(ENV.projectName))
        .isEqualTo(
            String.format(
                "ssh://%s@%s:%d/%s",
                ENV.urlEncodedUserName(), ENV.fqdn, ENV.sshPort, ENV.projectName));
  }
}
