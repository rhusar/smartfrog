/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%STATE SCOPING

:- dynamic clause_level/1.
:- dynamic clause_state/2.

level(L) :- (clause_level(L) -> 
                             true
                             ;
                             L=0).

sos :- (clause_level(L) -> 
                             retract(clause_level(L)), 
                             L1 is L+1,
                             assert(clause_level(L1))
                             ;
                             assert(clause_level(1))).

eos :- (clause_level(L) -> 
                             retract(clause_level(L)), 
                             L1 is L-1,
                             assert(clause_level(L1)),
                             retract_state_level(L)
                             ;
                             true).  %being kind

assert_state(C) :- level(L), assert(clause_state(L, C)).
asserta_state(C) :- level(L), asserta(clause_state(L, C)).
assertz_state(C) :- level(L), assertz(clause_state(L, C)).

replace_state_level(C) :-
        C=..[F|Args], 
        length(Args,N), length(ArgsR,N),
        CR=..[F|ArgsR],
        retract_state_level(CR),
        assert_state(C).

retract_all_state :- retract_all(clause_state(_,_)).
retract_all_state(C) :- retract_all(clause_state(_,C)).
retract_all_state_level(C) :- level(L), retract_all(clause_state(L,C)).
retract_all_state_level :- eos.

retract_state_level(L) :- retract_all(clause_state(L,_)).

retract_state(C) :- level(L), try_retract(L, C).
try_retract(L, C) :- retract(clause_state(L,C)),!.
try_retract(0, _) :- writeln("Failed to retract given"
                                         " clause state at any scope"
                             " level"); flush(stdout).
try_retract(L, C) :- L>0, L1 is L-1, try_retract(L1, C).

clause_state(C) :- level(L), try_clause(L, C).
try_clause(L, C) :- clause_state(L, C).
try_clause(L, C) :- L>0, L1 is L-1, try_clause(L1, C).

clause_state_level(C) :- level(L), clause_state(L, C).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%THEORY NAMESPACES

:- dynamic namespace/1.
:- dynamic root/1.

add_path(PT) :- atom_string(PT, PTS), assert(root(PTS)). 

source_compose(FNIn,FNOut) :-
        atom_string(FNIn, FNInS), 
        (root(PTS) -> concat_strings(PTS, FNInS, FNOutS), atom_string(FNOut, FNOutS); 
                   FNOut=FNIn). 

source(FN) :- source_compose(FN,FNO), compile(FNO).
source(FN, NSS) :- (namespace(NSS) -> 
                       writeln("Namespace atom provided"
                            " already in use"), flush(stdout);
                    source_compose(FN,FNS),
                    source_compose(NSS, FNT1),
                    concat_strings(FNT1, FN, FNT),
                    do_compile(FNS, FNT, NSS),
                    assert(namespace(NSS))).


do_compile(FNS, FNT, NSS) :- open(FNS, read, S),
                     get_heads(S, [], [], Hs, Cls),
                     close(S),
                     concat_strings(NSS, "__", NSS1),
                     process_clauses(NSS1, Hs, Cls, [], Cls1),
                     open(FNT, write, OS),
                     writeclauses(OS, Cls1),
                     close(OS),
                     compile(FNT).

writeclauses(_,[]).
writeclauses(S,[H|T]) :-
        writeclause(S, H), 
        writeclauses(S, T).

get_heads(S, HsI, ClsI, HsO, ClsO):- 
              read(S, C),
              (C = end_of_file -> HsI=HsO, ClsI=ClsO;
              (C = (:- _ ) -> get_heads(S, HsI, [C|ClsI],
                                         HsO, ClsO);
              (C = (H :- _) ->                   
                               get_arity(H, F, A);
                               get_arity(C, F, A)),
                                                
                               (member((F,A),HsI) -> HsI1 = HsI;
                                                     HsI1 = [(F,A)|HsI]),
                               get_heads(S, HsI1, [C|ClsI], HsO, ClsO))).

get_arity(H, F, Ar) :- 
        H =.. [F|Args],
        length(Args, Ar).

