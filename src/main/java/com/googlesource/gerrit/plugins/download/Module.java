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

package com.googlesource.gerrit.plugins.download;

import com.google.gerrit.extensions.annotations.Exports;
import com.google.gerrit.extensions.config.DownloadCommand;
import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.inject.AbstractModule;

import com.googlesource.gerrit.plugins.download.command.CheckoutCommand;
import com.googlesource.gerrit.plugins.download.command.CherryPickCommand;
import com.googlesource.gerrit.plugins.download.command.FormatPatchCommand;
import com.googlesource.gerrit.plugins.download.command.PullCommand;
import com.googlesource.gerrit.plugins.download.command.RepoCommand;
import com.googlesource.gerrit.plugins.download.scheme.AnonymousHttpScheme;
import com.googlesource.gerrit.plugins.download.scheme.GitScheme;
import com.googlesource.gerrit.plugins.download.scheme.HttpScheme;
import com.googlesource.gerrit.plugins.download.scheme.RepoScheme;
import com.googlesource.gerrit.plugins.download.scheme.SshScheme;

class Module extends AbstractModule {
  @Override
  protected void configure() {
    bind(DownloadScheme.class).annotatedWith(Exports.named("anonymous http"))
        .to(AnonymousHttpScheme.class);
    bind(DownloadScheme.class).annotatedWith(Exports.named("git"))
        .to(GitScheme.class);
    bind(DownloadScheme.class).annotatedWith(Exports.named("http"))
        .to(HttpScheme.class);
    bind(DownloadScheme.class).annotatedWith(Exports.named("repo"))
        .to(RepoScheme.class);
    bind(DownloadScheme.class).annotatedWith(Exports.named("ssh"))
        .to(SshScheme.class);

    bind(DownloadCommand.class).annotatedWith(Exports.named("Checkout"))
        .to(CheckoutCommand.class);
    bind(DownloadCommand.class).annotatedWith(Exports.named("Cherry-Pick"))
        .to(CherryPickCommand.class);
    bind(DownloadCommand.class).annotatedWith(Exports.named("Format-Patch"))
        .to(FormatPatchCommand.class);
    bind(DownloadCommand.class).annotatedWith(Exports.named("Pull"))
        .to(PullCommand.class);
    bind(DownloadCommand.class).annotatedWith(Exports.named("Repo-Download"))
        .to(RepoCommand.class);
  }
}
