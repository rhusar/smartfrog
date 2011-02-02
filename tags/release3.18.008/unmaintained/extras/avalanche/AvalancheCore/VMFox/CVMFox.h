#ifndef _CVMFOX_H_
#define _CVMFOX_H_

#define REAL_VIX_SDK_VERSION 1

#include <vix.h>
#include <stdio.h>

class CVMFox
{
private:
	const char		*m_pHostname,	// hostname
					*m_pUsername,	// username
					*m_pPassword,	// password
					*m_pVMPath;		// path to the vm on the host
	int				m_iHostport;	// port on the hostmachine

	VixHandle		m_HostHandle,	// handle to the host
					m_VMHandle;		// handle to the vm

	bool connect();
	bool aquireVMHandle();			// gets the handle to the vm
	void disconnect();

public:
	// default constructor
	CVMFox() 
		:	m_pHostname(NULL),
			m_pUsername(NULL),
			m_pPassword(NULL),
			m_pVMPath(""),
			m_iHostport(0),
			m_HostHandle(VIX_INVALID_HANDLE),
			m_VMHandle(VIX_INVALID_HANDLE) { };

	// destructor
	~CVMFox();

	// setters
	void setHostname(const char* inHostname) { m_pHostname = inHostname; }
	void setUsername(const char* inUsername) { m_pUsername = inUsername; }
	void setPassword(const char* inPassword) { m_pPassword = inPassword; }
	void setVMPath(const char* inVMPath) { m_pVMPath = inVMPath; }
	void setHostPort(int inPort) { m_iHostport = inPort; }

	void startVM();			
	void stopVM();
	void suspendVM();
	void resetVM();
	void listRunningVMs();
	void registerVM();
	void unregisterVM();
	void getPowerState();
	void getToolsState();
};

#endif
