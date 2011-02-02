#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN
#include "com_hp_ail_smartfrog_collateOutput.h"

static char *dump_fileprefix;  
static char dump_filename[1024];
static COutput_ptr top=NULL;
static COutput_ptr cur=NULL;
static FILE *dump_file=NULL;
static int dump_type=0x0;
static int idx=0;

void collateSetPrefix(char* fileprefix){
  /*fprintf(stdout, "collateSetPrefix"); fflush(stdout);
  fprintf(stdout, fileprefix); fflush(stdout);
  fprintf(stdout, "\n"); fflush(stdout);
  */
  dump_fileprefix=fileprefix;
}

void collateFreshRecord(){
  //fprintf(stdout, "collateFreshRecord"); fflush(stdout);

  if (top==NULL) {
    cur = top = (COutput_ptr) malloc(sizeof(COutput));
  } else {
    cur->next = (COutput_ptr) malloc(sizeof(COutput));
    idx++; 
    cur = cur->next;
  }
  cur->prop[0]= '\0';
  cur->in[0]= '\0';
  cur->next=NULL;
}

void collateCloseCurrentRecord(){
  //fprintf(stdout, "collateCollateCurrentRecord"); fflush(stdout);

  if (dump_file!=NULL){
    fclose(dump_file);
    dump_file=NULL;
  }
}

void collateSetDumpType(int _dump_type){
  //fprintf(stdout, "collateSetDumpType"); fflush(stdout);

  dump_type = _dump_type;
}

void collateAddToDump(char *dump_str){
  //fprintf(stdout, "collateAddToDump"); fflush(stdout);

  switch (dump_type){
  case COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_DUMP_PROP: strcat(cur->prop, dump_str); break;
  case COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_DUMP_IN: strcat(cur->in, dump_str); break;
  case COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_DUMP_FAILREC: 
  if (dump_file==NULL){
    sprintf(dump_filename, "%s%d", dump_fileprefix, idx); 
    dump_file = fopen(dump_filename, "w");
  }
  fprintf(dump_file, dump_str);
  break;
  }
}

void collateSetResult(int result){
  //fprintf(stdout, "collateSetResult"); fflush(stdout);

  cur->result = result;
}

COutput_ptr collateGetTop(){
  //fprintf(stdout, "collateGetTop"); fflush(stdout);

  return top;
}



