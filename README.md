# Minesweeper

A finished Minesweeper game implemented in Java using Swing. Playable desktop game with multiple difficulty levels, a timer, flagging, and safe first click.

## Overview

This repository contains a standalone Minesweeper application. It provides a simple but polished UI, game state management, and basic scoring information displayed in the window.

## Features
- Three difficulty presets (Easy / Medium / Hard)
- Safe first click (first revealed square is never a mine)
- Left-click to reveal, right-click to toggle flag
- Timer and remaining-flag counter
- Win/lose detection and restart option

## Prerequisites
- Java 17 or newer installed

## Build & Run
From the repository root run:

```bash
javac src/*.java -d bin/
java -cp bin/ Window
```

If you prefer to run from an IDE, import the project as a Java project and run the `Window` class.

## Controls / Gameplay
- Left-click: reveal a square
- Right-click: place or remove a flag
- Objective: reveal all non-mine squares without triggering a mine

## Project Structure
- `src/Board.java` — game logic and board state
- `src/Square.java` — individual cell representation
- `src/Window.java` — Swing UI and entry point (`main`)
- `bin/` — compiled classes (created by `javac -d bin/`)

## Design Notes
- The board is backed by a 2D array of `Square` objects.
- `Board` manages mine placement, neighbor counts, and reveal/flag operations.
- `Window` handles user input, rendering, and the game loop (timer, restart).

## Known Issues & Future Improvements
- Add a customizable difficulty option
- Improve accessibility (keyboard controls, high-contrast theme)
- Persist high scores locally

## Credits
Created by the project author. Uses only standard Java libraries.

If you want any changes to this README (screenshots, badges, or extra instructions), tell me what to add and I'll update it.