process_clauses(_, _, [], Cls, Cls).
process_clauses(NSs, Hs, [HC|TC], ClsI, ClsO) :-
        
        (HC = (:- _ ) -> ClsI1 = [HC|ClsI];  
                                        
        (HC = (H :- T) -> process_head(NSs, H, NSH),
                          convert_tuple_list(T, TL),
                          process_tail(NSs, Hs, TL, NST), 
                          convert_tuple_list(NSTT, NST),
                          NSC = (NSH :- NSTT),
                          ClsI1 = [NSC | ClsI];
                          
                          process_head(NSs, HC, NSH), 
                          ClsI1 = [NSH | ClsI])),
                          
        process_clauses(NSs, Hs, TC, ClsI1, ClsO).

process_head(NSs, H, NSH):-
        H =..[F|Args],
        atom_string(F, Fs),
        concat_strings(NSs, Fs, F1s),
        atom_string(F1, F1s),
        NSH =..[F1|Args].

process_func(_, _, assert, [_], assert_state) :- !.
process_func(_, _, asserta, [_], asserta_state) :- !.
process_func(_, _, assertz, [_], assertz_state) :- !.

process_func(_, _, ecl_assert, [_], assert) :- !.
process_func(_, _, ecl_asserta, [_], asserta) :- !.
process_func(_, _, ecl_assertz, [_], assertz) :- !.

process_func(_, _, retract, [_], retract_state) :- !.
process_func(_, _, retract_all, [_], retract_all_state) :- !.

process_func(_, _, ecl_retract, [_], retract) :- !.
process_func(_, _, ecl_retract_all, [_], retract_all) :- !.

process_func(_, _, clause, [_], clause_state) :- !.

process_func(_, _, ecl_clause, [_], clause) :- !.

        
process_func(NSs, Hs, F, Args, F1):-
        length(Args, Ar),
        member((F, Ar), Hs), !,
        atom_string(F, Fs),
        concat_strings(NSs, Fs, F1s),
        atom_string(F1, F1s).
process_func(_, _, F, _, F).

process_tail(_, _, [], []).
process_tail(NSs, Hs, [H|T], [NSH|NST1]):-
        process_tail(NSs, Hs, T, NST1),
        process_atom(NSs, Hs, H, NSH).

process_atom(NSs, Hs, H, NSH) :-
        (nonvar(H) -> H =..[F|Args], 
                    process_func(NSs, Hs, F, Args, NSF), 
                    process_tail(NSs, Hs, Args, NSA),
                    NSH =..[NSF|NSA];
                    NSH=H).

convert_tuple_list((M,Ms), [M,Ms1|MsR]) :- !, convert_tuple_list(Ms, [Ms1|MsR]).
convert_tuple_list((M), [M]).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Description hierarchy state

:- lib(ic).
:- lib(sd).
:- lib(hash).

%%%
%solve/0: Solve an sfConfig hierarchy
%%%
sfsolve :- 
        (sfsolve_wkr -> true; sftojava(sffailed)).
sfsolve_wkr :-
           hash_create(Binds), 
           %set undo index
           hash_set(Binds, sf_evalidx, 0),         
           sffromjava(Binds).

%%%
%sftojava/1: Sends ToJava to Java side
%%%
sftojava(ToJava) :-
        write_exdr(eclipse_to_java, ToJava),
        flush(eclipse_to_java). 

%%%
%sffromjava/1: Receives Resp from Java side, converts to a term and
% calls it
%%%
sffromjava(Binds) :- 
        read_exdr(java_to_eclipse, Resp),
        term_string(Solve, Resp),
        sfsolve_process(Solve, Binds).

%%%
%sfsolve_process/2: Processes coms from Java
%%%
sfsolve_process(sfstop, _).
sfsolve_process(sfsolve(Attrs, Values, CIndex, Solve, AutoVars, Verbose), Binds) :-
        %set particular constraint index
        hash_set(Binds, sf_evalcidx, CIndex),
        %populate hash table with info (attrs, vals) from current description
        sfsolve_populate_hash(Binds, Attrs, Values, Pref),
        %need to replace any variable references in vectors
        sfreplace_refs_in_vec(Binds, CIndex),
        %add suspend goals, range goals, default value goals
        sfsolve_pref_goals(Binds, Pref, Pref1, Pref2, Defs),
        %pre-process solve goal
        sfsolve_preprocess(Binds, CIndex, Attrs, Solve, SolveP1),
        %add automatic variable processing
        sfsolve_indomains(Binds, CIndex, AutoVars, InDomains), 
        %aggregate goal
        append(Pref2, SolveP1, SolveP2),
        append(Pref1, SolveP2, SolveP3),
        append(SolveP3, InDomains, SolveP4),
        append(SolveP4, Defs, SolveP),
        %render goal list as a goal tuple
        convert_tuple_list(SolveT, SolveP),
        %write the goal out on verbose
        (Verbose==true -> write("Solving: "), writeln(SolveT), flush(stdout); true),  
        %call the blighter
        call(SolveT),
        %process any user variables
        sfuser(Binds, CIndex),
        %communicate to Java we're done
        sftojava(sfdonegoal),
        %get next constraint
        sffromjava(Binds). 

