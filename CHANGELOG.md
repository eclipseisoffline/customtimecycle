- Updated to 26.1.
- The mod now allows changing the rate at which time progresses for each time marker available in the game.
  - This can be done using the following commands:
    - `/timecycle set from <from> to <to> duration <duration>`, and
    - `/timecycle set from <from> to <to> rate <rate>`
  - The old `/timecycle set <dayduration> <nightduration>` command still functions and sets the time between the following time markers:
    - `customtimecycle:sunrise` and `customtimecycle:sunset` for `dayduration`
    - `customtimecycle:sunset` and `customtimecycle:sunrise` for `nightduration`
    - These time markers are automatically added for the `minecraft:overworld` clock.
  - This change results in a major change on how clock rate is saved in Minecraft worlds:
    - Time cycle data for the overworld dimension is automatically migrated.
    - **Time cycle data for other, custom dimensions is lost. Make sure to take backups!**

**Please make sure to properly back up your world when updating! Minecraft 26.1 changes the structure of Minecraft worlds a lot, and while this mod has adjusted for that, I cannot guarantee that no data loss will happen!**
