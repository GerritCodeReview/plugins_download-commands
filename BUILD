load("@com_googlesource_gerrit_bazlets//tools:junit.bzl", "junit_tests")
load("@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "download-commands",
    srcs = glob(["src/main/java/**/*.java"]),
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
    deps = [
        ":download-commands__plugin",
        "//java/com/google/gerrit/acceptance:lib",
        "//plugins:plugin-lib",
    ],
)
