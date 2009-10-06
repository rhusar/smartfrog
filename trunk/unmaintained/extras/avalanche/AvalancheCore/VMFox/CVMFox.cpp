#include "CVMFox.h"

CVMFox::~CVMFox()
{
	// disconnect from host
	disconnect();
}

void discoveryProc(VixHandle jobHandle, VixEventType eventType, VixHandle moreEventInfo, void *clientData)
{
	// return if it's the wrong event
	if (eventType != VIX_EVENTTYPE_FIND_ITEM)
		return;
	else
	{
		// found a vm
		char * url = NULL;
		VixError err = Vix_GetProperties(	moreEventInfo,
											VIX_PROPERTY_FOUND_ITEM_LOCATION,
											&url,
											VIX_PROPERTY_NONE);

		// print the url if a machine has been found
		if (err == VIX_OK)
			printf("%s\n", url);

		// cleanup
		Vix_FreeBuffer(url);
	}
}

void CVMFox::listRunningVMs()
{
	if (connect())
	{
		// find all running machines
		VixHandle jobHandle = VixHost_FindItems(	m_HostHandle,
													VIX_FIND_RUNNING_VMS,
													VIX_INVALID_HANDLE,				// searchCriteria
													-1,								// timeout
													discoveryProc,					// callback
													NULL);							// clientData

		// wait for the job to finish
		VixError err = VixJob_Wait(	jobHandle,
									VIX_PROPERTY_NONE);

		// not needed anymore
		Vix_ReleaseHandle(jobHandle);

		if (err != VIX_OK)
			printf(Vix_GetErrorText(err, NULL));
	}
}

void CVMFox::registerVM()
{
	if (connect())
	{
		// register the vm to the vmware server
		VixHandle jobHandle = VixHost_RegisterVM(	m_HostHandle,
													m_pVMPath,
													NULL,
													NULL);

		// wait for the job to finish
		VixError err = VixJob_Wait(	jobHandle,
									VIX_PROPERTY_NONE);

		// print error output if necessary
		if (err != VIX_OK)
			printf(Vix_GetErrorText(err, NULL));
	}
}

void CVMFox::unregisterVM()
{
	if (connect())
	{
		// unregister the vm to the vmware server
		VixHandle jobHandle = VixHost_UnregisterVM(	m_HostHandle,
													m_pVMPath,
													NULL,
													NULL);

		// wait for the job to finish
		VixError err = VixJob_Wait(	jobHandle,
									VIX_PROPERTY_NONE);

		// print error output if necessary
		if (err != VIX_OK)
			printf(Vix_GetErrorText(err, NULL));
	}
}

void CVMFox::startVM()
{
	if (aquireVMHandle())
	{
		// power on the vm
		VixHandle jobHandle = VixVM_PowerOn(	m_VMHandle,
												VIX_VMPOWEROP_NORMAL,
												VIX_INVALID_HANDLE,
												NULL,
												NULL);

		// wait for the job to finish
		VixError err = VixJob_Wait(	jobHandle,
									VIX_PROPERTY_NONE);

		// print error output if necessary
		if (err != VIX_OK)
			printf(Vix_GetErrorText(err, NULL));
	}
}

void CVMFox::stopVM()
{
	if (aquireVMHandle())
	{
		// power off the vmja ab
		VixHandle jobHandle = VixVM_PowerOff(	m_VMHandle,
												VIX_VMPOWEROP_NORMAL,
												NULL,
												NULL);

		// wait for the job to finish
		VixError err = VixJob_Wait(	jobHandle,
									VIX_PROPERTY_NONE);

		// print error output if necessary
		if (err != VIX_OK)
			printf(Vix_GetErrorText(err, NULL));
	}
}

void CVMFox::suspendVM()
{
	if (aquireVMHandle())
	{
		// suspend the vm
		VixHandle jobHandle = VixVM_Suspend(	m_VMHandle,
												VIX_VMPOWEROP_NORMAL,
												NULL,
												NULL);

		// wait for the job to finish
		VixError err = VixJob_Wait(	jobHandle,
									VIX_PROPERTY_NONE);

		// print error output if necessary
		if (err != VIX_OK)
			printf(Vix_GetErrorText(err, NULL));
	}
}

