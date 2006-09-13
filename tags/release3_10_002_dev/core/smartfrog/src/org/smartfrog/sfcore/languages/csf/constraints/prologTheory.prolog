member(X, []) :- !, fail.
member(X,[X|_]).
member(X, [_ | T]) :- member(X, T).

allDifferent([]).
allDifferent([X]).
allDifferent([H|T]) :- member(H,T),!, fail; allDifferent(T).

subset([], []).
subset([], [X|Y]).
subset([X|Y], Z) :- member(X,Z), subset(Y,Z).