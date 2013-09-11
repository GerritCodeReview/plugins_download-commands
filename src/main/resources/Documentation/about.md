This plugin defines commands for downloading changes / projects in
different download schemes (for downloading via different network
protocols).

Download Schemes
----------------

The following download schemes are defined by this plugin. Please note
that some download schemes must be enabled by [configuration]
(config.html#download.scheme).

* `Anonymous Git`: Scheme for anonymous downloads via the Git protocol.

* `Anonymous HTTP`: Scheme for anonymous downloads via the HTTP protocol.

* `HTTP`: Scheme for authenticated downloads via the HTTP protocol.

* `SSH`: Scheme for authenticated downloads via the SSH protocol.

* `REPO`: Scheme for downloading with the Repo tool.

Download Commands
-----------------

The following download commands are defined by this plugin. Please note
that some download commands must be enabled by [configuration]
(config.html#download.command).

### Git Commands

All Git commands are for the Git command line. The Git commands are
available for the schemes `Anonymous Git`, `Anonymous HTTP`, `HTTP` and
`SSH`.

* `Checkout`:
Command to fetch and checkout a patch set.

* `Cherry-Pick`:
Command to fetch a patch set and cherry-pick it onto the current
commit.

* `Clone`:
Command to clone a project.

* `Format-Patch`:
Command to fetch a patch set and feed it into the `format-patch`
command.

* `Pull`:
Command to pull a patch set.

### Repo Commands

The `Repo` command is only available for the `REPO` scheme.

* `Repo`:
Command to download a patch set of a change with the Repo tool.
