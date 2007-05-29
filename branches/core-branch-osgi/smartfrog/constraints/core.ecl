%%Core theory
:- lib(sd).
:- lib(hash).

:- dynamic colo_list/1, nocolo_list/1, hosted/2, host_cap/2, vm_req/2,
   hosts/1, vms/1, default_vm_req/1, default_host_cap/1.
:- dynamic exc/3, pend/3.
:- local variable(backtracks), variable(deep_fail).

init :- retract_all(colo_list(_)), retract_all(nocolo_list(_)), 
        retract_all(hosted(_,_)), retract_all(host_cap(_,_)),
        retract_all(vm_req(_,_)), retract_all(hosts(_)),
        retract_all(vms(_)), 
        retract_all(default_vm_req(_)), retract_all(default_host_cap(_)), 
        retract_all(pend(_,_,_)), retract_all(exc(_,_,_)), 
        assert(hosted(_,_):-false), assert(colo_list(_):-false), assert(nocolo_list(_):-false).

init_backtracks :-
        setval(backtracks,0).

get_backtracks(B) :-
        getval(backtracks,B).

count_backtracks :-
        setval(deep_fail,false).
count_backtracks :-
        getval(deep_fail,false),        % may fail
        setval(deep_fail,true),
        incval(backtracks), writeln("Backtracking..."),
        fail.

vm_reqs(V, R) :- 
        vm_req(V, R),R\==nil,!.
vm_reqs(V, R) :- 
        vm_req(V, nil), default_vm_req(R).

host_caps(H, C) :- 
        host_cap(H, C),C\==nil,!.
host_caps(H, C) :- 
        host_cap(H, nil), default_host_cap(C). 
 
colo(Key,CList) :- colo_list(CList), CList = [Key | _].
colo(VM,[VM]) :- vms(VMs), member(VM, VMs), 
        not (colo_list(CList), member(VM, CList)).

remove_dups(L1, L2):-
        (fromto((L1,[]), ([H|T],L3), (T,L4), ([],L2))
         do (member(H,L3) -> L4=L3; L4=[H|L3])). 

initialise(As) :-
    init_backtracks,
    hash_create(As),
    (clause(hosts(_))->true;  
        findall(Host, host_cap(Host,_), Hosts), assert(hosts(Hosts))),
    (clause(vms(_))->true;  
        findall(VM, vm_req(VM,_), VMs), assert(vms(VMs))),

    findall((Key, CList, ResReqs), (colo(Key,CList),
                                    aggregate_colo_reqs(Key,ResReqs)), Reqs),
    
    (
       foreach((Key, CList, ResReqs), Reqs),
       param(As)
       do
        %%%Ground Assignments...
        findall(Host, (member(VM, CList), hosted(VM, Host)),KeyHosts1),
        remove_dups(KeyHosts1, KeyHosts), 
        (KeyHosts = [HVar] -> true; 
                              (KeyHosts\=[] -> 
                                  writeln("Colocated vms have been assigned to different hosts. Failing..."),
                                  false; true
                              )
        ),
        hash_set(As, Key, (Key,HVar,ResReqs))
    ). 


aggregate_colo_reqs(Key, Reqs) :-
        colo(Key,VMs),
        (fromto((VMs,[]), ([VM|TVMs], Reqs1), 
                             (TVMs, Reqs2), ([],Reqs)) do
           vm_reqs(VM, Reqs3),           
           (Reqs1==[] -> Reqs2=Reqs3; aggregate_req(Reqs1, Reqs3, Reqs2))
        ).

aggregate_req(Rs1, Rs2, Rs3) :-
        ( foreach(R1, Rs1), 
          foreach(R2, Rs2), foreach(R3, Rs3) do R3 is R1 + R2).

