%% Resource Allocator logic
%%
%% 23/04/2007 -- Awaiting improvement, re storing host caps in hash table

init :- assert_state(hosted(_,_):-false), 
        assert_state(colo_list(_):-false), 
        assert_state(nocolo_list(_):-false).

init_backtracks :-
        assert_state(backtracks(0)).

get_backtracks(B) :-
        clause_state_level(backtracks(B)),!.

count_backtracks :-
        replace_state_level(deep_fail(false)).
count_backtracks :-
        clause_state_level(deep_fail(false)), %may fail
        replace_state_level(deep_fail(true)),
        get_backtracks(B), B1 is B+1,
        replace_state_level(backtracks(B1)),!,
        fail.

allocate(Producers, Producer_Caps, Consumers, Consumer_Caps, Allocations, Colos, NoColos, Hosted):-
            writeln("+++++++++++In allocate++++++++++++"),		    
            sos,
            preprocess_producers(Producers, Producer_Caps),
            preprocess_consumers(Consumers, Consumer_Caps),
            preprocess_colocations(Colos),
            preprocess_noncolocations(NoColos),
            preprocess_hosteds(Hosted),
            allocate(Allocations), 
            eos. 

preprocess_producers([], []).
preprocess_producers([HP|TP], [HCa|TCa]):-
        assert_state(host_cap(HP, HCa)), preprocess_producers(TP, TCa).

preprocess_consumers([], []).
preprocess_consumers([HC|TC], [HCa|TCa]):-
        assert_state(vm_req(HC, HCa)), preprocess_consumers(TC, TCa).

preprocess_colocations([]).
preprocess_colocations([HC|TC]):-
        assert_state(colo_list(HC)),
        preprocess_colocations(TC).

preprocess_noncolocations([]).
preprocess_noncolocations([HC|TC]):-
        assert_state(nocolo_list(HC)),
        preprocess_noncolocations(TC).

preprocess_hosteds([]).
preprocess_hosteds([[V, H]|TC]):-
        assert_state(hosted(V, H)),
        preprocess_hosteds(TC).

colo(Key,CList) :- clause_state_level(colo_list(CList)), CList = [Key | _].
colo(VM,[VM]) :- clause_state_level(vms(VMs)), member(VM, VMs), 
        not (clause_state_level(colo_list(CList)), member(VM, CList)).

remove_dups(L1, L2):-
        (fromto((L1,[]), ([H|T],L3), (T,L4), ([],L2))
         do (var(H) -> L4=[H|L3]; (member(H,L3) -> L4=L3; L4=[H|L3]))). 

initialise(As) :-
    init_backtracks,
    hash_create(As),
    (clause_state_level(hosts(_))->true;  
        findall(Host, clause_state_level(host_cap(Host,_)), Hosts), assert_state(hosts(Hosts))),
    (clause_state_level(vms(_))->true;  
        findall(VM, clause_state_level(vm_req(VM,_)), VMs), assert_state(vms(VMs))),

    findall((Key, CList, ResReqs), (colo(Key,CList),
                                    aggregate_colo_reqs(Key,ResReqs)), Reqs),
    
    (
       foreach((Key, CList, ResReqs), Reqs),
       param(As)
       do
        %%%Ground Assignments...
        findall(Host, (member(VM, CList), clause_state_level(hosted(VM, Host))),KeyHosts1),
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
           clause_state_level(vm_req(VM, Reqs3)),           
           (Reqs1==[] -> Reqs2=Reqs3; aggregate_req(Reqs1, Reqs3, Reqs2))
        ).

aggregate_req(Rs1, Rs2, Rs3) :-
        ( foreach(R1, Rs1), 
          foreach(R2, Rs2), foreach(R3, Rs3) do R3 is R1 + R2).

