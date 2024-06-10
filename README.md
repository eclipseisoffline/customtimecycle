# Custom Time Cycle

![version badge](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2FXuf4fk5b%2Fversion&query=%24%5B0%5D.version_number&label=version&color=green)
[![downloads badge](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2FXuf4fk5b&query=%24.downloads&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iNTEyIiBoZWlnaHQ9IjUxNCIgdmlld0JveD0iMCAwIDUxMiA1MTQiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI%2BCiAgPHBhdGggZmlsbC1ydWxlPSJldmVub2RkIiBjbGlwLXJ1bGU9ImV2ZW5vZGQiIGQ9Ik01MDMuMTYgMzIzLjU2QzUxNC41NSAyODEuNDcgNTE1LjMyIDIzNS45MSA1MDMuMiAxOTAuNzZDNDY2LjU3IDU0LjIyOTkgMzI2LjA0IC0yNi44MDAxIDE4OS4zMyA5Ljc3OTkxQzgzLjgxMDEgMzguMDE5OSAxMS4zODk5IDEyOC4wNyAwLjY4OTk0MSAyMzAuNDdINDMuOTlDNTQuMjkgMTQ3LjMzIDExMy43NCA3NC43Mjk4IDE5OS43NSA1MS43MDk4QzMwNi4wNSAyMy4yNTk4IDQxNS4xMyA4MC42Njk5IDQ1My4xNyAxODEuMzhMNDExLjAzIDE5Mi42NUMzOTEuNjQgMTQ1LjggMzUyLjU3IDExMS40NSAzMDYuMyA5Ni44MTk4TDI5OC41NiAxNDAuNjZDMzM1LjA5IDE1NC4xMyAzNjQuNzIgMTg0LjUgMzc1LjU2IDIyNC45MUMzOTEuMzYgMjgzLjggMzYxLjk0IDM0NC4xNCAzMDguNTYgMzY5LjE3TDMyMC4wOSA0MTIuMTZDMzkwLjI1IDM4My4yMSA0MzIuNCAzMTAuMyA0MjIuNDMgMjM1LjE0TDQ2NC40MSAyMjMuOTFDNDY4LjkxIDI1Mi42MiA0NjcuMzUgMjgxLjE2IDQ2MC41NSAzMDguMDdMNTAzLjE2IDMyMy41NloiIGZpbGw9IiMxYmQ5NmEiLz4KICA8cGF0aCBkPSJNMzIxLjk5IDUwNC4yMkMxODUuMjcgNTQwLjggNDQuNzUwMSA0NTkuNzcgOC4xMTAxMSAzMjMuMjRDMy44NDAxMSAzMDcuMzEgMS4xNyAyOTEuMzMgMCAyNzUuNDZINDMuMjdDNDQuMzYgMjg3LjM3IDQ2LjQ2OTkgMjk5LjM1IDQ5LjY3OTkgMzExLjI5QzUzLjAzOTkgMzIzLjggNTcuNDUgMzM1Ljc1IDYyLjc5IDM0Ny4wN0wxMDEuMzggMzIzLjkyQzk4LjEyOTkgMzE2LjQyIDk1LjM5IDMwOC42IDkzLjIxIDMwMC40N0M2OS4xNyAyMTAuODcgMTIyLjQxIDExOC43NyAyMTIuMTMgOTQuNzYwMUMyMjkuMTMgOTAuMjEwMSAyNDYuMjMgODguNDQwMSAyNjIuOTMgODkuMTUwMUwyNTUuMTkgMTMzQzI0NC43MyAxMzMuMDUgMjM0LjExIDEzNC40MiAyMjMuNTMgMTM3LjI1QzE1Ny4zMSAxNTQuOTggMTE4LjAxIDIyMi45NSAxMzUuNzUgMjg5LjA5QzEzNi44NSAyOTMuMTYgMTM4LjEzIDI5Ny4xMyAxMzkuNTkgMzAwLjk5TDE4OC45NCAyNzEuMzhMMTc0LjA3IDIzMS45NUwyMjAuNjcgMTg0LjA4TDI3OS41NyAxNzEuMzlMMjk2LjYyIDE5Mi4zOEwyNjkuNDcgMjE5Ljg4TDI0NS43OSAyMjcuMzNMMjI4Ljg3IDI0NC43MkwyMzcuMTYgMjY3Ljc5QzIzNy4xNiAyNjcuNzkgMjUzLjk1IDI4NS42MyAyNTMuOTggMjg1LjY0TDI3Ny43IDI3OS4zM0wyOTQuNTggMjYwLjc5TDMzMS40NCAyNDkuMTJMMzQyLjQyIDI3My44MkwzMDQuMzkgMzIwLjQ1TDI0MC42NiAzNDAuNjNMMjEyLjA4IDMwOC44MUwxNjIuMjYgMzM4LjdDMTg3LjggMzY3Ljc4IDIyNi4yIDM4My45MyAyNjYuMDEgMzgwLjU2TDI3Ny41NCA0MjMuNTVDMjE4LjEzIDQzMS40MSAxNjAuMSA0MDYuODIgMTI0LjA1IDM2MS42NEw4NS42Mzk5IDM4NC42OEMxMzYuMjUgNDUxLjE3IDIyMy44NCA0ODQuMTEgMzA5LjYxIDQ2MS4xNkMzNzEuMzUgNDQ0LjY0IDQxOS40IDQwMi41NiA0NDUuNDIgMzQ5LjM4TDQ4OC4wNiAzNjQuODhDNDU3LjE3IDQzMS4xNiAzOTguMjIgNDgzLjgyIDMyMS45OSA1MDQuMjJaIiBmaWxsPSIjMWJkOTZhIi8%2BCjwvc3ZnPg%3D%3D&label=downloads&color=green)](https://modrinth.com/mod/modify-player-data)

Custom Time Cycle is a mod that allows changing the duration of Minecraft days and nights using a simple
command, without changing tick speed.

## License

This mod is licensed under GNU GPLv3.

## Usage

Mod builds can be found [here](https://github.com/eclipseisoffline/customtimecycle/packages/2106877).

This mod is currently available for Fabric, Minecraft 1.20.5+6 and 1.20.4 (no longer updated).
The Fabric API is required. When installed server-side, the mod is not required on clients.

Durations of days and nights can be configured across dimensions and are saved across server restarts / world saves.

This mod adds a simple command, `/timecycle`. Its usage is as follows:

- `/timecycle status`
  - Shows a simple status message displaying which time durations are currently in use in the current dimension.
- `/timecycle set <dayduration> <nightduration>`
  - Modifies the durations of the Minecraft day and night in the current dimension.
- `/timecycle reset`
  - Resets the durations of the Minecraft day and night in the current dimension.

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
