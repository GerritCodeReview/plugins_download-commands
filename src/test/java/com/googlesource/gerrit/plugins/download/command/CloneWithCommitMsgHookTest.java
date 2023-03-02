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
import static com.googlesource.gerrit.plugins.download.command.CloneWithCommitMsgHook.HOOK_COMMAND_KEY;

import com.googlesource.gerrit.plugins.download.DownloadCommandTest;
import org.eclipse.jgit.lib.Config;
import org.junit.Test;

public class CloneWithCommitMsgHookTest extends DownloadCommandTest {

  @Test
  public void testSshNoConfiguredCommands() throws Exception {
    String command = getCloneCommand(null, null).getCommand(sshScheme, ENV.projectName);
    System.out.println(command);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && scp -p -P %d %s@%s:hooks/commit-msg %s/.git/hooks/",
                sshScheme.getUrl(ENV.projectName),
                ENV.sshPort,
                ENV.userName,
                ENV.fqdn,
                baseName(ENV.projectName)));
  }

  @Test
  public void testSshConfiguredHookCommand() throws Exception {
    String hookCommand = "my hook command";
    String command = getCloneCommand(hookCommand, null).getCommand(sshScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s)",
                sshScheme.getUrl(ENV.projectName), baseName(ENV.projectName), hookCommand));
  }

  @Test
  public void testSshConfiguredExtraCommand() throws Exception {
    String extraCommand = "my extra command";
    String command = getCloneCommand(extraCommand, null).getCommand(sshScheme, ENV.projectName);
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
        getCloneCommand(hookCommand, extraCommand).getCommand(sshScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s)",
                sshScheme.getUrl(ENV.projectName), baseName(ENV.projectName), hookCommand));
  }

  @Test
  public void testHttpNoConfiguredCommands() throws Exception {
    String command = getCloneCommand(null, null).getCommand(httpScheme, ENV.projectName);
    System.out.println(command);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && mkdir -p .git/hooks && curl -Lo `git rev-parse --git-dir`/hooks/commit-msg https://%s@%s/tools/hooks/commit-msg; chmod +x `git rev-parse --git-dir`/hooks/commit-msg)",
                httpScheme.getUrl(ENV.projectName),
                baseName(ENV.projectName),
                ENV.urlEncodedUserName(),
                ENV.fqdn));
  }

  @Test
  public void testHttpConfiguredExtraCommand() throws Exception {
    String extraCommand = "my extra command";
    String command = getCloneCommand(extraCommand, null).getCommand(httpScheme, ENV.projectName);
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
        getCloneCommand(hookCommand, extraCommand).getCommand(httpScheme, ENV.projectName);
    assertThat(command)
        .isEqualTo(
            String.format(
                "git clone \"%s\" && (cd %s && %s)",
                httpScheme.getUrl(ENV.projectName), baseName(ENV.projectName), hookCommand));
  }

  private String baseName(String projectName) {
    return projectName.substring(projectName.lastIndexOf('/') + 1);
  }

  private CloneCommand getCloneCommand(String hookCommand, String extraCommand) {
    Config cfg = new Config();
    cfg.setString("gerrit", null, HOOK_COMMAND_KEY, hookCommand);
    cfg.setString("gerrit", null, EXTRA_COMMAND_KEY, extraCommand);
    return new CloneWithCommitMsgHook(cfg, sshScheme, userProvider);
  }
}
