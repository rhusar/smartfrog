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
