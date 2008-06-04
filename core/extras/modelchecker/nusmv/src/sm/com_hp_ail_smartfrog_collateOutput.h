/**
 *
 */

#ifndef COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN
#define COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN extern
#endif

#ifndef COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_H
#define COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_H

typedef struct collatedOutput *COutput_ptr;
typedef struct collatedOutput {
  int idx;
  char prop[256];  /*For simplicity, we restrict props to be no bigger than 256 chars*/
  char in[256];    /*Ditto component names...*/
  int result;
  COutput_ptr next;
} COutput;

#define COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_DUMP_NONE 0x0
#define COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_DUMP_PROP 0x1
#define COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_DUMP_IN 0x2
#define COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_DUMP_FAILREC 0x3

COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN void collateSetPrefix(char *file_prefix);
COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN void collateFreshRecord();
COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN void collateSetDumpType(int _dump_type);
COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN void collateAddToDump(char *dump_str);
COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN void collateSetResult(int result);
COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN void collateCloseCurrentRecord();
COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_EXTERN COutput_ptr collateGetTop();

#endif /*COM_HP_AIL_SMARTFROG_COLLATEOUTPUT_H*/
