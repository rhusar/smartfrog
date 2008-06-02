/**CFile***********************************************************************

  FileName    [traceCmd.c]

  PackageName [trace]

  Synopsis    [Trace Commands]

  Description [This file contains commands related to traces.]

  SeeAlso     []

  Author      [Ashutosh Trivedi]

  Copyright   [
  This file is part of the ``trace'' package of NuSMV version 2. 
  Copyright (C) 2003 by ITC-irst.

  NuSMV version 2 is free software; you can redistribute it and/or 
  modify it under the terms of the GNU Lesser General Public 
  License as published by the Free Software Foundation; either 
  version 2 of the License, or (at your option) any later version.

  NuSMV version 2 is distributed in the hope that it will be useful, 
  but WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public 
  License along with this library; if not, write to the Free Software 
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

  For more information on NuSMV see <http://nusmv.irst.itc.it>
  or email to <nusmv-users@irst.itc.it>.
  Please report bugs to <nusmv-users@irst.itc.it>.

  To contact the NuSMV development board, email to <nusmv@irst.itc.it>. ]

******************************************************************************/

#if HAVE_CONFIG_H
# include "config.h"
#endif

#include "TraceManager.h"
#include "utils/error.h" /* for CATCH */
#include "utils/utils_io.h"
#include "parser/symbols.h"
#include "utils/error.h"

#include "TraceManager.h"
#include "Trace.h"
#include "pkg_traceInt.h"
#include "pkg_trace.h"
#include "enc/enc.h"
#include "cmd/cmd.h"
#include "utils/ucmd.h"

static char rcsid[] UTIL_UNUSED = "$Id: traceCmd.c,v 1.1.2.26.4.6.4.3 2006/11/16 14:54:29 nusmv Exp $";

/*---------------------------------------------------------------------------*/
/* Static function prototypes                                                */
/*---------------------------------------------------------------------------*/
static int UsageShowTraces ARGS((void));
static int UsageShowPlugins ARGS((void));
static int UsageReadTrace ARGS((void)); 

/*---------------------------------------------------------------------------*/
/* Definition of exported functions                                          */
/*---------------------------------------------------------------------------*/
void traceCmd_init(void)
{
  Cmd_CommandAdd("show_traces", CommandShowTraces, 0, true);
  Cmd_CommandAdd("show_plugins", CommandShowPlugins, 0, true);
  Cmd_CommandAdd("read_trace", CommandReadTrace, 0, true);
}

