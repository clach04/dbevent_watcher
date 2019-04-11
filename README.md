# dbevent_watcher
Ingres database event watcher

Monitor dbevents with a small embedded sql program when working with Ingres replicator.

It is called watchrepev and selects only those dbevents from iievents starting with `dd_`, but you can remove this restriction.
It doesn't take care of owners, so if you have events owned by different owners it probably won't work.
It uses a cursor select to select from iievents.


## Building

Requirements:

  * Ingres client with esql/c pre-compiler
  * Supported C compiler for that platform
  * Jam (any flavor; Jam, FTJam, BoostJam, etc.)

Issue a `jam` setting `JAM_TOOLSET` accordingly.
