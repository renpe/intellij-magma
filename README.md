# IntelliJ Magma

IntelliJ IDEA plugin for the [Magma](https://magma.maths.usyd.edu.au/magma/)
computer algebra system. Ported from the
[vscode-magma](https://github.com/etairi/vscode-magma) extension by Erkan Tairi.

> **Disclaimer:** This is an unofficial, community-maintained plugin and is
> **not affiliated with, endorsed by, or developed by** the Magma Computational
> Algebra Group at the University of Sydney. *Magma* is a trademark of its
> respective owners.

## Features

- Syntax highlighting for `.m`, `.mag`, `.magma`, `.magmarc`, `.magmarc-dev`
  and `.spec` files
- Brace matching for `()`, `[]`, `{}`
- Line (`//`) and block (`/* */`) comments
- Live templates for the most common Magma constructs: `func`, `proc`, `intr`,
  `if`, `ife`, `for`, `while`, `repeat`, `case`, `sub`, `q`, `hom`, `rec`,
  `recformat`, `gp`, `sy`, `pr`, `as`/`as2`/`as3`, `e`/`ef`, `p`, `dm`/`dp`,
  `im`, `lo`, `log`/`ulog`, `vt`/`vp`
- Dedicated color scheme entries under
  *Settings → Editor → Color Scheme → Magma*
- Run Magma scripts from the editor — gutter play icon, *Run 'script.m'* in
  the context menu, and a dedicated **Magma** run configuration type

## Running Magma scripts

1. Open *Settings → Tools → Magma* and set the path to the `magma` binary.
   Default interpreter options (`-b` for batch mode) can be tweaked there too.
2. Open any `.m` / `.mag` / `.magma` file and click the green ▶ in the gutter,
   or right-click the file → *Run 'script.m'*.
3. The first run creates a **Magma** run configuration that you can refine via
   *Run → Edit Configurations…* (script arguments, working directory,
   interpreter override, stdin mode, WSL distribution).

### WSL (Windows)

If `magma` lives inside a WSL distribution, set the global Magma settings (or
the run-configuration-level override) to the **WSL-side** interpreter path,
e.g. `/usr/local/bin/magma`, and pick the distribution from the dropdown.
Windows script paths (e.g. `C:\…\script.m`) are translated to `/mnt/c/…`
automatically and the process is launched via the WSL platform integration.

## Build

### Plugin ZIP for installation

```bash
./gradlew buildPlugin
```

The plugin ZIP is written to:

```
build/distributions/intellij-magma-<version>.zip
```

Note: plain `./gradlew build` only compiles classes and runs tests — it does
**not** produce the ZIP. Use `buildPlugin` (or `./gradlew build buildPlugin`)
to get the installable archive.

### Run in a sandbox IDE

```bash
./gradlew runIde
```

Starts an isolated IntelliJ IDEA instance with the plugin pre-installed for
quick testing. The first run downloads the target IDEA distribution (~1 GB),
subsequent runs use the Gradle cache.

## Install

In your real IntelliJ IDEA: *Settings → Plugins → ⚙ → Install Plugin from
Disk…* and pick the ZIP from `build/distributions/`.

## Requirements

- JDK 21
- IntelliJ IDEA 2024.1 or newer (`pluginSinceBuild = 241`)

The Gradle wrapper is included; you do not need to install Gradle separately.

## License

Apache License 2.0 — Copyright (c) 2026 René Peschmann. See [LICENSE](LICENSE).

Parts of this plugin are ported from the
[vscode-magma](https://github.com/etairi/vscode-magma) project by Erkan Tairi
and remain under its original MIT license (see [LICENSE-MIT](LICENSE-MIT)).
Details about the ported components are listed in [NOTICE](NOTICE).
