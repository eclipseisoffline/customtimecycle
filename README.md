# Custom Time Cycle

[![Modrinth Version](https://img.shields.io/modrinth/v/Xuf4fk5b?logo=modrinth&color=008800)](https://modrinth.com/mod/customtimecycle)
[![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/Xuf4fk5b?logo=modrinth&color=008800)](https://modrinth.com/mod/customtimecycle)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/Xuf4fk5b?logo=modrinth&color=008800)](https://modrinth.com/mod/customtimecycle)
[![Discord Badge](https://img.shields.io/badge/chat-discord-%235865f2)](https://discord.gg/CNNkyWRkqm)
[![Github Badge](https://img.shields.io/badge/github-customtimecycle-white?logo=github)](https://github.com/eclipseisoffline/customtimecycle)
![GitHub License](https://img.shields.io/github/license/eclipseisoffline/customtimecycle)
![Available for Fabric](https://img.shields.io/badge/available_for-fabric-_?color=%23dbd0b4)
![Available for NeoForge](https://img.shields.io/badge/available_for-NeoForge-_?color=%23e58c53)

Custom Time Cycle is a mod that allows changing the duration of Minecraft days and nights using a simple
command, without changing tick speed. It is available for both Fabric and NeoForge. It can be used on servers (without
being required on player clients), and in singleplayer.

## License

This mod is licensed under GNU LGPLv3.

## Donating

If you like this mod, consider [donating](https://buymeacoffee.com/eclipseisoffline).

## Discord

For support and/or any questions you may have, feel free to join [my discord](https://discord.gg/CNNkyWRkqm).

## Version support

| Minecraft Version | Status       |
|-------------------|--------------|
| 1.21.9            | ✅ Current    |
| 1.21.6+7+8        | ✔️ Available |
| 1.21.5            | ✔️ Available |
| 1.21.4            | ✔️ Available |
| 1.21.2+3          | ✔️ Available |
| 1.21+1            | ✅ Current    |
| 1.20.5+6          | ✔️ Available |
| 1.20.4            | ✔️ Available |
| 1.20.1            | ✔️ Available |

I try to keep support up for the latest major and latest minor release of Minecraft. Updates to newer Minecraft
versions may be delayed from time to time, as I do not always have the time to immediately update my mods.

Unsupported versions are still available to download, but they won't receive new features or bugfixes.

NeoForge ports are available for Minecraft 1.21+1 and for Minecraft 1.21.9 onwards.

## Usage

Mod builds can be found on the releases page, as well as on [Modrinth](https://modrinth.com/mod/customtimecycle).

On Fabric, the Fabric API is required. When installed server-side, the mod is not required on clients.
Durations of days and nights can be configured across dimensions and are saved across server restarts / world saves.

For clients, the time cycle of a world can be configured upon creation, by selecting the "More" tab at the top, then
clicking the "Time Cycle" button. You can also configure default time cycle durations for all new created worlds, in the mod's
global configuration screen. This can be accessed on Fabric using ModMenu mod, and on NeoForge using the built-in mod menu.
Global configuration is stored in `.minecraft/config/customtimecyle.json`.

The mod also adds a simple command, `/timecycle`, which can be used to alter the time cycle on servers and existing singleplayer worlds.
Its usage is as follows:

- `/timecycle status`
  - Shows a simple status message displaying which time durations are currently in use in the current dimension.
- `/timecycle set <dayduration> <nightduration>`
  - Modifies the durations of the Minecraft day and night in the current dimension.
- `/timecycle reset`
  - Resets the durations of the Minecraft day and night in the current dimension.

Using the `/timecycle` command requires the `timecycle.command` permission or operator level 2.

Modpack developers can also include the following file in the `config` folder to preconfigure time cycle durations for
worlds on servers or clients:

```json
{
  "daytime": <day time ticks>,
  "nighttime": <night time ticks>
}
```

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
