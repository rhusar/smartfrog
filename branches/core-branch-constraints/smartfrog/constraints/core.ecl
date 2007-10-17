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

add_path(PT) :- assert(root(PT)). 

source_compose(FNIn,FNOut) :- 
        (root(PT) -> concat_strings(PT, FNIn, FNOut); FNOut=FNIn). 

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

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Preprocessing
:- lib(hash).
:- dynamic sfvcnt/1.

sfvcnt(1).

preprocess(Cxt,G,Gs) :-
                 hash_create(AttrCount),         
                 convert_tuple_list(G, GL),
                 preprocess_goal(AttrCount, Cxt, GL, GLP, GL2, GLS),
                 append(GLP, GL2, GL3),
                 append(GL3, GLS, GL1),
                 convert_tuple_list(G1, GL1),
                 term_string(G1, Gs).

preprocess_sfop_done(sfget,(sfget,_)).
preprocess_sfop_done(sfset,(_,sfset)).
preprocess_sfop_done(sfuser,(_,sfset)).
preprocess_sfop_done(sfattr,(sfget,sfset)).

preprocess_attr(AC, Cxt, F, HGOut, Key, GLP1, GLP, GLS1, GLS):-
            (F==sfuser -> HGOut=sfuser((sfvar(0), Cxt), Key, sfvar(Cnt));
                          HGOut=sfvar(Cnt)), 
            (hash_get(AC,Key,(Cnt,SFG,SFS)) ->
                (preprocess_sfop_done(F,(SFG,SFS))->
                    GLP=GLP1, GLS=GLS1; 
                    (F==sfget ->   hash_set(AC, Key, (Cnt,sfget,SFS)),
                                   GLP=[sfget((sfvar(0),Cxt),Key,sfvar(Cnt))|GLP1],GLS=GLS1;
                    (F==sfset ->   hash_set(AC,Key,(Cnt, SFG, sfset)),
                                   GLS=[sfset((sfvar(0),Cxt),Key,sfvar(Cnt))|GLS1],GLP=GLP1;
                    (F==sfuser ->  hash_set(AC,Key,(Cnt, SFG, sfset)),
                                   GLS=[sfset((sfvar(0),Cxt),Key,sfvar(Cnt))|GLS1],GLP=GLP1;
                    (SFG==sfget -> GLP=GLP1;GLP=[sfget((sfvar(0),Cxt),Key,sfvar(Cnt))|GLP1]),
                    (SFS==sfset -> GLS=GLS1;GLS=[sfset((sfvar(0),Cxt),Key,sfvar(Cnt))|GLS1]),
                    hash_set(AC,Key,(Cnt,sfget,sfset))))));
                %%No entry present
                incr_cnt(Cnt), 
                (F==sfget -> hash_set(AC, Key, (Cnt, sfget, nil)),
                             GLP=[sfget((sfvar(0),Cxt),Key, sfvar(Cnt))|GLP1],GLS=GLS1;
                (F==sfset -> hash_set(AC, Key, (Cnt,nil,sfset)),
                             GLS=[sfset((sfvar(0),Cxt),Key, sfvar(Cnt))|GLS1],GLP=GLP1;

                             hash_set(AC, Key, (Cnt,nil,sfset)),
                             GLP=[sfget((sfvar(0),Cxt),Key, sfvar(Cnt))|GLP1],
                             GLS=[sfset((sfvar(0),Cxt),Key, sfvar(Cnt))|GLS1]))).
                                          
preprocess_sfop(sfget).
preprocess_sfop(sfset).
preprocess_sfop(sfattr).
preprocess_sfop(sfuser).

preprocess_attr_try(AC,Cxt,F,HGOut,Args,GLP1,GLP,GLS1,GLS) :- 
        (Args=[Key] -> preprocess_attr(AC, Cxt, F, HGOut, Key, GLP1,
                                       GLP, GLS1, GLS);
                       (Args=[Key,Var] -> GCxt=(sfvar(0),Cxt); Args=[GCxt,Key,Var]),                   
                       preprocess_goal(AC, Cxt, [Var], _, [Var1], _),
                       GLP=GLP1, GLS=GLS1,
                       HGOut=..[F,GCxt,Key,Var1]).

preprocess_goal(_, _, [], [], [], []).
preprocess_goal(AC, Cxt, [HG|TG], GLP, [HGOut|TGOut], GLS) :-
        preprocess_goal(AC, Cxt, TG, GLP1, TGOut, GLS1),
        (var(HG) -> incr_cnt(Cnt), HG=HGOut, HGOut=sfvar(Cnt), GLP=GLP1, GLS=GLS1;
                    HG =..[F|Args],
                    (F==sfcxt ->
                        HGOut=(sfvar(0),Cxt), GLP=GLP1, GLS=GLS1;
                    (F==sfvar ->
                        HGOut=HG, GLP=GLP1, GLS=GLS1;  
                        (preprocess_sfop(F)-> preprocess_attr_try(AC,Cxt,F,HGOut,Args,
                                                GLP1,GLP,GLS1,GLS);
                            preprocess_goal(AC, Cxt, Args, ArgsP, Args1, ArgsS),
                            append(GLP1, ArgsP, GLP), append(GLS1, ArgsS, GLS), HGOut =..[F|Args1])))).