%%%
%sfsolve_indomains/4: Adds indomain (automatic variable) dispensation
% for each of the given attributes
%%%
sfsolve_indomains(_, _, [], []).
sfsolve_indomains(Binds, CIndex, [Attr|TAttrs], [HID|TIDs]) :-
        hash_get(Binds, (CIndex, Attr), (RefVal, _, Type, _)),
        (Type==enum -> HID=sd:indomain(RefVal); HID=ic:indomain(RefVal)),
        sfsolve_indomains(Binds, CIndex, TAttrs, TIDs).

%%%
%sfsolve_pref_goals/5: Adds suspend, range and default goals to the
% global goal to solve
%%%
sfsolve_pref_goals(_,[],[],[],[]).
sfsolve_pref_goals(Binds, [(RefCI, RefAttr, RangeGoal, Def)|Rem],
                   [Susp|TSusp], RangeGoals, DefGoals):-      
        sfsolve_pref_goals(Binds, Rem, TSusp, RangeGoals1, DefGoals1),
        hash_get(Binds, (RefCI, RefAttr), (Val,_)),
        Susp=suspend(sfsolve_var_sync(Binds, (RefCI, RefAttr),
                                      Val), 1, Val->inst), 
        append(RangeGoals1, RangeGoal, RangeGoals), 
        (nonvar(Def) -> DefGoal= [sfsetdefault(Val,Def)]; DefGoal=[]),
        append(DefGoals1, DefGoal, DefGoals).    

%%%
%sfsetdefault/2: If the current Val for an attribute is a Var then
% unify the first arg, the Var, with its default, the second arg.
%%%
sfsetdefault(Val, _):-nonvar(Val),!.
sfsetdefault(Val, Val).


%%%
%sfsolve_populate_hash/5: Populates hash table Binds with attributes
% and values. Also adds a suspend goal for unbound variables to the
% front of the constraint goal. This will force a sync with Java side
% whenever a variable is bound.
%%%
sfsolve_populate_hash(_, [], [], []).
sfsolve_populate_hash(Binds, [Attr|TAttrs], [Val|TVals], Pref) :-
        sfsolve_populate_hash(Binds, TAttrs, TVals, Pref1),
        hash_get(Binds, sf_evalcidx, CIndex),
        %I'm a VAR with a range and a possible default?
        (Val = sfvar(Var, Range, Def) -> IsVar=yes; 
                                    (Val = sfvar(Var, Range) -> IsVar=yes; IsVar=no)),
        %Yes?
        (IsVar==yes ->
                     %Pref2 will house details for adding a suspend goal
                     Pref2=[(CIndex, Attr, RangeGoal, Def)], 
                     %Extract and importantly assign variable range
                     sfsolve_extract_var_range(Range, Var, Type, RangeGoal),
                     hash_set(Binds, (CIndex, Attr), (Var, [], Type, first));
        %I'm a reference to a variable attribute?             
        (Val = sfref(RefCI, RefAttr) ->
            hash_get(Binds, (RefCI, RefAttr), (RefVal, Refs, Type, First)),
            hash_set(Binds, (CIndex, Attr), (RefVal, [], Type, notfirst)),
            hash_set(Binds, (RefCI, RefAttr), (RefVal, [(CIndex, Attr)
                                                       | Refs], Type, First)),
            Pref2=[];
        %Is my value a component description? If so, we assign the
        % value kept in the hash table to the name of the attribute
        (Val==sfcd -> Val1=Attr;Val1=Val),
        %We are an instantiated value, but are we just partially
        % instantiated? If so, we need to add a suspend goal for
        % future instantiations...
        (sfterm_variables(Val1) -> Pref2=[(CIndex, Attr, [], _)], First=first;
                                   Pref2=[], First=notfirst),
        hash_set(Binds, (CIndex, Attr), (Val1, [], null, First)))),     
        append(Pref2, Pref1, Pref).