/**Function********************************************************************

  Synopsis           [Shows the traces generated in a NuSMV session]

  SeeAlso            [pick_state goto_state simulate]

  CommandName        [show_traces]

  CommandSynopsis    [Shows the traces generated in a NuSMV session]

  CommandArguments   [\[ \[-h\] \[-v\] \[-m | -o output-file\]
  -t | -a | trace_number \]]

  CommandDescription [ Shows the traces currently stored in system memory, if
  any. By default it shows the last generated trace, if any.  
  <p> Command Options:<p>
  <dl>
    <dt> <tt>-v</tt>
       <dd> Verbosely prints traces content (all state variables, otherwise
       it prints out only those variables that have changed their value from
       previous state).
    <dt> <tt>-t</tt>
       <dd> Prints only the total number of currently stored traces.
    <dt> <tt>-a</tt>
       <dd> Prints all the currently stored traces.
    <dt> <tt>-p trace plugin</tt>
       <dd> Uses the specified trace plugin to explain the trace.
    <dt> <tt>-m</tt>
       <dd> Pipes the output through the program specified
       by the <tt>PAGER</tt> shell variable if defined, else through the
       <tt>UNIX</tt> command "more".
    <dt> <tt>-o output-file</tt>
       <dd> Writes the output generated by the command to <tt>output-file</tt>
    <dt> <tt>trace_number</tt>
       <dd> The (ordinal) identifier number of the trace to be printed.
  </dl> ]

  SideEffects        []

******************************************************************************/
int CommandShowTraces(int argc, char** argv)
{
  int old_plugin;
  int c = 0;
  boolean all = false;
  boolean number = false;
  short int useMore = 0;
  char* dbgFileName = NIL(char);
  char* err_occ[2];
  char* steps_no_str;
  int traceno = TraceManager_get_size(global_trace_manager);
  int trace = traceno;
  int show_plugin = TraceManager_get_default_plugin(global_trace_manager);
  FILE* old_nusmv_stdout = NIL(FILE);

  util_getopt_reset();
  while ((c = util_getopt(argc, argv, "hvatmp:o:")) != EOF) {
    switch (c) {
    case 'h': return UsageShowTraces();
    
    case 'v':
      show_plugin = 1;
      break;
    
    case 'a':
      all = true;
      break;
    
    case 't':
      number = true;
      break;
    
    case 'p':
      {
        char* err_occ[1];

        show_plugin = strtol((util_strsav(util_optarg)), err_occ, 10);
        if (strncmp(err_occ[0], "", 1) != 0) { 
          fprintf(nusmv_stderr, 
                  "Error: \"%s\" is not a valid trace plugin value " \
                  "for \"show_traces -p \" command line option.\n",
                  err_occ[0]);

          return UsageShowPlugins();
        }
      }
      break;
    
    case 'o':
      if (useMore == 1) return UsageShowTraces();
      dbgFileName = util_strsav(util_optarg);
      break;
    
    case 'm':
      if (dbgFileName != NIL(char)) return UsageShowTraces();
      useMore = 1;
      break;
    
    default: return UsageShowTraces();
    } /* Switch */
  } /* While */

  if (traceno == 0) {
    fprintf(nusmv_stderr, "There are no traces currently available.\n");
    return 0;
  }

  if ((util_optind == 0) && (argc > 2)) return UsageShowTraces();

  /* Parsing of the trace number to be printed */
  if (all == false) {
    if (argc != util_optind) {
      err_occ[0] = "";
      steps_no_str = util_strsav(argv[util_optind]);
      trace = strtol(steps_no_str, err_occ, 10);

      if  ((strncmp(err_occ[0], "", 1) != 0)) {
        fprintf(nusmv_stderr,
                "Error: \"%s\" is not a valid value (must be a positive" \
                "integer).\n",
                err_occ[0]);
        return 1;
      }
      if ( (trace > traceno) || (trace == 0) ) {
        fprintf(nusmv_stderr,
                "Error: \"%d\" is not a valid trace number. Acceptable range is" \
                " 1..%d.\n", trace, traceno);
        return 1;
      }
    }
  }
  else if (argc != util_optind) return UsageShowTraces();

  old_plugin = TraceManager_get_default_plugin(global_trace_manager);
  
  if (show_plugin != old_plugin) {
    if (TracePkg_set_default_trace_plugin(show_plugin)) return 1;
  }


  if (useMore) {
    old_nusmv_stdout = nusmv_stdout;
    nusmv_stdout = CmdOpenPipe(useMore);
    if (nusmv_stdout==(FILE*) NULL) {nusmv_stdout=old_nusmv_stdout; return 1;}
  }  
  if (dbgFileName != NIL(char)) {
    old_nusmv_stdout = nusmv_stdout;
    nusmv_stdout = CmdOpenFile(dbgFileName);
    if (nusmv_stdout==(FILE*) NULL) {nusmv_stdout = old_nusmv_stdout; return 1;}
  }

  if (number == true) {
    fprintf(nusmv_stdout, (traceno == 1) ? 
            "There is %d trace currently available.\n" :
            "There are %d traces currently available.\n", traceno);
  }
  else {
    /* A trace header will not printed when the plugin is the XML
       dumper or dynamically registered external plugins: */ 
    boolean print_header = 
      (TraceManager_get_default_plugin(global_trace_manager) != 4) && 
      TraceManager_is_plugin_internal(global_trace_manager, 
		      TraceManager_get_default_plugin(global_trace_manager));

    set_indent_size(2);
    
    if (all == false) {
      if (print_header) {
        fprintf(nusmv_stdout,
                "<!-- ################### Trace number: %d #################" 
                "## -->\n", 
                trace); 
      }

      TraceManager_execute_plugin(global_trace_manager, 
		  TraceManager_get_default_plugin(global_trace_manager), 
		  trace-1);
    }
    else {
      int c;
      for (c=0; c<traceno; c++){
        if (print_header) {
          fprintf(nusmv_stdout, 
                  "<!-- ################### Trace number: %d #################"\
                  "## -->\n", c+1); 
        }
        TraceManager_execute_plugin(global_trace_manager, 
                     TraceManager_get_default_plugin(global_trace_manager), c);
      } 
    }
    reset_indent_size();

    TracePkg_set_default_trace_plugin(old_plugin);
  }

  if (useMore) {
    CmdClosePipe(nusmv_stdout);
    nusmv_stdout = old_nusmv_stdout;
  }
  if (dbgFileName != NIL(char)) {
    CmdCloseFile(nusmv_stdout);
    nusmv_stdout = old_nusmv_stdout;
  }

  return 0;
}

