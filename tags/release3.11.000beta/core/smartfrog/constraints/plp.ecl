:- compile(core).

initial_compatable_ncolo(AsList) :-
        member((VM1, Hn, _), AsList), nonvar(Hn),
        member((VM2, Hn, _), AsList), VM1\==VM2, 
        nocolo_list(NList),
        member(VM1, NList), member(VM2, NList), !, 
        write("Identified initial non-colocation conflict, in attempting to put "), 
        write(VM1), write(" and "), write(VM2), write(" on "), write(Hn), writeln(". Failing..."),
                                                    false.        
initial_compatable_ncolo(_).

compatable_ncolo(AsList,VM,Hn):-
        findall(VM1, (member((VM1, HVar, _), AsList), Hn==HVar), VMs),
        member(VM1, VMs), nocolo_list(NList), 
        member(VM,NList), member(VM1,NList), !, 
        write("Identified a non-colocation conflict, in attempting to put "), 
        write(VM),write(" on "), write(Hn), writeln(". Trying another"
                                                    " host..."), false.
compatable_ncolo(_,_,_).

host_constraints(AsList, As, VMs) :-
        compile_hosts(AsList, HInfo),
        (foreach((H,C), HInfo), param(As) do hash_set(As, H, C)),

        (foreach(VM, VMs), count(Tick, 0, _), param(As, AsList)
         do
              hash_get(As, VM, Rec),
              Rec = (_, HVar, Reqs),
              findall(Cap, exc(VM,Cap,_), Exc), 
              constrain_vm_range(As, Reqs, Exc, HsR),
              count_backtracks,
              member(HVar, HsR),
              retract_old_asserts(Tick),
              hash_get(As, HVar, Caps),
              update_vms_exceptions(AsList, VM, Reqs, Caps, Tick, HsR), 
              compatable_ncolo(AsList, VM, HVar),
              hash_set(As, HVar, Caps1),
              ( foreach(Req, Reqs), foreach(Cap, Caps), foreach(Cap1, Caps1)
                 do Cap1 is Cap-Req
              ), 
              write("Provisionally Allocating: "), write(VM), write(" on host: "), writeln(HVar)
        ).

assign_wkr(AsList) :-
        initialise(As),
        hash_list(As, _, AsList),
        initial_compatable_ncolo(AsList),
        get_sorted_vm_list(AsList, VMList),
        host_constraints(AsList, As, VMList),
        write_out(As).

