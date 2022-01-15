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

import com.google.gerrit.entities.Account;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.GroupMembership;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import java.util.List;
import java.util.Optional;
import org.eclipse.jgit.lib.Config;
import org.junit.Before;
import org.junit.Test;

public class SchemeTest {
  private HttpScheme httpScheme;
  private SshScheme sshScheme;

  @Before
  public void setUp() {
    Config cfg = new Config();
    Provider<String> urlProvider = Providers.of("https://gerrit.company.com/");
    Provider<CurrentUser> userProvider = Providers.of(fakeUser());
    DownloadConfig downloadConfig = new DownloadConfig(cfg);
    httpScheme = new HttpScheme(cfg, urlProvider, userProvider, downloadConfig);
    sshScheme =
        new SshScheme(
            List.of("gerrit.company.com:29418"), urlProvider, userProvider, downloadConfig);
  }

  @Test
  public void ensureHttpSchemeEncodedInUrl() {
    assertThat(httpScheme.getUrl("foo"))
        .isEqualTo("https://john-doe%40company.com@gerrit.company.com/a/foo");
  }

  @Test
  public void ensureSshSchemeEncodedInUrl() {
    assertThat(sshScheme.getUrl("foo"))
        .isEqualTo("ssh://john-doe%40company.com@gerrit.company.com:29418/foo");
  }

  private static CurrentUser fakeUser() {
    return new CurrentUser() {
      @Override
      public Optional<String> getUserName() {
        return Optional.of("john-doe@company.com");
      }

      @Override
      public GroupMembership getEffectiveGroups() {
        throw new UnsupportedOperationException("not implemented");
      }

      @Override
      public Object getCacheKey() {
        return new Object();
      }

      @Override
      public boolean isIdentifiedUser() {
        return true;
      }

      @Override
      public Account.Id getAccountId() {
        return Account.id(1);
      }
    };
  }
}
