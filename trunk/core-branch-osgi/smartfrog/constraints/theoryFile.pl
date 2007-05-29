:- lib(ic).

resolve(X,Y,Z,U) :- X::[2..3], Y::[2..3], Z::[1..2], U::[1], X#=Y, Y#\=Z, Z#\=U.