compile_hosts(AsList, HInfos):-
        hosts(HNames),
        (    param(AsList), 
             foreach(Hn, HNames), 
             foreach(HInfo, HInfos)
          do
             host_caps(Hn, Caps), 
             aggregate_host_reqs(Hn, AsList, HReqs),
             (HReqs==[] -> Caps1=Caps;
                 (  is_enough_resources(HReqs, Caps, Caps1) -> true;
                    (write("Not enough resources on host: "), write(Hn), 
                     write(" for prespecified host allocations. Failing... "),
                     writeln("This behaviour will be improved in constraint"
                       " version."), false)
                 )
              ), 
              HInfo = (Hn,Caps1)
         ).

aggregate_host_reqs(Hn, AsList, Reqs):-
         (param(Hn), fromto((AsList,[]), ([(_,Host,Reqs1)|TAsList], Reqs2), 
                                         (TAsList,Reqs3), ([],Reqs)) do
           (Host==Hn -> 
            (Reqs2==[] -> Reqs3=Reqs1; aggregate_req(Reqs2, Reqs1, Reqs3)); Reqs3=Reqs2)).

is_enough_resources([], [], []).
is_enough_resources([Req|Reqs], [Cap|Caps], [Cap1|Caps1]):-
        is_enough_resources(Reqs, Caps, Caps1),
        (Req=<Cap -> Cap1 is Cap-Req; false).

get_sorted_vm_list(AsList, VMList) :-
        findall(VM, (member((VM, Host, _), AsList), var(Host)), VMList1),
        sort(VMList1, VMList).

write_out(As) :-
        vms(VMs), 
        ( foreach(VM, VMs), param(As) do
            colo(Key, CList),
            member(VM, CList), !,
            hash_get(As, Key, (_,Host,_)), write(VM),
            write(" deployed on host "),
            writeln(Host)). 

assign :- assign(_).

assign(A) :- 
         (assign_wkr(A) -> writeln("PLACEMENT SUCCESS: Successfully placed VMs."), write_backtracks;
                           writeln("PLACEMENT FAILED: Could not place VMs."), 
                           write_backtracks, false).


write_backtracks:-
        write("Backtrack count: "), get_backtracks(B), writeln(B).


constrain_vm_range(As, Reqs, Exc, HsR):-
        hosts(Hs),
        (param(As, Reqs), 
         fromto((Hs, [], Exc), ([H|T], Hs1, Caps1), (T, Hs2, Caps2),
                ([], HsR, _)) do
            hash_get(As, H, Caps),
            ((not member(Caps, Caps1), 
              (foreach(Req, Reqs), foreach(Cap, Caps) do Req =< Cap))
            -> 
                append(Hs1,[H],Hs2),Caps2=[Caps|Caps1]);
                Hs2=Hs1, Caps2=Caps1
        ),!,
        (HsR=[]->false;true).

%Trivial satisfaction if singleton...
update_vms_exceptions(_, _, _, _, _, [_]) :- !.   
%Or, if first
update_vms_exceptions(_, VM, _, Caps, Tick, _) :- 
    not clause(pend(VM,_,Tick)), !, 
    assert(pend(VM,Caps,Tick)). 
%Otherwise, copy @HVar-1 to other VM's Exc list
update_vms_exceptions(AsList, VM, Reqs, Caps, Tick, _) :-
    clause(pend(VM,Caps1,Tick)), retract(pend(VM,Caps1,Tick)),
    assert(pend(VM,Caps,Tick)),
    findall(VM1, (member((VM1,_,Reqs), AsList), VM1\==VM), VMs),
    (param(Caps1, Tick), foreach(VM1, VMs) do
      assert(exc(VM1,Caps1,Tick))
    ),!.

retract_old_asserts(Tick):-
        findall(_, (pend(V,C,Tick1), Tick1>Tick, 
                       retract(pend(V,C,Tick1))), _),
        findall(_,  (exc(V,C,Tick1), Tick1>Tick, 
                       retract(exc(V,C,Tick1))), _).


