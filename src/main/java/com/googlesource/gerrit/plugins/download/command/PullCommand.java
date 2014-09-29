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

package com.googlesource.gerrit.plugins.download.command;

import static com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadCommand.PULL;

import com.google.gerrit.server.config.DownloadConfig;

import javax.inject.Inject;

class PullCommand extends GitDownloadCommand {
  @Inject
  PullCommand(DownloadConfig downloadConfig) {
    super(downloadConfig, PULL);
  }

  @Override
  String getCommand(String url, String ref) {
    return "git pull " + url + " " + ref;
  }
}