/**Function********************************************************************

  Synopsis    [UsageShowTraces]

  Description []

  SideEffects []

  SeeAlso     []

******************************************************************************/
static int UsageShowTraces(void) 
{
  fprintf(nusmv_stderr, 
          "usage: show_traces [-h] [-v] [-t] [-m | -o file] " \
          "[-a | trace_number] [-p plugin]\n");

  fprintf(nusmv_stderr, "  -h \t\tPrints the command usage.\n");
  fprintf(nusmv_stderr, 
          "  -v \t\tVerbosely prints traces content (unchanged vars also).\n");

  fprintf(nusmv_stderr, "  -a \t\tPrints all the currently stored traces.\n");
  fprintf(nusmv_stderr, 
          "  -t \t\tPrints only the total number of currently stored traces.\n");
  
  fprintf(nusmv_stderr, 
          "  -m \t\tPipes output through the program specified by the \"PAGER\"\n");
  
  fprintf(nusmv_stderr, 
          "     \t\tenvironment variable if defined, else through " \
          "the UNIX command \"more\".\n");
  
  fprintf(nusmv_stderr, 
          "  -p plugin\tUses the specified trace plugin to explain the trace.\n");
  
  fprintf(nusmv_stderr, 
          "  -o file\tWrites the generated output to \"file\".\n");
  
  fprintf(nusmv_stderr, 
          "  trace_number\tThe number of the trace to be printed.\n");
  
  return 1;
}


/**Function********************************************************************

  Synopsis    [Called when the user selects a trace plugin to be used as 
  default]

  Description [Returns true if an error occurred]

  SideEffects []

  SeeAlso     []

******************************************************************************/
boolean TracePkg_set_default_trace_plugin(int dp)
{
  int avail_plugins = 
    TraceManager_get_plugin_size(TracePkg_get_global_trace_manager());
  int internal_plugins =
    TraceManager_get_internal_plugin_size(TracePkg_get_global_trace_manager());

  if (dp < 0) {
    fprintf(nusmv_stderr,"Error: Not a proper plugin to show a trace \n");
    return true;
  }

#if HAVE_LIBEXPAT
  /* XML Loader is not a proper plugin */
  if (dp == internal_plugins-1) {
    fprintf(nusmv_stderr,"Error: Not a proper plugin to show a trace \n");
    return true;
  }
#endif

  if (dp < avail_plugins)
    {
      TraceManager_set_default_plugin(TracePkg_get_global_trace_manager(), dp);
    }
  else {
    fprintf(nusmv_stderr,"Error: Plugin %d is not currently available\n", dp);
    return true;
  }
  
  return false;
}