%%%
%sfterm_variables/1: Extracts whether there are any variables or
% variable references in a vector (list) value
%%%
sfterm_variables([]):- false.
sfterm_variables([H|_]) :- var(H).
sfterm_variables([sfref(_,_)|_]).
sfterm_variables([H|T]):- sfterm_variables(H),!; sfterm_variables(T).

%%%
%sfreplace_refs_in_vec/2: Replaces variable references in vector (list)
%values.  Uses a "wkr" predicate.
%%%
sfreplace_refs_in_vec(Binds, CIndex):-
        hash_list(Binds, Attrs, Vals),
        sfreplace_refs_in_vec(Binds, CIndex, Attrs, Vals).
sfreplace_refs_in_vec(_, _, [], []).
sfreplace_refs_in_vec(Binds, CIndex, [(CIndex, Attr)|TAs], [(Val, Rem)|TVs]):-!,
        sfreplace_refs_in_vec_wkr(Binds, Val, ValOut),
        hash_set(Binds, (CIndex, Attr), (ValOut, Rem)),
        sfreplace_refs_in_vec(Binds, CIndex, TAs, TVs).
sfreplace_refs_in_vec(Binds, CIndex, [_|TAs], [_|TVs]):-
        sfreplace_refs_in_vec(Binds, CIndex, TAs, TVs).

%%%
%sfreplace_refs_in_vec_wkr/3: Does the work for the aforementioned
% predicate.  For sfref, we retrieve the actual Var from the hash table.
%%%     
sfreplace_refs_in_vec_wkr(_, Var, Var):-var(Var),!.
sfreplace_refs_in_vec_wkr(_, [], []):-!.
sfreplace_refs_in_vec_wkr(Binds, [HIn|TIn], [HOut|TOut]):- 
        sfreplace_refs_in_vec_wkr(Binds, HIn, HOut), !, 
        sfreplace_refs_in_vec_wkr(Binds, TIn, TOut).
sfreplace_refs_in_vec_wkr(Binds, sfref(RefCI, RefAttr), Out):- !, 
                          hash_get(Binds, (RefCI, RefAttr), (Out, _)).
sfreplace_refs_in_vec_wkr(_, Val, Val). 

%%%
%sfsolve_extract_var_range/4: Constructs a range goal for Var on the
% basis of the nature of its Range
%%%
sfsolve_extract_var_range(null, _, null, []) :- !.
sfsolve_extract_var_range(Range, Var, Type, RangeGoal) :-
        sfsolve_extract_var_range(Range, Range, Var, Type, RangeGoal).
