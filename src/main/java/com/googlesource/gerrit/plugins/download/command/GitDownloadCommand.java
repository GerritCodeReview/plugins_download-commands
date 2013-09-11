package com.googlesource.gerrit.plugins.download.command;

import com.google.common.base.CaseFormat;
import com.google.gerrit.extensions.config.DownloadCommand;
import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.gerrit.server.config.DownloadConfig;

import com.googlesource.gerrit.plugins.download.scheme.AnonymousGitScheme;
import com.googlesource.gerrit.plugins.download.scheme.AnonymousHttpScheme;
import com.googlesource.gerrit.plugins.download.scheme.HttpScheme;
import com.googlesource.gerrit.plugins.download.scheme.SshScheme;

public abstract class GitDownloadCommand extends DownloadCommand {
  private final com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadCommand cmd;
  private final boolean commandAllowed;

  GitDownloadCommand(
      DownloadConfig downloadConfig,
      com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadCommand cmd) {
    this.cmd = cmd;
    this.commandAllowed = downloadConfig.getDownloadCommands().contains(cmd)
        || downloadConfig.getDownloadCommands().contains(
        com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadCommand.DEFAULT_DOWNLOADS);
  }

  @Override
  public String getName() {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, cmd.name());
  }

  @Override
  public final String getCommand(DownloadScheme scheme, String project) {
    if (!commandAllowed) {
      return null;
    }

    if (scheme instanceof SshScheme
        || scheme instanceof HttpScheme
        || scheme instanceof AnonymousHttpScheme
        || scheme instanceof AnonymousGitScheme) {
      return getCommand(scheme.getUrl(project));
    } else {
      return null;
    }
  }

  public abstract String getCommand(String url);
}
