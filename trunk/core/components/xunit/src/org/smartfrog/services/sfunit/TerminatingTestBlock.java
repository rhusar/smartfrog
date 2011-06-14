package org.smartfrog.services.sfunit;

import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.services.assertions.TestBlockImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 *
 * This a component that immediately terminates when started. It's here for testing only.
 *
 */

public class TerminatingTestBlock extends TestBlockImpl implements TestBlock {

    public static final String ATTR_SUCCEED = "succeed";

    public TerminatingTestBlock() throws RemoteException {
    }

    @Override
    protected void checkActionDefined() throws SmartFrogResolutionException, RemoteException {

    }

    @Override
    protected void startChildAction() throws RemoteException, SmartFrogException {
    }

    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        boolean succeeded = sfResolve(ATTR_SUCCEED, true, true);
        TerminationRecord tr;
        tr = new TerminationRecord( succeeded ? TerminationRecord.NORMAL: TerminationRecord.ABNORMAL,
                "TerminatingTestBlock finished",
                sfCompleteNameSafe());
        end(tr);
    }
}
