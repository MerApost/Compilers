 _____________________________
|Execution instructions:     |
|----------------------------|
|$ javac Part1/*.java \n     |
|$ java Part1.Main           |
|____________________________|

_____________________________________________________________________________________________________________
|        | '0'-'9'     | '+'           | '-'           | '('           | ')'   | '**'              | '$'    |
|--------|-------------|---------------|---------------|---------------|-------|-------------------|--------|
| Exp    | Term Exp2   | error         | error         | Term Exp2     | error | error             | error  |
| Exp2   | error       | + Term Exp2   | - Term Exp2   | error         | e     | error             | e      |
| Term   | Factor Term2| error         | error         | Factor Term2  | error | error             | error  |
| Term2  | error       | e             | e             | error         | e     | ** Factor Term2   | e      |
| Factor | num         | error         | error         | ( Exp )       | error | error             | error  |
| Num    | digit num2  | error         | error         | error         | error | error             | error  |
| Num2   | digit num2  | e             | e             | e             | e     | e                 | e      |
| Digit  | 0-9         | error         | error         | error         | error | error             | error  |
|___________________________________________________________________________________________________________|