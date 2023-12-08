# BungeeCordLyricChat

This BungeeCord plugin allows server owners to have a bit of fun trolling their players with song lyrics. A similar command exists on SkittleMC (`/loganpaul`), but I wanted a reimplementation that could be user-configurable.  

Just place the Jar in your `plugins/` folder on BungeeCord and restart the server. The appropriate config files are generated for you.  

You can set the chat format in `config.yml` if your server uses a chat formatting plugin.  

You can add sets of lyrics in `lyrics.yml` with &(x) color/formatting code support. You can use a set by its name in the file. If a set is named "song1", then do `/lyrics song1`.  

The chat format can be reloaded with `/lyrics reload`. The `lyrics.yml` doesn't need reloading as the relevant set is loaded when the command is executed.  

### Commands:
- `/lyrics` (`lyrics.use`) - Uses the set named `default`.
- `/lyrics <set>` (`lyrics.use`) - Uses the set with the supplied name.
- `lyrics reload` (`lyrics.reload`) - Reloads the chat format.

### Permissions:
- `lyrics.use` (`/lyrics [set]`) - Ability to make people sing.
- `lyrics.reload` (`/lyrics reload`) - Ability to reload the chat format.