/**Function********************************************************************

  Synopsis    [Returns the trace plugin currently selected as default]

  Description [Returns the trace plugin currently selected as default]

  SideEffects []

  SeeAlso     []

******************************************************************************/
int TracePkg_get_default_trace_plugin()
{
  return TraceManager_get_default_plugin(TracePkg_get_global_trace_manager());
}


/**Function********************************************************************

  Synopsis           [Lists out all the available plugins inside the system.]

  SeeAlso            []

  CommandName        [show_plugins]

  CommandSynopsis    [Lists out all the available plugins inside the system. In
  addition, it prints \[D\] in front of the default plugin.]

  CommandArguments   [\[ \[-h\] \[-n plugin_index| -a\]]

  CommandDescription [
  Sets the default plugin to print traces. 
  <p> Command Options:<p>
  <dl>
    <dt> <tt>-h</tt>
       <dd> Prints the usage of the command.
    <dt> <tt>-n plugin_index</tt>
       <dd> Prints the description message of the plugin at specified index
       only.
    <dt> <tt>-a</tt>
       <dd> Prints all the available plugins with their description.
  </dl> ]

  SideEffects        []

******************************************************************************/
int CommandShowPlugins(int argc, char** argv)
{
  int c;
  boolean showAll = false;
  int dp = -1;
  
  util_getopt_reset();
  while ((c = util_getopt(argc, argv, "hn:a")) != EOF) {
    switch (c) {
    case 'h': return UsageShowPlugins();
      break;
    
    case 'n':
      {
        char *err_occ[1];

        if (showAll) return UsageShowPlugins();
        dp = strtol((util_strsav(util_optarg)), err_occ, 10);
    
        if (strncmp(err_occ[0], "", 1) != 0) { 
          fprintf(nusmv_stderr, 
                  "Error: \"%s\" is not a valid value for" \
                  "\"-show_plugins\" command line option.\n", 
                  err_occ[0]);

          return UsageShowPlugins();
        }
      }
      break;
    
    case 'a':
      if (dp >= 0) return UsageShowPlugins();
      showAll = true;
      break;
    
    default: return UsageShowPlugins();
    }
  }

  if (dp < 0) showAll = true; 
  if (showAll) {
    int i;


    if (TraceManager_get_plugin_size(global_trace_manager) <= 0) {
      fprintf(nusmv_stderr, "There are no registered plugins to be shown\n");
      return 0;
    }

    for (i = 0; i < TraceManager_get_plugin_size(global_trace_manager); i++){
      TracePlugin_ptr p_i = TraceManager_get_plugin_at_index(global_trace_manager, i);

      if (i == TraceManager_get_default_plugin(global_trace_manager)) {
	fprintf(nusmv_stdout, "[D]  %d\t %s\n", i, TracePlugin_get_desc(p_i));
      }
      else {
	fprintf(nusmv_stdout, "     %d\t %s\n", i, TracePlugin_get_desc(p_i));
      }
    }
  }
  else {
    TracePlugin_ptr p_i;
    
    if (dp < TraceManager_get_plugin_size(global_trace_manager)) {
      p_i = TraceManager_get_plugin_at_index(global_trace_manager, dp);
      
      if (dp == TraceManager_get_default_plugin(global_trace_manager)) {
        fprintf(nusmv_stdout, "[D]  %d\t %s\n", dp, TracePlugin_get_desc(p_i));
      }
      else {
        fprintf(nusmv_stdout, "     %d\t %s\n", dp, TracePlugin_get_desc(p_i)); 
      }
    }
    else fprintf(nusmv_stderr, "Error: Plugin %d is not yet registered\n", dp); 
  }

  return 0;
}

/**Function********************************************************************

  Synopsis    [UsageShowPlugins]

  Description []

  SideEffects []

  SeeAlso     []

******************************************************************************/
static int UsageShowPlugins(void) 
{
  fprintf(nusmv_stderr, "usage: show_plugins [-h]  [-n  plugin_index | -a]\n");
  fprintf(nusmv_stderr, "  -h                Prints the command usage.\n");
  fprintf(nusmv_stderr, "  -a                Shows all registered plugins.\n");
  fprintf(nusmv_stderr, 
          "  -n plugin_index   Shows only the description of the specified " \
          "plugin_index.\n");

  return 1;
}

