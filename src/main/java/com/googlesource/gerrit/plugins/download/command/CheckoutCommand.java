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

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.server.config.DownloadConfig;
import com.google.inject.Inject;

@Listen
public class CheckoutCommand extends GitDownloadCommand {
  @Inject
  CheckoutCommand(DownloadConfig downloadConfig) {
    super(downloadConfig, com.google.gerrit.reviewdb.client.AccountGeneralPreferences.DownloadCommand.CHECKOUT);
  }

  @Override
  public String getCommand(String url) {
    return "git fetch " + url + " ${ref} && git checkout FETCH_HEAD";
  }
}