preprocess2(G,Gs) :- 
                 hash_create(AttrCount),         
                 convert_tuple_list(G, GL),
                 preprocess_goal2(AttrCount, GL, GL1),
                 convert_tuple_list(G1, GL1),
                 term_string(G1, Gs).

preprocess_goal2(_, [], []).
preprocess_goal2(AC, [HG|TG], [HGOut|TGOut]) :-
        preprocess_goal2(AC, TG, TGOut),
        (HG = sfvar(Key) ->
            (hash_get(AC, Key, HGOut) -> true;hash_set(AC, Key, HGOut));
            HG=..[F|Args],
            preprocess_goal2(AC, Args, Args1),
            HGOut=..[F|Args1]).

incr_cnt(Cnt) :-
               sfvcnt(Cnt),
               retract(sfvcnt(Cnt)),
               Cnt1 is Cnt + 1,
               assert(sfvcnt(Cnt1)).
        
       
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Description hierarchy state

%%%
%solve/0: Solve an sfConfig hierarchy
%%%
sfsolve :- hash_create(Binds), 
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

sfsolve_process(sfstop, _).
sfsolve_process(sfsolve(Attrs, Values, CIndex, Solve), Binds) :-
        hash_set(Binds, sf_evalcidx, CIndex),
        %writeln(("1", Attrs, Values, CIndex, Solve)), flush(stdout),
        %writeln("hellohello"), flush(stdout),
        sfsolve_populate_hash(Binds, Attrs, Values, Pref),
        %writeln(("2", Binds)), flush(stdout),
        sfsolve_suspend_goals(Binds, Pref, Pref1),
        sfsolve_preprocess(Binds, Attrs, Solve, SolveP1),
        append(Pref1, SolveP1, SolveP),
        convert_tuple_list(SolveT, SolveP),
        %writeln(("converted",SolveT)), flush(stdout),
        call(SolveT),
        %writeln("sfdonegoal"), flush(stdout),
        sftojava(sfdonegoal),
        sffromjava(Binds). 
   
sfsolve_suspend_goals(_,[],[]).
sfsolve_suspend_goals(Binds, [(RefCI, RefAttr)|Rem], [Susp|TSusp]):-      
        sfsolve_suspend_goals(Binds, Rem, TSusp),
        hash_get(Binds, (RefCI, RefAttr), (Val,_)),
        Susp=suspend(sfsolve_var_sync(Binds, (RefCI, RefAttr), Val), 1, Val->inst).
        %writeln(("Susp:", Susp)), flush(stdout).

%%%
%sfsolve_populate_hash/5: Populates hash table Binds with attributes
% and values. Also adds a suspend goal for unbound variables to the
% front of the constraint goal. This will force a sync with Java side
% whenever a variable is bound.
%%%
sfsolve_populate_hash(_, [], [], []).
sfsolve_populate_hash(Binds, [Attr|TAttrs], [Val|TVals], Pref) :-
        %writeln(("3",Attr, Val)), flush(stdout),
        sfsolve_populate_hash(Binds, TAttrs, TVals, Pref1),
        hash_get(Binds, sf_evalcidx, CIndex),
        %writeln(CIndex), flush(stdout),
        (var(Val) -> Pref2=[(CIndex, Attr)], 
                     hash_set(Binds, (CIndex, Attr), (Val,[]));
        (Val = sfref(RefCI, RefAttr) ->
            hash_get(Binds, (RefCI, RefAttr), (RefVal, Refs)),
            hash_set(Binds, (CIndex, Attr), (RefVal, [])),
            hash_set(Binds, (RefCI, RefAttr), (RefVal, [(CIndex, Attr)
                                                       | Refs])),
            Pref2=[];
            %(member((RefCI, RefAttr), Pref1) -> Pref2=[]; 
            %                                    Pref2=[(RefCI, RefAttr)]);
        hash_set(Binds, (CIndex, Attr), (Val, [])),     
        (term_variables(Val, []) -> Pref2=[];
                                    Pref2=[(CIndex, Attr)]))),
        append(Pref2, Pref1, Pref).

%%%
%sfsolve_preprocess/4: Preprocess goal string. Converts all references
% to attributes (stored in Binds) to vars.
%%%
sfsolve_preprocess(_,_,[],[]).
sfsolve_preprocess(Binds, Attrs, [HG|TG], [HGOut|TGOut]):-
        %writeln(HG), flush(stdout),
        hash_get(Binds, sf_evalcidx, CIndex),
        (var(HG) -> HGOut=HG;
        (HG=..[F] -> (hash_get(Binds, (CIndex, F), (HGOut, _)) -> true; HGOut=HG);
         HG=..[F|Args], sfsolve_preprocess(Binds, Attrs, Args, Args1),
                HGOut=..[F|Args1])),
    sfsolve_preprocess(Binds, Attrs, TG, TGOut).