/**Function********************************************************************

  Synopsis           [read_trace]

  SeeAlso            [show_traces]

  CommandName        [read_trace]

  CommandSynopsis    [Reads the trace from the specified file into the memory]

  CommandArguments   [\[ \[-h\] \[-n plugin_index\]]

  CommandDescription [
  Reads the trace from the specified file into the memory. 
  <p>
  Command Options:<p>
  <dl>
    <dt> <tt>-h</tt>
       <dd> Prints the usage of the command.
    <dt> <tt>-i filename</tt>
       <dd> reads the trace from the specified file.  
    </dl> ]

  SideEffects        []

******************************************************************************/
int CommandReadTrace(int argc, char** argv)
{
  int c;
  char* filename = NIL(char);

  if (argc != 3) { return UsageReadTrace(); }

  util_getopt_reset();
  /*  while ((c = util_getopt(argc, argv, "hi:")) != EOF) { */
  c = util_getopt(argc, argv, "hi:");

  switch (c) {
  case 'h': return UsageReadTrace();
    
  case 'i':
    if (util_is_string_null(util_optarg)) filename = NIL(char); 
    else filename = util_strsav(util_optarg);
    break;
    
  default: 
    return UsageReadTrace();
  }

  if (get_input_file(options) == (char *) NULL) {
    fprintf(nusmv_stderr, 
	    "Input file is (null). You must set the input file before.\n");
    return 1;
  }

  /* pre-conditions */
  if (Compile_check_if_model_was_built(nusmv_stderr, true)) return 1;


#if HAVE_LIBEXPAT
  /* The plugin is available */
  if (filename != (char *) NULL) {
    int res;
    TracePlugin_ptr plugin;
    Trace_ptr trace = Trace_create(Enc_get_bdd_encoding(), 
				   "XML_trace", TRACE_TYPE_CNTEXAMPLE, 
				   BDD_STATES(NULL));
    
    FILE* fp = fopen(filename, "r");

    if (fp != NULL) {
      plugin = TraceManager_get_plugin_at_index(global_trace_manager, 
          TraceManager_get_internal_plugin_size(
	       TracePkg_get_global_trace_manager())-1);
      
      /* layer_names parameter is ignored in loader plugin, so NULL is
	 passed here */
      res = TracePlugin_action(plugin, trace, (const array_t*) NULL, fp);

      if (res) {
        Trace_destroy(trace);
        fprintf(nusmv_stderr, 
                " XML File \"%s\" does not represent a valid XML trace.\n", 
                filename);
      }
      else {
        fprintf(nusmv_stdout, "Trace is stored at %d index \n",
                TraceManager_register_trace(global_trace_manager, trace) + 1);
      }

      fclose(fp);
    } 
    else {
      fprintf(nusmv_stderr, "XML File \"%s\" does not exists\n",
	      filename);
    }
  }

  return 0;

#else /* HAVE_LIBEXPAT */
  /* The plugin is not available */
  fprintf(nusmv_stderr, 
	  "Sorry, this feature is not available under this system, \n" 
	  "since the loading plugin is not embedded in NuSMV.\n");
  return 1;
#endif
}

/**Function********************************************************************

  Synopsis    [UsageReadTrace]

  Description []

  SideEffects []

  SeeAlso     []

******************************************************************************/
static int UsageReadTrace(void) 
{
  fprintf(nusmv_stderr, "usage: read_trace [-h | -i file-name]\n");
  fprintf(nusmv_stderr, "  -h                Prints the command usage.\n");
  fprintf(nusmv_stderr, "  -i file-name      Reads an XML trace from given file.\n");

  return 1;
}

