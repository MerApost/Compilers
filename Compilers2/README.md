
Execution instructions:
$ make
$ java Main [examples]


Αυτή η εργασία υλοποιεί semantic analyzer για τη γλώσσα MiniJava.

Ο semantic analyzer ελέγχει MiniJava προγράμματα για semantic λάθη (τύποι, κληρονομικότητα, διπλές δηλώσεις, forbidden overloading κ.λπ.) και εκτυπώνει offsets για fields και methods κάθε κλάσης, όπως ζητείται στην εκφώνηση.

Έχουν υλοποιηθεί 2 visitors:
1. (first pass) για SymbolTable
2. (second pass) για TypeChecking