compile_hosts(AsList, HInfos):-
        clause_state_level(hosts(HNames)),
        (    param(AsList), 
             foreach(Hn, HNames), 
             foreach(HInfo, HInfos)
          do
             clause_state_level(host_cap(Hn, Caps)), 
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

write_out(As,AsAtts) :-
        clause_state_level(vms(VMs)), 
        ( foreach(VM, VMs), foreach(AsAtt,AsAtts), param(As) do
            colo(Key, CList),
            member(VM, CList), !,
            hash_get(As, Key, (_,Host,_)), write(VM),
            write(" deployed on host "),
            writeln(Host), AsAtt=Host).

allocate :- allocate(_).

allocate(A) :- 
         (allocate_wkr(A) -> writeln("PLACEMENT SUCCESS: Successfully placed VMs."), write_backtracks;
                           writeln("PLACEMENT FAILED: Could not place VMs."), 
                           write_backtracks, false), flush(stdout).


write_backtracks:-
        write("Backtrack count: "), get_backtracks(B), writeln(B).


constrain_vm_range(As, Reqs, Exc, Hs, HsR):-
        (param(As, Reqs), 
         fromto((Hs, [], Exc), ([H|T], Hs1, Caps1), (T, Hs2, Caps2),
                ([], HsR, _)) do
            hash_get(As, H, Caps),
            ((not member(Caps, Caps1), 
              (foreach(Req, Reqs), foreach(Cap, Caps) do Req =< Cap))
            -> 
                append(Hs1,[H],Hs2),Caps2=[Caps|Caps1]);
                Hs2=Hs1, Caps2=Caps1
        ),!,HsR\=[].

%Trivial satisfaction if singleton...
update_vms_exceptions(_, _, _, _, _, [_]) :- !.   
%Or, if first
update_vms_exceptions(_, VM, _, Caps, Tick, _) :- 
    not clause_state_level(pend(VM,_,Tick)), !, 
    assert_state(pend(VM,Caps,Tick)). 
%Otherwise, copy @HVar-1 to other VM's Exc list
update_vms_exceptions(AsList, VM, Reqs, Caps, Tick, _) :-
    clause_state_level(pend(VM,Caps1,Tick)), retract_state(pend(VM,Caps1,Tick)),
    assert_state(pend(VM,Caps,Tick)),
    findall(VM1, (member((VM1,_,Reqs), AsList), VM1\==VM), VMs),
    (param(Caps1, Tick), foreach(VM1, VMs) do
      assert_state(exc(VM1,Caps1,Tick))
    ),!.

retract_old_asserts(Tick):-
        findall(_, (clause_state_level(pend(V,C,Tick1)), Tick1>Tick, 
                       retract_state(pend(V,C,Tick1))), _),
        findall(_,  (clause_state_level(exc(V,C,Tick1)), Tick1>Tick, 
                       retract_state(exc(V,C,Tick1))), _).


set_ncolo_constraints(AsList, As):-
   clause_state_level(hosts(Hs)), 
   (param(Hs), foreach((_,HVar,_),AsList) do &::(HVar,Hs)),
   findall(NList, clause_state_level(nocolo_list(NList)), NLists),
   ( foreach(NList, NLists), param(As) do 
      ( fromto((NList,[]), ([VM|VMs], HList1), 
               (VMs, [H|HList1]), ([], HList)),
        param(As)
        do 
          hash_get(As, VM, (_,H,_))
      ),
      sd:alldifferent(HList)
   ).

host_constraints(AsList, As, VMs) :-
        compile_hosts(AsList, HInfo),
        (foreach((H,C), HInfo), param(As) do hash_set(As, H, C)),

        (foreach(VM, VMs), count(Tick, 0, _), param(As, AsList)
         do
              hash_get(As, VM, Rec),
              Rec = (_, HVar, Reqs),
              findall(Cap, clause_state_level(exc(VM,Cap,_)), Exc), 
              sd:get_domain_as_list(HVar, Hs),
              constrain_vm_range(As, Reqs, Exc, Hs, HsR),
              count_backtracks,
              &::(HVar,HsR),
              (var(HVar) -> sd:indomain(HVar); true),
              retract_old_asserts(Tick),
              hash_get(As, HVar, Caps),
              update_vms_exceptions(AsList, VM, Reqs, Caps, Tick, HsR), 
              hash_set(As, HVar, Caps1),
              ( foreach(Req, Reqs), foreach(Cap, Caps), foreach(Cap1, Caps1)
                 do Cap1 is Cap-Req
              ), 
              write("Provisionally Allocating: "), write(VM), write(" on host: "), writeln(HVar), flush(stdout)
        ).

allocate_wkr(AsAtts) :-
        initialise(As),
        hash_list(As, _, AsList),
        set_ncolo_constraints(AsList, As),
        get_sorted_vm_list(AsList, VMList),
        host_constraints(AsList, As, VMList),
        write_out(As,AsAtts1),
        AsAtts=AsAtts1.  %need to split out for correct suspend behaviour
