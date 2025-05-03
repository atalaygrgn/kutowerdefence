# Change Log

Please list the changes you made here during development

## 29.04

### Atalay:

- Created a basic gameplay view with map loading, very WIP
- Some modifications to GameSession, MapSelectionOverlay, Map, and Routing for creating a gameplay screen with the selected map
- Introducing controller package for the controller pattern, and GamePlayController for handling game logic
- This file renamed to README to show the changes directly on the repo page
### Çınar:
- Created options menu view and layout
- Handled the exit button case


optionsmenuview enemy domain classes enemy spawning path movement animation
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

### Çınar:
- Implemented Enemy domain classes: Enemy, EnemyDescription, and EnemyFactory.
- EnemyDescription holds the necessary informations for enemy creation, used in Enemy.
- Enemy class includes an update() function for handling enemy movement and a takeDamage() that sets damage multiplier for different enemy and projectile combinations.
- EnemyFactory is used for different types of enemy creation using factory pattern
- Implemented Point class for the path, we hold the path as a linkedlist type point as enemies move on it.

## 01.05

### Anıl:

- Added saveOptions function, it saves the configured options under resources/options folder. Now when a new game starts, it reads the options from the saved file.

### Atalay:

- Tower classes implemented
- Build tower by clicking an empty lot and choosing a tower type with the popup panel

### Çınar: 
- Implemented basic enemy spawning and path movement animation, many changes here and there.
- Modified GamePlayController and Routing to test enemy movement, includes spawnTestEnemy() which creates 1 goblin and 1 knight.
- Enemy and EnemyDescription classes are modified for takeDamage() logic with new resistance fields.
- Implemented EnemyView class with ImageView, update() function modified for pixel movement and animationFrames list with Timeline for animation.
- Assets for goblin and knight animations added.
- GamePlayView updated with enemyLayer, enemyViews list, updateEnemies() for movement and startUpdateEnemyLoop() for enemy movement animation with AnimationTimer.
- GamePlayView layout updated for including enemyLayer.

## 02.05

### Jahan:

- Added Two new asset files: Fire and Explosion under effects.
- Animations added for projectile attacks
- Health-bar decrease added on losing HP
- New View: ProjectileView
- Towers attack enemies, in line with what is selected in options.

### Anıl:

- Added functions related to gold (gaining when an enemy is dead, losing when buying towers)
- Options now modify tower properties

### Çınar:
- Added health, gold, and wave icons on gameplay screen, slight modification on GamePlayView layout.
