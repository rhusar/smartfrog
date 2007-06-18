:- compile(core).

set_ncolo_constraints(AsList, As):-
   hosts(Hs), (param(Hs), foreach((_,HVar,_),AsList) do HVar &:: Hs),
   findall(NList, nocolo_list(NList), NLists),
   ( foreach(NList, NLists), param(As) do 
      ( fromto((NList,[]), ([VM|VMs], HList1), 
               (VMs, [H|HList1]), ([], HList)),
        param(As)
        do 
          hash_get(As, VM, (_,H,_))
      ),
      (alldifferent(HList) -> true; 
          writeln("Non colocation constraints violated by initial ground"
                  " assignments."), false)
          
   ).

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
              HVar &:: HsR,
              (HsR=[_] -> true; indomain(HVar)),
              retract_old_asserts(Tick),
              hash_get(As, HVar, Caps),
              update_vms_exceptions(AsList, VM, Reqs, Caps, Tick, HsR), 
              hash_set(As, HVar, Caps1),
              ( foreach(Req, Reqs), foreach(Cap, Caps), foreach(Cap1, Caps1)
                 do Cap1 is Cap-Req
              ), 
              write("Provisionally Allocating: "), write(VM), write(" on host: "), writeln(HVar)
        ).

assign_wkr(AsList) :-
        initialise(As),
        hash_list(As, _, AsList),
        set_ncolo_constraints(AsList, As),
        get_sorted_vm_list(AsList, VMList),
        host_constraints(AsList, As, VMList),
        write_out(As).