sfsolve_extract_var_range([H|_], Range, Var, Type, RangeGoal) :-
       (atom(H) -> Type=enum; Type=integer),
       (Type==enum -> %%Enum Range
                  RangeGoal=[&::(Var, Range)]; 
                  %%Int Range
                  RangeGoal=[#::(Var, Range)]). 

%%%
%sfsolve_preprocess/4: Preprocess goal string. Converts all references
% to attributes (stored in Binds) to vars.
%%%
sfsolve_preprocess(_,_,_,[],[]).
sfsolve_preprocess(Binds, CIndex, Attrs, [HG|TG], [HGOut|TGOut]):-
        hash_get(Binds, sf_evalcidx, CIndex),
        (var(HG) -> HGOut=HG;
        (HG=..[F] -> (hash_get(Binds, (CIndex, F), (HGOut, _)) -> true; HGOut=HG);
                     HG=..[F|Args], 
                         (F==subtype -> sfmapop_(F, FOut), Args1=Args, Lib=none;
                                        sfmapop(Binds, CIndex, F, Args, FOut, Args2, Lib), 
                                        sfsolve_preprocess(Binds, CIndex, Attrs, Args2, Args1)),     
                     (Lib==none -> HGOut=..[FOut|Args1]; HGOut1=..[FOut|Args1], HGOut=Lib:HGOut1))),
    sfsolve_preprocess(Binds, CIndex, Attrs, TG, TGOut).

%%%
%sfpossenumop/1: Is it possible that the argument is an operator for
% enum attributes/values
%%%
sfpossenumop(eq).
sfpossenumop(neq).
sfpossenumop(equal).
sfpossenumop(notequal).
sfpossenumop(alldifferent).

%%%
%sfmapenumop/3: Maps the SF lang operator to its eclipse version, for
% enum operators. Third arg yields library qualification
%%%
sfmapenumop(eq, &=, none).
sfmapenumop(equals, &=, none).
sfmapenumop(neq, &\=, none).
sfmapenumop(notequals, &\=, none).
sfmapenumop(alldifferent, alldifferent, sd).

%%%
%sfmapnotenumop/3: Maps the SF lang operator to its eclipse version, for
% non-enum (ie integer) operators. Third arg yields library qualification
%%%
sfmapnotenumop(eq, #=, none).
sfmapnotenumop(equals, #=, none).
sfmapnotenumop(neq, #\=, none).
sfmapnotenumop(notequals, #\=, none).
sfmapnotenumop(alldifferent, alldifferent, ic).

%%%
%sfobtaintype/4: Obtains the type of a range from its constituents.
%%%
sfobtaintype(Binds, CIndex, Arg, Type) :-
        hash_get(Binds, (CIndex, Arg), (_, _, Type, _)), !.
sfobtaintype(_, _, Arg, enum) :-
        atom(Arg), !.
sfobtaintype(Binds, CIndex, [Arg|_], Type) :- !,
        sfobtaintype(Binds, CIndex, Arg, Type).
sfobtaintype(_, _, _, notenum).

%%%
%sfmapop/7: Maps SF lang operator to appropriate eclipse version
%%%
sfmapop(Binds, CIndex, F, Args, FOut, ArgsOut, Lib) :-
        (F=='-->' ->
            Args=[Cond, Then],  FOut=';', CondThen=..['->',Cond, Then],
            ArgsOut=[CondThen, true], Lib=none;
        ArgsOut=Args,
        (sfpossenumop(F) -> Args = [Arg|_], 
                            sfobtaintype(Binds, CIndex, Arg, Type),
                            (Type==enum -> sfmapenumop(F, FOut, Lib);
                                           sfmapnotenumop(F, FOut, Lib));
                            sfmapop(F, FOut), Lib=none)).  
 
%%%
%sfmap/2: Wkr for its 7-arg version
%%%
sfmapop(F, FOut) :- sfmapop_(F, FOut), !.
sfmapop(F, F).

%%%
%sfmapop_/2: Wkr for sfmapop/2
%%%
sfmapop_(subtype, sfsubtype).
sfmapop_(lt, #<).
sfmapop_(lessthan, #<).
sfmapop_(lte, #=<).
sfmapop_(lessthanequal, #=<).
sfmapop_(gt, #>).
sfmapop_(greaterthan, #>).
sfmapop_(gte, #>=).
sfmapop_(gte, #>=).
sfmapop_(impl, =>).
sfmapop_(implies, =>).
sfmapop_(nt, neg).

%%%
%Declare operator definitions
%%%
:- op(780, xfx, impl).
:- op(780, xfx, implies).
:- op(750, fx, nt).
:- op(700, xfx, eq).
:- op(700, xfx, neq).
:- op(700, xfx, equal).
:- op(700, xfx, notequal).
:- op(700, xfx, subtype). 
:- op(700, xfx, lt).
:- op(700, xfx, lessthan).
:- op(700, xfx, lte).
:- op(700, xfx, lessthanequal).
:- op(700, xfx, gt).
:- op(700, xfx, greaterthan).
:- op(700, xfx, gte).
:- op(700, xfx, greaterthanequal).

%%%
%sfsubtype/2: Effects subtype constraint
%%%
sfsubtype(Attr, Type) :-
        sftojava(sfsubtype(Attr,Type)).

%%%
%sfsolve_var_sync/4: Synchronises variable binding (inc. in
% backtracking) with Java side 
%%%
sfsolve_var_sync(Binds, (RefCI, RefAttr), Val):-
       hash_get(Binds, (RefCI, RefAttr), (Val, Lists, Type, First)),
       (term_variables(Val, []) -> Send=final;
                                   (First=first -> Send=notfinal; Send=no)),
       %Toggle first as set, just in case first currently...
       hash_set(Binds, (RefCI, RefAttr), (Val, Lists, Type, notfirst)),
       (Send==final -> true; 
                       suspend(sfsolve_var_sync(Binds, (RefCI,RefAttr), Val), 1, Val->inst)),       
       (Send==no -> true;
           hash_get(Binds, sf_evalcidx, CIndex),
           hash_get(Binds, sf_evalidx, Index),
           Index1 is Index + 1,
           hash_set(Binds, sf_evalidx, Index1),
           (Send==final ->
               sftojava(sfset(Index, Val, notfirst, [(RefCI, RefAttr) | Lists], CIndex));

               (First==first -> Val1=[]; Val1=Val),
               sftojava(sfset(Index, Val1, First, [(RefCI, RefAttr) | Lists], CIndex))),
           read_exdr(java_to_eclipse, SuccS),
           term_string(Succ, SuccS),
           call(Succ)).

:-dynamic sfuser_back/1.
:-dynamic sfuser_desc/3.
:-dynamic sfuser_refs/1.

%%%
%sfuser/2: Effects user variable processing
%%%
sfuser(Binds, CIndex):-
        assert(sfuser_back(0)),
        write_exdr(eclipse_to_java, sfuser),
        flush(eclipse_to_java), 
        read_exdr(java_to_eclipse, ValOut),        
        sfuser_wkr(Binds, CIndex, ValOut),
        retract_all(sfuser_back(_)),
        retract_all(sfuser_desc(_,_,_)),
        retract_all(sfuser_refs(_)).

%%%
%sfuser_wkr/3: Processes requests for eg range from Java side
%%%
sfuser_wkr(_, _, done).
sfuser_wkr(Binds, CIndex, range) :-
        sfuser_refs(Refs),
        sfuser_wkr_range(Binds, CIndex, Refs).
sfuser_wkr(Binds, CIndex, range(Refs)) :-
        assert(sfuser_refs(Refs)),
        sfuser_wkr_range(Binds, CIndex, Refs).
sfuser_wkr(Binds, CIndex, set(Ref, ValIn1)) :-
        hash_get(Binds, (CIndex, Ref), (Val, _, Type, _)),
        (Type==integer -> integer_atom(ValIn, ValIn1);
                          ValIn=ValIn1),
        sfuser_unify(Binds, CIndex, Ref, Val, ValIn),
        sfuser_back(N),
        sfuser_msg(Binds, CIndex, set(N, Ref, ValIn, noback)).
sfuser_wkr(_, _, _, back):-
        fail.

%%%
%sfuser_msg/3: Feeds back response to Java side, and gets response in
% return
%%%
sfuser_msg(Binds, CIndex, CDOp):-
        write_exdr(eclipse_to_java, CDOp), 
        flush(eclipse_to_java), 
        read_exdr(java_to_eclipse, ValOut),
        sfuser_wkr(Binds, CIndex, ValOut).                      


%%%
%sfuser_unify/5: Performs unification of user-specified value with
% variable pertaining to attribute
%%%
sfuser_unify(_, _, Ref, Val, Val2):-
        retract(sfuser_back(N)), N1 is N+1,
        assert(sfuser_back(N1)),
        assert(sfuser_desc(N1,Ref,Val2)),
        Val=Val2.
sfuser_unify(Binds, CIndex, _, _, _):-
        retract(sfuser_back(N)), N1 is N-1,
        assert(sfuser_back(N1)), 
        retract(sfuser_desc(N,_,_)),
        (N1>0 -> sfuser_desc(N1, Ref, Val);true),
        sfuser_msg(Binds, CIndex, set(N1,Ref,Val,back)).

%%%
%sfuser_wkr_range/3: Processes a range request from Java side for user
% variable. Uses sfuser_range/5. 
%%%
sfuser_wkr_range(Binds, CIndex, Refs):-
        sfuser_range(Binds, CIndex, Refs, Ranges, Succ),
        (Succ==yes -> 
             sfuser_msg(Binds, CIndex, range(Ranges));
             sfuser_msg(Binds, CIndex, norange(Ranges))).

%%%
%sfuser_range/5: Does the work for sfuser_wkr_range/3 in getting the
% range for a user variable.
%%%
sfuser_range(_, _, [], [], yes).
sfuser_range(Binds, CIndex,  [HRef|TRefs], Rans, Succ):-
        atom_string(HRefA, HRef),
        (hash_get(Binds, (CIndex, HRefA), (Var, _, Type, _))->
            (Type==enum -> sd:get_domain_as_list(Var, HRan);
                           ic:get_domain_as_list(Var, HRan)),
            sfuser_range(Binds, CIndex, TRefs, TRans, Succ),
            Rans=[HRan|TRans];
            Succ=no
        ).
        

:- compile(allocator). 




