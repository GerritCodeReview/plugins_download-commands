load("@gerrit_api_version//:version.bzl", "GERRIT_API_VERSION")
load("@com_googlesource_gerrit_bazlets//tools:junit.bzl", "junit_tests")
load(
    "@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)

gerrit_plugin(
    name = "download-commands",
    srcs = glob(["src/main/java/**/*.java"]),
    gerrit_api_version = GERRIT_API_VERSION,
    manifest_entries = [
        "Gerrit-PluginName: download-commands",
        "Gerrit-Module: com.googlesource.gerrit.plugins.download.PluginModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
)

junit_tests(
    name = "download-commands_tests",
    size = "small",
    srcs = glob(["src/test/java/**/*.java"]),
    tags = ["download-commands"],
    deps = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":download-commands__plugin",
    ],
)
