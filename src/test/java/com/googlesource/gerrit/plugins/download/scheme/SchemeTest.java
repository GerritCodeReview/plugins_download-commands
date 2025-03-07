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
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.googlesource.gerrit.plugins.download.DownloadCommandTest;
import org.eclipse.jgit.lib.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SchemeTest extends DownloadCommandTest {
  @Mock private PluginConfigFactory pluginConfigFactoryMock;

  @Test
  public void ensureHttpSchemeEncodedInUrl() throws Exception {
    assertThat(httpScheme.getUrl(ENV.projectName))
        .isEqualTo(
            String.format(
                "https://%s@%s/a/%s", ENV.urlEncodedUserName(), ENV.fqdn, ENV.projectName));
  }

  @Test
  public void ensureSshSchemeEncodedInUrlWithUserName() throws Exception {
    assertThat(sshScheme.getUrl(ENV.projectName))
        .isEqualTo(
            String.format(
                "ssh://%s@%s:%d/%s",
                ENV.urlEncodedUserName(), ENV.fqdn, ENV.sshPort, ENV.projectName));
  }

  @Test
  public void ensureSshSchemeNameEncodedInUrlWithoutUserName() {
    assertThat(getSshSchemeUrlWithoutUserName())
        .isEqualTo(String.format("ssh://%s:%d/%s", ENV.fqdn, ENV.sshPort, ENV.projectName));
  }

  private String getSshSchemeUrlWithoutUserName() {
    PluginConfig.Update pluginConfig = PluginConfig.Update.forTest(PLUGIN_NAME, new Config());
    pluginConfig.setBoolean("sshIncludeUserName", false);
    when(pluginConfigFactoryMock.getFromGerritConfig(PLUGIN_NAME))
        .thenReturn(pluginConfig.asPluginConfig());
    SshScheme scheme =
        new SshScheme(
            ImmutableList.of(String.format("%s:%d", ENV.fqdn, ENV.sshPort)),
            PLUGIN_NAME,
            pluginConfigFactoryMock,
            urlProvider,
            userProvider,
            new DownloadConfig(new Config()));
    return scheme.getUrl(ENV.projectName);
  }
}
