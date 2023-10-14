
Difficulty Levels:

Use the Singleton Pattern to manage the game's difficulty levels. Create a DifficultyLevelManager class as a singleton. This class will load the JSON configuration files and provide methods to set the current difficulty level and retrieve it.
Time and Score:

Implement the Observer Pattern to manage time and score updates on the screen. The game screen can be the subject, and components interested in time and score updates can be observers. When the game state changes (aliens are hit or time progresses), the subject notifies its observers to update the time and score on the screen.
Undo and Cheat:

To implement undo functionality and cheating operations, you can use the Memento Pattern. Define a GameStateMemento class to capture the game's current state (including score, time, alien positions, and projectile positions).
Create a MementoCaretaker to manage the mementos. This class stores a single game state memento and allows you to undo to a previous state.
For cheating operations, you can use the Decorator Pattern to add behaviors dynamically. Create decorator classes for different cheating options. These decorators can add behaviors like removing alien projectiles or removing aliens based on the chosen cheating option.
Use the Strategy Pattern to manage different cheating options. Define a CheatStrategy interface with concrete classes for each cheating option. The player can select a strategy, and the decorator will apply it based on the selected strategy.
By utilizing these design patterns, you can fulfill the requirements of your assignment while sticking to the design patterns learned in your course.