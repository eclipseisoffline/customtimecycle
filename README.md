# Custom Time Cycle

![Modrinth Version](https://img.shields.io/modrinth/v/Xuf4fk5b?logo=modrinth&color=008800)
![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/Xuf4fk5b?logo=modrinth&color=008800)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/Xuf4fk5b?logo=modrinth&color=008800)

Custom Time Cycle is a mod that allows changing the duration of Minecraft days and nights using a simple
command, without changing tick speed.

## License

This mod is licensed under GNU LGPLv3.

## Usage

Mod builds can be found [here](https://github.com/eclipseisoffline/customtimecycle/packages/2106877) and on [Modrinth](https://modrinth.com/mod/customtimecycle).

This mod is currently available for Fabric, Minecraft 1.21+1 and 1.20.5+6/1.20.4 (no longer updated).
The Fabric API is required. When installed server-side, the mod is not required on clients.

Durations of days and nights can be configured across dimensions and are saved across server restarts / world saves.

This mod adds a simple command, `/timecycle`. Its usage is as follows:

- `/timecycle status`
  - Shows a simple status message displaying which time durations are currently in use in the current dimension.
- `/timecycle set <dayduration> <nightduration>`
  - Modifies the durations of the Minecraft day and night in the current dimension.
- `/timecycle reset`
  - Resets the durations of the Minecraft day and night in the current dimension.

Using the `/timecycle` command requires the `timecycle.command` permission or operator level 2.

## How it works (technical explanation)

Each Minecraft dimension (also called a *level* within Minecraft's code) has 3 counters related to time:

- The *game time* counter:
  - Is incremented by 1 every server tick, and never resets.
  - Counts the amount of ticks that have passed in the dimension.
  - Can be read in-game by using the vanilla `/time query gametime` command.
- The *day time* counter:
  - Is incremented by 1 every server tick, and is set back to 0 when it reaches 24000.
  - Determines the time of day:
    - When at 0, the sun rises.
    - When at 6000, the sun is at its peak (noon).
    - When at 12000, the sun starts to set, and the moon starts to rise.
    - When at 18000, the moon is at its peak (midnight).
    - When at 24000, resets to 0, and the sun rises again.
  - This means a Minecraft day lasts 12000 *day time ticks* and a Minecraft night also lasts 12000 *day time ticks*.
  - In vanilla Minecraft, 20 server ticks occur every second, meaning one Minecraft day and night together last 20 minutes.
  - Can be read in-game by using the vanilla `/time query daytime` command.
- The *day* counter:
  - Is incremented by 1 every time the *day time* counter resets back to 0, never resets.
  - Counts the amount of days that have passed in the dimension.
  - Can be read in-game by using the vanilla `/time query day` command, or in the debug screen (F3) at the `Local Difficulty` line.

This mod simply changes the rate the *day time* counter increments at, and by how much it increments:

- For example, if you set the duration of the Minecraft day to 6000 *day time ticks*, the *day time* counter will be incremented twice as fast, so by 2 every server tick.
  - We can confirm this by running the `/timecycle status` command:
    > Using day time tick rate (duration=6000)  
    > Incrementing 2 time ticks every 1 server ticks
- If you set the duration of the Minecraft day to 24000 *day time ticks*, the *day time* counter will be incremented at half the speed, so by 1 every 2 server ticks.
  - Once again, we can confirm this by running the `/timecycle status` command:
    > Using day time rate (duration=24000)  
    > Incrementing 1 time ticks every 2 server ticks

If you want to read more about the way Minecraft time works, I recommend [this](https://minecraft.wiki/w/Daylight_cycle) page on the Minecraft wiki.
