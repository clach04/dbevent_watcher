# dbevent_watcher

Ingres database event watcher

Monitor dbevents with a small embedded sql program when connected to an Ingres or Actian X database.

It listens for dbevents that already exist in `iievents`.
It doesn't take care of owners, so if you have events owned by different owners it probably won't work.
It uses a cursor select to select from iievents.


## Building

Requirements:

  * Ingres client with esql/c pre-compiler
  * Supported C compiler for that platform
  * Jam (any flavor; Jam, FTJam, BoostJam, etc.)

Issue a `jam` setting `JAM_TOOLSET` accordingly.
