@PLUGIN@ Configuration
======================

By configuration it is possible to specify which download schemes and
commands are enabled.

The configuration must be done in the `gerrit.config` of the Gerrit
server.

### <a id="download">Section download</a>

```
[download]
  command = branch
  command = checkout
  command = cherry_pick
  command = pull
  command = format_patch
  command = reset
  scheme = ssh
  scheme = http
  scheme = anon_http
  scheme = anon_git
  scheme = depot_tools
  scheme = repo
  hide = ssh
  recurseSubmodules = true
```

The download section configures the allowed download methods.

<a id="download.command">download.command</a>
:   Commands that should be offered to download changes.

    Multiple commands are supported:

	* `branch`: Command to fetch and create a new branch from the patch set.

	* `checkout`: Command to fetch and checkout the patch set.

	* `cherry_pick`: Command to fetch the patch set and cherry-pick
	it onto the current commit.

	* `pull`: Command to pull the patch set.

	* `format_patch`: Command to fetch the patch set and feed it
	into the `format-patch` command.

	* `reset`: Command to fetch the patch set and reset the current branch
	(or HEAD) to it.

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

  * `repo`: Gerrit advertises patch set downloads with the `repo download`
  command, assuming that all projects managed by this instance are
  generally worked on with the
        [repo multi-repository tool](https://gerrit.googlesource.com/git-repo)
        tool. This is not default, as not all instances will deploy repo.

    *   `depot_tools`: Gerrit advertises patch set downloads with the `git cl
        patch` command, assuming that all projects managed by this instance are
        generally worked on with the
        [Depot Tools](https://commondatastorage.googleapis.com/chrome-infra-docs/flat/depot_tools/docs/html/depot_tools.html).

	If `download.scheme` is not specified, SSH, HTTP and Anonymous HTTP
	downloads are allowed.

<a id="download.hide">download.hide</a>
:   Schemes that can be used to download changes, but will not be advertised
    in the UI. This can be any scheme that can be configured in <<download.scheme>>.

    This is mostly useful in a deprecation scenario during a time where using
    a scheme is discouraged, but has to be supported until all clients have
    migrated to use a different scheme.

    By default, no scheme will be hidden in the UI.

<a id="download.hide">download.recurseSubmodules</a>
    Add `--recurse-submodules` to the `checkout` command to update submodules
    while checking out change.

    Note: recursive checkout can issue multiple parallel fetch requests increasing
    the load on the server.

    By default, set to `false`.

<a id="download.checkForHiddenChangeRefs">download.checkForHiddenChangeRefs</a>
:	Whether the download commands should be adapted when the change
	refs are hidden.

	Git has a configuration option to hide refs from the initial
	advertisement (`uploadpack.hideRefs`). This option can be used to
	hide the change refs from the client. As consequence fetching
	changes by change ref does not work anymore. However by setting
	`uploadpack.allowTipSHA1InWant` to `true` fetching changes by
	commit ID is possible. If `download.checkForHiddenChangeRefs` is
	set to `true` the git download commands use the commit ID instead
	of the change ref when a project is configured like this.

	Example git configuration on a project:

		[uploadpack]
		  hideRefs = refs/changes/
		  hideRefs = refs/cache-automerge/
		  allowTipSHA1InWant = true

	By default `false`.


### <a id="gerrit">Section gerrit</a>

```
[gerrit]
  installCommitMsgHookCommand = command
  installCommitExtraCommand = command
```

<a id="gerrit.installCommitMsgHookCommand">gerrit.installCommitMsgHookCommand</a>
  Optional command to install the commit-msg hook. Typically of the form:
  `fetch-cmd some://url/to/commit-msg .git/hooks/commit-msg ; chmod +x .git/hooks/commit-msg`
  By default unset; falls back to using scp from the canonical SSH host,
  or curl from the canonical HTTP URL for the server. Only necessary
  if a proxy or other server/network configuration prevents clients
  from fetching from the default location.

<a id="gerrit.installCommitExtraCommand">gerrit.installCommitExtraCommand</a>
  Optional command to complete the commit-msg hook. For example:
  `git submodule update --init --recursive && git review -s`
  would initialize the submodules and setup git review.

### <a id="plugin.@PLUGIN@">Section plugin.@PLUGIN@</a>

```
[plugin "@PLUGIN@"]
	sshdAdvertisedPrimaryAddress = host:port
	sshIncludeUserName = true
```

<a id="plugin.@PLUGIN@.sshdAdvertisedPrimaryAddress">plugin.@PLUGIN@.sshdAdvertisedPrimaryAddress</a>
Specifies the address where clients can reach a Gerrit primary
instance via ssh protocol.

This may differ from sshd.listenAddress if fetch is served from
another address. An example is a setup where upload-pack requests
are served by a Gerrit replica and receive-pack by a Gerrit primary.
Since ssh cannot be load balanced on layer 7 the addresses of the
primary and replica need to be different.

The following forms may be used to specify an address. In any
form, `:'port'` may be omitted to use the default SSH port of 22.

* `'hostname':'port'` (for example `review.example.com:22`)
* `'IPv4':'port'` (for example `10.0.0.1:29418`)
* `['IPv6']:'port'` (for example `[ff02::1]:29418`)

By default unset.

<a id="plugin.@PLUGIN@.sshIncludeUserName">plugin.@PLUGIN@.sshIncludeUserName</a>
Whether the SSH scheme's download commands should include the current user's username
or not. By default `true`.