void CVMFox::resetVM()
{
	if (aquireVMHandle())
	{
		// reset the vm
		VixHandle jobHandle = VixVM_Reset(	m_VMHandle,
											VIX_VMPOWEROP_NORMAL,
											NULL,
											NULL);

		// wait for the job to finish
		VixError err = VixJob_Wait(	jobHandle,
									VIX_PROPERTY_NONE);

		// print error output if necessary
		if (err != VIX_OK)
			printf(Vix_GetErrorText(err, NULL));
	}
}

bool CVMFox::connect()
{
	// only connect if we aren't alredy connected
	if (m_HostHandle == VIX_INVALID_HANDLE)
	{
		// connect to specified host/localhost
		VixHandle jobHandle = VixHost_Connect(	REAL_VIX_SDK_VERSION,
												VIX_SERVICEPROVIDER_VMWARE_SERVER,
												m_pHostname,		// hostName
												m_iHostport,		// hostPort
												m_pUsername,		// userName
												m_pPassword,		// password,
												0,					// options
												VIX_INVALID_HANDLE, // propertyListHandle
												NULL,				// callbackProc
												NULL);				// clientData

		// wait for the job to finish
		VixError err = VixJob_Wait(	jobHandle,
									VIX_PROPERTY_JOB_RESULT_HANDLE,
									&m_HostHandle,
									VIX_PROPERTY_NONE);

		// not needed anymore
		Vix_ReleaseHandle(jobHandle);

		if (err != VIX_OK)
		{
			// print the error
			printf(Vix_GetErrorText(err, NULL));
			return false;
		}
	}

	return true;
}

bool CVMFox::aquireVMHandle()
{
	if (connect())
	{
		if (m_VMHandle == VIX_INVALID_HANDLE)
		{
			// open the vm image
			VixHandle jobHandle = VixVM_Open(	m_HostHandle, 
												m_pVMPath, 
												NULL, 
												NULL);

			// the handle to the vm image
			VixError err = VixJob_Wait(	jobHandle,
										VIX_PROPERTY_JOB_RESULT_HANDLE,
										&m_VMHandle,
										VIX_PROPERTY_NONE);

			// not needed anymore
			Vix_ReleaseHandle(jobHandle);

			if (err != VIX_OK)
			{
				printf(Vix_GetErrorText(err, NULL));
				return false;
			}
		}
		return true;
	}
	return false;
}

void CVMFox::disconnect()
{
	if (m_HostHandle != VIX_INVALID_HANDLE)
	{
		// release the vm handle
		Vix_ReleaseHandle(m_VMHandle);

		// disconnect
		VixHost_Disconnect(m_HostHandle);
		m_HostHandle = VIX_INVALID_HANDLE;
	}
}

void CVMFox::getPowerState()
{
	if (aquireVMHandle())
	{
		// get the powerstate properties
		int iPowerState = 0;
		VixError err = Vix_GetProperties(	m_VMHandle,
											VIX_PROPERTY_VM_POWER_STATE,
											&iPowerState,
											VIX_PROPERTY_NONE);

		if (err != VIX_OK)
			printf(Vix_GetErrorText(err, NULL));
		else
		{
			// print the powerstate
			printf("%d\n", iPowerState);
		}
	}
}

void CVMFox::getToolsState()
{
	if (aquireVMHandle())
	{
		// get the toolstate properties
		int iToolsState = 0;
		VixError err = Vix_GetProperties(	m_VMHandle,
											VIX_PROPERTY_VM_TOOLS_STATE,
											&iToolsState,
											VIX_PROPERTY_NONE);

		if (err != VIX_OK)
			printf(Vix_GetErrorText(err, NULL));
		else
		{
			// print the toolstate
			printf("%d\n", iToolsState);
		}
	}
}
