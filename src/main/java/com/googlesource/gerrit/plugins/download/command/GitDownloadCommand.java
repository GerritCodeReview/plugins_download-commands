package com.googlesource.gerrit.plugins.download.command;

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
    return upperUnderscoreToUpperHyphen(cmd.name());
  }

  /** Converts UPPER_UNDERSCORE to Upper-Hyphen */
  private static String upperUnderscoreToUpperHyphen(String upperUnderscore) {
    StringBuilder upperHyphen = new StringBuilder(upperUnderscore.length());
    String[] words = upperUnderscore.split("_");
    for (int i = 0, l = words.length; i < l; ++i) {
      if (i > 0) {
        upperHyphen.append("-");
      }
      upperHyphen.append(Character.toUpperCase(words[i].charAt(0))).append(
          words[i].substring(1).toLowerCase());

    }
    return upperHyphen.toString();
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
