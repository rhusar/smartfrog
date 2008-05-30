#include <stdlib.h>
#include "NuSMVInterface.h"

JNIEXPORT void JNICALL Java_org_smartfrog_services_dependencies_experimental_modelcheck_NuSMVInterface_run(JNIEnv *env, jclass cl, jstring _cmd){
	const char* cmd;
	
	cmd = (const char*) (*env)->GetStringUTFChars(env, _cmd, NULL);
	system(cmd);
}

