#include "NuSMVInterface.h"
#include "src/sm/com_hp_ail_smartfrog_collateOutput.h"

extern int main_wkr(int  argc, char ** argv, char *dump_out);

JNIEXPORT void JNICALL Java_org_smartfrog_services_dependencies_modelcheck_NuSMVInterface_run
(JNIEnv *env, jclass cl, jstring input_file, jstring _file_prefix, jstring _dump_out, jobject mcrs){

        /*Set the file prefix and get/set the input/output files*/

	char *file_prefix = (char*) (*env)->GetStringUTFChars(env, _file_prefix, NULL);
        collateSetPrefix(file_prefix);

	const char* cmd;
        char* args[2];
        	
	args[0] = "NuSMV";
	args[1] = (char*) (*env)->GetStringUTFChars(env, input_file, NULL);
	
	char *dump_out = (char*) (*env)->GetStringUTFChars(env, _dump_out, NULL);

        main_wkr(2, args, dump_out); 

        /*Process the output*/  
        COutput_ptr col_out = collateGetTop();
	jclass class_mcrs = (*env)->GetObjectClass(env, mcrs);
        
        jmethodID add_result = (*env)->GetMethodID(env, class_mcrs, "addResult", "(Ljava/lang/String;Ljava/lang/String;Z)V");
        jstring prop;
        jstring in;

	while (col_out!=NULL){
          prop = (*env)->NewStringUTF(env, col_out->prop);
          in = (*env)->NewStringUTF(env, col_out->in);
          (*env)->CallVoidMethod(env, mcrs, add_result, prop, in, col_out->result);
	  COutput_ptr toFree = col_out;
          col_out=col_out->next;
	  
	  /*Free record*/
          free(toFree);
	  }    

       /*Free up strings*/
	(*env)->ReleaseStringUTFChars(env, _file_prefix, file_prefix);       
	(*env)->ReleaseStringUTFChars(env, input_file, args[1]);       
	(*env)->ReleaseStringUTFChars(env, _dump_out, dump_out);       
}

