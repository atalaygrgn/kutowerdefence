# Change Log

Please list the changes you made here during development

## 29.04

### Atalay:

- Created a basic gameplay view with map loading, very WIP
- Some modifications to GameSession, MapSelectionOverlay, Map, and Routing for creating a gameplay screen with the selected map
- Introducing controller package for the controller pattern, and GamePlayController for handling game logic
- This file renamed to README to show the changes directly on the repo page

## 30.04

### Atalay:

- Pathfinding, path setting in edit mode, path saving/loading to/from file
- Map objects (decoration, castles, houses over tiles), map object saving/loading to/from file
- Right-click on a tile to clear it quickly in edit mode
- Right-click on a map object to remove it in edit mode
- A change of formatting in .kutdmap format: storage of the path and map objects
- All map files are now saved into resources/maps, user just inputs the map name
- Two new example maps, compatible with the format change
- The Tower abstract Tower class WIP

### Jahan:

- Changed No Maps Found Error to include two buttons, OK to continue, or Go to Map Editor to direct the user to the map editor. (two new imports in MainMenuView)
-
- Slightly changed wording of the error
-

### Anıl:

- Added OptionController for handling option adjustment (Further developement of SaveOptions and Save/Load game requires WaveController to be developed). Restore option is done.
- Code refactoring and slight modifications in OptionController in Routing & OptionsMenuViewmake to make them compatible with OptionController (please review)

## 01.05

### Anıl:

- Added saveOptions function, it saves the congigured options under resources/options folder. Now when a new game starts, it reads the options from the saved file.
