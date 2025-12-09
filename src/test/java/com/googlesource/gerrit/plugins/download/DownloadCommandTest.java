// Copyright (C) 2023 The Android Open Source Project
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
package com.googlesource.gerrit.plugins.download;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gerrit.entities.Account;
import com.google.gerrit.extensions.client.GitBasicAuthPolicy;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.GroupMembership;
import com.google.gerrit.server.account.externalids.ExternalId;
import com.google.gerrit.server.config.AuthConfig;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import com.googlesource.gerrit.plugins.download.scheme.HttpScheme;
import com.googlesource.gerrit.plugins.download.scheme.SshScheme;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import org.eclipse.jgit.lib.Config;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;
import org.mockito.Mockito;

@Ignore
public class DownloadCommandTest {

  public static class TestUser extends CurrentUser {
    private ImmutableSet<ExternalId.Key> externalIds = ImmutableSet.of();

    public void setExternalIds(Set<ExternalId.Key> keys) {
      this.externalIds = ImmutableSet.copyOf(keys);
    }

    @Override
    public Optional<String> getUserName() {
      return Optional.of(ENV.userName);
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

    @Override
    public ImmutableSet<ExternalId.Key> getExternalIdKeys() {
      return externalIds;
    }
  }

  static CurrentUser fakeUser() {
    return new TestUser();
  }

  public static class TestEnvironment {
    public final String fqdn = "gerrit.company.com";
    public final String projectName = "my/project";
    public final String userName = "john-doe@company.com";
    public final int sshPort = 29418;
    public final int sshdAdvertisedPrimaryAddress = 39418;

    public String urlEncodedUserName() throws UnsupportedEncodingException {
      return URLEncoder.encode(userName, StandardCharsets.UTF_8.name());
    }

    public String canonicalUrl() {
      return "https://" + fqdn + "/";
    }
  }

  protected static final String PLUGIN_NAME = "download-commands";
  protected static TestEnvironment ENV = new TestEnvironment();

  protected HttpScheme httpScheme;
  protected SshScheme sshScheme;
  protected Provider<String> urlProvider;
  protected Provider<CurrentUser> userProvider;
  protected @Mock AuthConfig authConfig;

  public DownloadCommandTest() {
    super();
  }

  @Before
  public void setUp() {
    PluginConfigFactory configFactory = Mockito.mock(PluginConfigFactory.class);
    Mockito.when(configFactory.getFromGerritConfig(PLUGIN_NAME))
        .thenReturn(PluginConfig.createFromGerritConfig(PLUGIN_NAME, new Config()));

    urlProvider = Providers.of(ENV.canonicalUrl());

    Config cfg = new Config();
    DownloadConfig downloadConfig = new DownloadConfig(cfg);

    userProvider = Providers.of(fakeUser());

    authConfig = Mockito.mock(AuthConfig.class);
    Mockito.when(authConfig.getGitBasicAuthPolicy()).thenReturn(GitBasicAuthPolicy.HTTP);

    httpScheme = new HttpScheme(cfg, urlProvider, userProvider, downloadConfig, authConfig);
    sshScheme =
        new SshScheme(
            ImmutableList.of(String.format("%s:%d", ENV.fqdn, ENV.sshPort)),
            PLUGIN_NAME,
            configFactory,
            urlProvider,
            userProvider,
            downloadConfig);
  }
}
