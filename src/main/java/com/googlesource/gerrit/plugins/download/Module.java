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

import com.google.gerrit.extensions.config.DownloadCommand;
import com.google.gerrit.extensions.config.DownloadScheme;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.inject.AbstractModule;

import com.googlesource.gerrit.plugins.download.command.CheckoutCommand;
import com.googlesource.gerrit.plugins.download.command.CherryPickCommand;
import com.googlesource.gerrit.plugins.download.command.FormatPatchCommand;
import com.googlesource.gerrit.plugins.download.command.PullCommand;
import com.googlesource.gerrit.plugins.download.command.RepoCommand;
import com.googlesource.gerrit.plugins.download.scheme.GitScheme;
import com.googlesource.gerrit.plugins.download.scheme.AnonymousHttpScheme;
import com.googlesource.gerrit.plugins.download.scheme.HttpScheme;
import com.googlesource.gerrit.plugins.download.scheme.RepoScheme;
import com.googlesource.gerrit.plugins.download.scheme.SshScheme;

class Module extends AbstractModule {
  @Override
  protected void configure() {
    DynamicSet.bind(binder(), DownloadScheme.class).to(AnonymousHttpScheme.class);
    DynamicSet.bind(binder(), DownloadScheme.class).to(GitScheme.class);
    DynamicSet.bind(binder(), DownloadScheme.class).to(HttpScheme.class);
    DynamicSet.bind(binder(), DownloadScheme.class).to(RepoScheme.class);
    DynamicSet.bind(binder(), DownloadScheme.class).to(SshScheme.class);

    DynamicSet.bind(binder(), DownloadCommand.class).to(CheckoutCommand.class);
    DynamicSet.bind(binder(), DownloadCommand.class).to(CherryPickCommand.class);
    DynamicSet.bind(binder(), DownloadCommand.class).to(FormatPatchCommand.class);
    DynamicSet.bind(binder(), DownloadCommand.class).to(PullCommand.class);
    DynamicSet.bind(binder(), DownloadCommand.class).to(RepoCommand.class);
  }
}
