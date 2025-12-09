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
package com.googlesource.gerrit.plugins.download.command;

import static com.google.common.truth.Truth.assertThat;
import static com.googlesource.gerrit.plugins.download.command.CloneWithCommitMsgHook.EXTRA_COMMAND_KEY;
import static com.googlesource.gerrit.plugins.download.command.CloneWithCommitMsgHook.HOOKS_DIR;
import static com.googlesource.gerrit.plugins.download.command.CloneWithCommitMsgHook.HOOK_COMMAND_KEY;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gerrit.extensions.client.GitBasicAuthPolicy;
import com.google.gerrit.server.account.externalids.ExternalId;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.googlesource.gerrit.plugins.download.DownloadCommandTest;
import com.googlesource.gerrit.plugins.download.scheme.SshScheme;
import java.io.UnsupportedEncodingException;
import org.eclipse.jgit.lib.Config;
import org.junit.Test;
import org.mockito.Mockito;

public class CloneWithCommitMsgHookTest extends DownloadCommandTest {

  @Test
  public void testSshNoConfiguredCommands() throws Exception {
    String command = getCloneCommand(null, null, null).getCommand(sshScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && %s",
                sshScheme.getUrl(ENV.projectName), getDefaultHookCommand()));
  }

  @Test
  public void testSshConfiguredHookCommand() throws Exception {
    String hookCommand = "my hook command";
    String command =
        getCloneCommand(hookCommand, null, null).getCommand(sshScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s)",
                sshScheme.getUrl(ENV.projectName), baseName(ENV.projectName), hookCommand));
  }

  @Test
  public void testSshConfiguredHookCommandAndPrimaryAddress() throws Exception {
    String hookCommand = "my hook command";
    String primaryAddress = getSshdAdvertisedPrimaryAddress();
    String command =
        getCloneCommand(hookCommand, null, primaryAddress).getCommand(sshScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s) && (cd %s && git remote set-url --push "
                    + "\"$(git config --default origin --get clone.defaultRemoteName)\" \"%s\")",
                sshScheme.getUrl(ENV.projectName),
                baseName(ENV.projectName),
                hookCommand,
                baseName(ENV.projectName),
                sshScheme.getPushUrl(ENV.projectName)));
  }

  @Test
  public void testSshConfiguredExtraCommand() throws Exception {
    String extraCommand = "my extra command";
    String command =
        getCloneCommand(extraCommand, null, null).getCommand(sshScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s)",
                sshScheme.getUrl(ENV.projectName), baseName(ENV.projectName), extraCommand));
  }

  @Test
  public void testSshConfiguredHookAndExtraCommand() throws Exception {
    String hookCommand = "my hook command";
    String extraCommand = "my extra command";
    String command =
        getCloneCommand(hookCommand, extraCommand, null).getCommand(sshScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s) && (cd %s && %s)",
                sshScheme.getUrl(ENV.projectName),
                baseName(ENV.projectName),
                hookCommand,
                baseName(ENV.projectName),
                extraCommand));
  }

  @Test
  public void testSshConfiguredHookExtraCommandAndAdvertisedPrimary() throws Exception {
    String hookCommand = "my hook command";
    String extraCommand = "my extra command";
    String primaryAddress = getSshdAdvertisedPrimaryAddress();
    String command =
        getCloneCommand(hookCommand, extraCommand, primaryAddress)
            .getCommand(sshScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s) && (cd %s && %s) && (cd %s && git remote set-url"
                    + " --push \"$(git config --default origin --get clone.defaultRemoteName)\""
                    + " \"%s\")",
                sshScheme.getUrl(ENV.projectName),
                baseName(ENV.projectName),
                hookCommand,
                baseName(ENV.projectName),
                extraCommand,
                baseName(ENV.projectName),
                sshScheme.getPushUrl(ENV.projectName)));
  }

  @Test
  public void testHttpNoConfiguredCommands() throws Exception {
    String command = getCloneCommand(null, null, null).getCommand(httpScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && %s",
                httpScheme.getUrl(ENV.projectName), getDefaultHookCommand()));
  }

  @Test
  public void testHttpConfiguredExtraCommand() throws Exception {
    String extraCommand = "my extra command";
    String command =
        getCloneCommand(extraCommand, null, null).getCommand(httpScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s)",
                httpScheme.getUrl(ENV.projectName), baseName(ENV.projectName), extraCommand));
  }

  @Test
  public void testHttpConfiguredHookAndExtraCommand() throws Exception {
    String hookCommand = "my hook command";
    String extraCommand = "my extra command";
    String command =
        getCloneCommand(hookCommand, extraCommand, null).getCommand(httpScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s) && (cd %s && %s)",
                httpScheme.getUrl(ENV.projectName),
                baseName(ENV.projectName),
                hookCommand,
                baseName(ENV.projectName),
                extraCommand));
  }

  @Test
  public void usesLdapUsernameWhenLdapPolicyAndLdapExternalIdPresent() {
    DownloadCommandTest.TestUser user = (DownloadCommandTest.TestUser) userProvider.get();

    String ldapUserName = "ldap_username";
    ExternalId.Key ldapKey = ExternalId.Key.create(ExternalId.SCHEME_GERRIT, ldapUserName, true);
    user.setExternalIds(ImmutableSet.of(ldapKey));
    Mockito.when(authConfig.getGitBasicAuthPolicy()).thenReturn(GitBasicAuthPolicy.LDAP);

    String url = httpScheme.getUrl(ENV.projectName);

    assertThat(url).contains(ldapUserName + "@");
  }

  @Test
  public void fallsBackToGerritUsernameWhenLdapPolicyButNoLdapExternalId()
      throws UnsupportedEncodingException {
    Mockito.when(authConfig.getGitBasicAuthPolicy()).thenReturn(GitBasicAuthPolicy.LDAP);

    String url = httpScheme.getUrl(ENV.projectName);

    assertThat(url).contains(ENV.urlEncodedUserName() + "@");
  }

  private String baseName(String projectName) {
    return projectName.substring(projectName.lastIndexOf('/') + 1);
  }

  private String getDefaultHookCommand() {
    return String.format(
        "(cd %s && mkdir -p %s && curl -fLo %scommit-msg https://%s/tools/hooks/commit-msg && chmod"
            + " +x %scommit-msg)",
        baseName(ENV.projectName), HOOKS_DIR, HOOKS_DIR, ENV.fqdn, HOOKS_DIR);
  }

  private String getSshdAdvertisedPrimaryAddress() {
    return String.format("%s:%d", ENV.fqdn, ENV.sshdAdvertisedPrimaryAddress);
  }

  private CloneCommand getCloneCommand(
      String hookCommand, String extraCommand, String sshdAdvertisedPrimaryAddress) {
    final String pluginName = "download-commands";
    Config cfg = new Config();
    cfg.setString("gerrit", null, HOOK_COMMAND_KEY, hookCommand);
    cfg.setString("gerrit", null, EXTRA_COMMAND_KEY, extraCommand);
    cfg.setString(
        "plugin", pluginName, "sshdadvertisedprimaryaddress", sshdAdvertisedPrimaryAddress);

    PluginConfigFactory configFactory = Mockito.mock(PluginConfigFactory.class);
    Mockito.when(configFactory.getFromGerritConfig(pluginName))
        .thenReturn(PluginConfig.createFromGerritConfig(pluginName, cfg));

    sshScheme =
        new SshScheme(
            ImmutableList.of(String.format("%s:%d", ENV.fqdn, ENV.sshPort)),
            pluginName,
            configFactory,
            urlProvider,
            userProvider,
            new DownloadConfig(cfg));
    return new CloneWithCommitMsgHook(cfg, urlProvider);
  }
}