%%%
%sfsolve_var_sync/4: Synchronises variable binding (inc. in
% backtracking) with Java side 
%%%
sfsolve_var_sync(Binds, (RefCI, RefAttr), Val):-
       hash_get(Binds, sf_evalcidx, CIndex),
       %writeln(("here", (RefCI, RefAttr), Val, CIndex)), flush(stdout),
       hash_get(Binds, sf_evalidx, Index),
       Index1 is Index + 1,
       hash_set(Binds, sf_evalidx, Index1),
       hash_get(Binds, (RefCI, RefAttr), (Val, Lists)),
       %writeln(sfset(Index, Val, [(RefCI, RefAttr) | Lists], CIndex)), flush(stdout),
       sftojava(sfset(Index, Val, [(RefCI, RefAttr) | Lists], CIndex)).       


%%Need to reduce these as appropriate: 12/10/07
:-dynamic sfuser_back/1.
:-dynamic sfuser_desc/3.
:-dynamic sfuser_refs/1.

sfget((SFBinds, Cxt), Attr, Val) :- 
        sfop(SFBinds, Cxt, sfget, Attr, Val).

sfset((SFBinds, Cxt), Attr, Val) :- 
        sfop(SFBinds, Cxt, sfset, Attr, Val).

sfuser((SFBinds, Cxt), Attr, Val) :-
        sfop(SFBinds, Cxt, sfset, Attr, Val).

sfuser(SFBinds):-
        assert(sfuser_back(0)),
        write_exdr(eclipse_to_java, sfuser),
        flush(eclipse_to_java), 
        read_exdr(java_to_eclipse, ValOut),        
        sfuser_wkr(SFBinds, ValOut).

sfuser_wkr(_, done).

sfuser_wkr(SFBinds, range) :-
        sfuser_refs(Refs),
        sfuser_wkr_range(SFBinds, Refs).

sfuser_wkr(SFBinds, range(Refs)) :-
        assert(sfuser_refs(Refs)),
        sfuser_wkr_range(SFBinds, Refs).

sfuser_wkr(SFBinds, set(Ref, ValIn)) :-
        atom_string(RefA, Ref),
        hash_get(SFBinds, RefA, Val),
        sfuser_unify(SFBinds, Ref,Val,ValIn),
        sfuser_back(N),
        sfuser_msg(SFBinds, set(N,Ref,ValIn,"noback")).

sfuser_wkr(_, back):-
        fail.

sfuser_unify(_, Ref, Val, Val2):-
        retract(sfuser_back(N)), N1 is N+1,
        assert(sfuser_back(N1)),
        assert(sfuser_desc(N1,Ref,Val2)),
        Val=Val2.

sfuser_unify(SFBinds, _, _, _):-
        retract(sfuser_back(N)), N1 is N-1,
        assert(sfuser_back(N1)), 
        retract(sfuser_desc(N,_,_)),
        (N1>0 -> sfuser_desc(N1, Ref, Val);true),
        sfuser_msg(SFBinds, set(N1,Ref,Val,"back")).

sfuser_msg(SFBinds, CDOp):-
        write_exdr(eclipse_to_java, CDOp), 
        flush(eclipse_to_java), 
        read_exdr(java_to_eclipse, ValOut),
        sfuser_wkr(SFBinds, ValOut).                      

sfuser_wkr_range(SFBinds, Refs):-
        sfuser_range(SFBinds, Refs, Ranges, Succ),
        (Succ==yes -> 
             sfuser_msg(SFBinds, range(Ranges));
             sfuser_msg(SFBinds, norange(Ranges))).


sfuser_range(_, [], [], yes).
sfuser_range(SFBinds, [HRef|TRefs], Rans, Succ):-
        atom_string(HRefA, HRef),
        (hash_get(SFBinds, HRefA, Var)->
            get_domain_as_list(Var, HRan),
            sfuser_range(SFBinds, TRefs, TRans, Succ),
            Rans=[HRan|TRans];
            Succ=no
        ).
        
sfop(SFBinds, Cxt, Op, AttrA, Val) :-
        (atom(AttrA) -> atom_string(AttrA, Attr); AttrA=Attr),
        CDOpL = [Op, Cxt, Attr, Val],
        CDOp =..CDOpL,
        write_exdr(eclipse_to_java, CDOp), 
        flush(eclipse_to_java), 
        read_exdr(java_to_eclipse, ValOut),
        (ValOut=success -> true;
        (ValOut=success(Ref, Val) -> 
            (hash_get(SFBinds, Ref, Val)->true;hash_set(SFBinds,Ref,Val));fail)).

sfop(_,_,_,_,_) :-
        write_exdr(eclipse_to_java, sfundo), 
        flush(eclipse_to_java), 
        read_exdr(java_to_eclipse, _), fail.





