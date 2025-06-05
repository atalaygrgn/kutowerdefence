# Change Log

Please list the changes you made here during development

## 29.04

### Atalay:

- Created a basic _GamePlayView_ with map loading, very WIP
- Some modifications to _GameSession_, _MapSelectionOverlay_, _Map_, and _Routing_ for creating a gameplay screen with the selected map
- Introducing controller package for the controller pattern, and _GamePlayController_ for handling game logic
- This file renamed to README to show the changes directly on the repo page

### Çınar:

- Created _OptionsMenuView_ and layout.
- Handled the exit button case.

## 30.04

### Atalay:

- Pathfinding, path setting in edit mode, path saving/loading to/from file
- _Map_ objects (decoration, castles, houses over tiles), map object saving/loading to/from file
- Right-click on a tile to clear it quickly in edit mode
- Right-click on a map object to remove it in edit mode
- A change of formatting in .kutdmap format: storage of the path and map objects
- All map files are now saved into resources/maps, user just inputs the map name
- Two new example maps, compatible with the format change
- The Tower abstract _Tower_ class WIP

### Jahan:

- Changed No Maps Found Error to include two buttons, OK to continue, or Go to Map Editor to direct the user to the map editor. (two new imports in _MainMenuView_)
- Slightly changed wording of the error

### Anıl:

- Added _OptionController_ for handling option adjustment (Further developement of _SaveOptions_ and Save/Load game requires _WaveController_ to be developed). Restore option is done.
- Code refactoring and slight modifications in _OptionController_ in _Routing_ & _OptionsMenuViewmake_ to make them compatible with _OptionController_ (please review)

### Çınar:

- Implemented _Enemy_ domain classes: _Enemy_, _EnemyDescription_, and _EnemyFactory_.
- _EnemyDescription_ holds the necessary informations for enemy creation, used in _Enemy_.
- _Enemy_ class includes an _update()_ function for handling enemy movement and a _takeDamage()_ that sets damage multiplier for different enemy and projectile combinations.
- _EnemyFactory_ is used for different types of enemy creation using factory pattern
- Implemented _Point_ class for the path, we hold the path as a linkedlist type Point as enemies move on it.

## 01.05

### Anıl:

- Added _saveOptions()_ function, it saves the configured options under resources/options folder. Now when a new game starts, it reads the options from the saved file.

### Atalay:

- _Tower_ classes implemented
- Build tower by clicking an empty lot and choosing a tower type with the popup panel

### Çınar:

- Implemented basic enemy spawning and path movement animation, many changes here and there.
- Modified _GamePlayController_ and _Routing_ to test enemy movement, includes _spawnTestEnemy()_ which creates 1 goblin and 1 knight.
- _Enemy_ and _EnemyDescription_ classes are modified for _takeDamage()_ logic with new resistance fields.
- Implemented _EnemyView_ class with _ImageView_, _update()_ function modified for pixel movement and _animationFrames_ list with _Timeline_ for animation.
- Assets for goblin and knight animations added.
- _GamePlayView_ updated with _enemyLayer_, _enemyViews_ list, _updateEnemies()_ for movement and _startUpdateEnemyLoop()_ for enemy movement animation with _AnimationTimer_.
- _GamePlayView_ layout updated for including _enemyLayer_.

## 02.05

### Jahan:

- Added two new asset files: _Fire_ and _Explosion_ under effects.
- Animations added for projectile attacks
- Health-bar decrease added on losing HP
- New View: _ProjectileView_
- Towers attack enemies, in line with what is selected in options.

### Anıl:

- Added functions related to gold (gaining when an enemy is dead, losing when buying towers)
- Options now modify tower properties

### Çınar:

- Added health, gold, and wave icons on gameplay screen, slight modification on _GamePlayView_ layout.

## 23.05

### Anıl:

- Introduced state pattern for toggle speed functionality (now there are 4 different states)
- Bug fix

## 05.06

### Anıl:

- Introduced wave mechanics to the game. User may change the delay between waves/enemy groups/enemies within the group, and also the number of enemy groups a wave has and group size of each group. All the waves are identical in terms of the number of enemy groups they have, and the size of the groups (each enemy group has the same size). However, each enemy group is initialized randomly (ie, group size is same for all enemy groups throughout the game, but they may have different number of goblins and knights).
- Speed toggle/pause-resume states are integrated to wave mechanics.
- Load-save game functionality is completed. Users can now save their games and/or load the games they previously saved. Currently they do not select a slot when saving, the game automatically creates another save file and stores it under saves folder. When loading all the files under saves folder are displayed and user chooses a save to open.
