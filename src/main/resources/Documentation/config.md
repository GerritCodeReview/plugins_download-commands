@PLUGIN@ Configuration
======================

By configuration it is possible to specify which download schemes and
commands are enabled.

The configuration must be done in the `gerrit.config` of the Gerrit
server.

### <a id="download">Section download</a>

```
[download]
  command = checkout
  command = cherry_pick
  command = pull
  command = format_patch
  scheme = ssh
  scheme = http
  scheme = anon_http
  scheme = anon_git
  scheme = repo_download
```

The download section configures the allowed download methods.

<a id="download.command">download.command</a>
:	Commands that should be offered to download changes.

	Multiple commands are supported:

	* `checkout`: Command to fetch and checkout the patch set.

	* `cherry_pick`: Command to fetch the patch set and to cherry-pick
	it onto the	current commit.

	* `pull`: Command to pull the patch set.

	* `format_patch`: Command to fetch the patch set and to feed it
	into the `format-patch` command.

	If `download.command` is not specified, all download commands are
	offered.

<a id="download.scheme">download.scheme</a>
:	Schemes that should be used to download changes.

	Multiple schemes are supported:

	* `http`: Authenticated HTTP download is allowed.

	* `ssh`: Authenticated SSH download is allowed.

	* `anon_http`: Anonymous HTTP download is allowed.

	* `anon_git`: Anonymous Git download is allowed.  This is not
	default, it is also necessary to set [gerrit.canonicalGitUrl]
	(../../../Documentation/config-gerrit.html#gerrit.canonicalGitUrl)
	variable.

	* `repo_download`: Gerrit advertises patch set downloads with the
	`repo download`	command, assuming that all projects managed by this
	instance are generally worked on with the repo multi-repository
	tool.  This is not default, as not all instances will deploy repo.

	If `download.scheme` is not specified, SSH, HTTP and Anonymous HTTP
	downloads are allowed.
