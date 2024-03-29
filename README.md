# dbManager - lightweight opensource manager for interaction with database based on JavaFX.

## State: suspended

## Features:

### Execution:

- [x] Report overall time for execution
- [x] Execution time per particular query
- [x] ShortCut HotKeys. Execution via F5
- [x] TabPane with different tabs per particular query
- [x] ProgressIndicator running while query is executing
- [x] Export to CSV

### Editor:

- [x] Interaction with clipboard: copy / paste / select
- [x] Drag and drop text content to text area

### Navigate:

- [x] Add new Database
- [x] Open file
- [x] Save File
- [x] Different datasource. Connect to database via TreeView
- [x] Delete database configuration via tree view
- [x] Represent database tables via tree view
- [x] About modal

### Tests

- [x] Functional / Integration for database interaction
- [x] JavaFX controller test

## TODO:

* Preferences : text editor font size / family / color 
* Preferences : autocommit
* Preferences: path to database cfg file
* Select * from table tree item
* TextArea autocomplete
* Dashboard Chart with graphics of connected sessions
* Ensure logging work normally 
* ProgressIndicator while connecting to another database
* Google Juice as a dependency container
* Google event bus instead of mediators

## Execution view. More demo screenshots: /demo
![Alt text](demo/Multiline-statements-executed.jpg?raw=true "Multiline-statements-executed")
