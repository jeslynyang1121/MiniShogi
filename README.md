### The Board

The board is a grid of 5 rows by 5 columns. We will call each location on the board a *square*.

And, this is the grid representation of the starting board state:
```
5 | N| G| R| S| D|
4 |__|__|__|__| P|
3 |__|__|__|__|__|
2 | p|__|__|__|__|
1 | d| s| r| g| n|
    a  b  c  d  e
```

We read the board via a combination of the letters on the x-axis and numbers on the y-axis. For instance, piece *p* is at location *a2* while piece *P* is at location *e4*.


#### Move Limit
If each player makes 200 movies, the game simply ends in a ties.

#### Illegal Moves
If a player makes a move that is not legal, the game ends immediately.

## Game Interface
The program accepts command line flags to determine which mode to play in:
```
$ java Main -i
```
In **interactive mode**, two players enter keyboard commands to play moves against each other.

```
$ java Main -f <filePath>
```
In **file mode**, the specified file is read to determine the game state and which moves to make, then the game ends.

### Possible Moves:
**move <from> <to> [promote]**
**drop <piece> <to>**
from, to are written as coordinates such as a2 or d5