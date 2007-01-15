package org.smartfrog.services.persistence.deployer;

import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;

public class StorageOrDescriptionDeployerImpl extends PrimProcessDeployerImpl {

	public StorageOrDescriptionDeployerImpl(ComponentDescription descr) {
		super(descr);
		// TODO Auto-generated constructor stub
	}

	public Prim deploy(Reference name, Prim parent, Context params)
			throws SmartFrogDeploymentException {

		try {
			Object obj = target.sfResolve(Storage.CONFIG_DATA, (Object) null,
					false);
			if (Storage.isStorageDescription(obj)) {
				System.out.println("Attempting to deploy " + name
						+ " from storage");
				return SFProcess.getProcessCompound()
						.sfDeployComponentDescription(name, parent,
								(ComponentDescription) obj, null);
			}
		} catch (Exception ex) {
			// drop quietly
		}

		System.out.println("Attempting to deploy " + name
				+ " from component description");
		return super.deploy(name, parent, params);
	}

}
