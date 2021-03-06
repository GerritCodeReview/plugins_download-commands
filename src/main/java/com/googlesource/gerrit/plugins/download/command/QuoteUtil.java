// Copyright (C) 2018 The Android Open Source Project
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

public class QuoteUtil {

  private QuoteUtil() {}

  public static String quote(String string) {
    // Avoid quotes if the chars are entirely "safe".
    if (string.matches("^[a-zA-Z0-9@_.:/-]+$")) {
      return string;
    }
    return "\"" + string + "\"";
  }
}
