#include <string.h>
#include <stdlib.h>
#include "CVMFox.h"

void printUsage()
{
	printf("Usage: VMFox.exe [optional parameters] [path to vmx file] [command]\n\n");
	printf("Optional parameters:\n-h [hostname]\n-P [hostport]\n-u [username]\n-p [password]\n\n");
	printf("Commands:\n");
	printf("start			Starts the virtual machine.\n");
	printf("stop			Stops the virtual machine.\n");
	printf("suspend			Suspends the virtual machine.\n");
	printf("reset			Stops the virtual machine.\n");
	printf("register		Registers the virtual machine.\n");
	printf("unregister		Unregisters the virtual machine.\n");
	printf("powerstate		Retrieves the powerstate of a virtual machine.\n");
	printf("toolsstate		Gets information about the vm tools on the virtual machine.\n");
	printf("list			List running virtual machines. (No [path to vmx file] needed.)\n");
}

int main(int argc, char * argv[])
{
	if ( argc == 1 )
	{
		// no parameters given
		printUsage();
	}
	else
	{
		// create the vmfox object
		CVMFox *vmFox = new CVMFox();

		// parse the commandline arguments, optional parameters come first
		int		iArgs		= argc - 2;
		bool	bDone		= false;
		for (int i = 1; i <= iArgs; ++i)
		{
			if (!bDone)
			{
				if (!strcmp(argv[i], "-h"))
				{
					// the next argument should be the hostname
					if ( iArgs >= (++i) )
						vmFox->setHostname(argv[i]);
				}
				else if (!strcmp(argv[i], "-P"))
				{
					// the next argument should be the hostport
					if ( iArgs >= (++i) )
						vmFox->setHostPort(atoi(argv[i]));
				}
				else if (!strcmp(argv[i], "-u"))
				{
					// the next argument should be the username
					if ( iArgs >= (++i) )
						vmFox->setUsername(argv[i]);
				}
				else if (!strcmp(argv[i], "-p"))
				{
					// the next argument should be the password
					if ( iArgs >= (++i) )
						vmFox->setPassword(argv[i]);
				}
				else
				{
					// take the first non-optional parameter to be the path to the .vmx file
					vmFox->setVMPath(argv[i]);

					// no optional parameters allowed afterwards
					bDone = true;
				}
			}
			else
			{
				// invalid arguments
				printUsage();

				// free memory
				delete vmFox;
				vmFox = NULL;

				return 1;
			}
		}

		// get the command
		if (!strcmp(argv[argc - 1], "list"))
			vmFox->listRunningVMs();
		else if (!strcmp(argv[argc - 1], "start"))
			vmFox->startVM();
		else if (!strcmp(argv[argc - 1], "stop"))
			vmFox->stopVM();
		else if (!strcmp(argv[argc - 1], "suspend"))
			vmFox->suspendVM();
		else if (!strcmp(argv[argc - 1], "register"))
			vmFox->registerVM();
		else if (!strcmp(argv[argc - 1], "unregister"))
			vmFox->unregisterVM();
		else if (!strcmp(argv[argc - 1], "reset"))
			vmFox->resetVM();
		else if (!strcmp(argv[argc - 1], "powerstate"))
			vmFox->getPowerState();
		else if (!strcmp(argv[argc - 1], "toolsstate"))
			vmFox->getToolsState();
		else printUsage();

		// delete the vmfox object
		delete vmFox;
		vmFox = NULL;
	}

	return 0;
}
