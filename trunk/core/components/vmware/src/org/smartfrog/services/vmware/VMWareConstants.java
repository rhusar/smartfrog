package org.smartfrog.services.vmware;

/**
 * VMWare messaging protocol constants.
 */
public abstract class VMWareConstants {
	public static final String VMRESPONSE = "vmresponse";
	public static final String VMNAME = "vmname";
	public static final String VMCMD = "vmcmd";
	public static final String VM_CMD_START = "start";
	public static final String VM_CMD_STOP = "stop";
	public static final String VM_CMD_SUSPEND = "suspend";
	public static final String VM_CMD_RESET = "reset";
	public static final String VM_CMD_LIST = "list";
	public static final String VM_CMD_POWERSTATE = "powerstate";
	public static final String VM_CMD_TOOLSSTATE = "toolsstate";
	public static final String VM_CMD_CREATE = "create";
	public static final String VM_CREATE_NAME = "create_name";
	public static final String VM_CREATE_MASTER = "create_master";
	public static final String VM_CREATE_USER = "create_user";
	public static final String VM_CREATE_PASS = "create_pass";
	public static final String VM_CMD_DELETE = "delete";
	public static final String VM_CMD_GETMASTERS = "getmasters";
	public static final String VM_CMD_RENAME = "rename";
	public static final String VM_RENAME_NAME = "rename_name";
	public static final String VM_RENAME_OLD_NAME = "rename_old_name";
	public static final String VM_CMD_GETATTRIBUTE = "getattribute";
	public static final String VM_GETATTRIBUTE_KEY = "getattrib_key";
	public static final String VM_CMD_SETATTRIBUTE = "setattribute";
	public static final String VM_SETATTRIBUTE_KEY = "setattrib_key";
	public static final String VM_SETATTRIBUTE_VALUE = "setattrib_value";
	public static final String VM_CMD_EXECUTE = "executeinguest";
	public static final String VM_EXECUTE_CMD = "exec_cmd";
	public static final String VM_EXECUTE_PARAM = "exec_param";
	public static final String VM_EXECUTE_NOWAIT = "exec_nowait";
	public static final String VM_CMD_WAIT_FOR_TOOLS = "waitfortools";
	public static final String VM_CMD_TAKE_SNAPSHOT = "takesnapshot";
	public static final String VM_WAIT_FOR_TOOLS_TIMEOUT = "wait_timeout";
	public static final String VM_TAKE_SNAPSHOT_NAME = "tsnap_name";
	public static final String VM_TAKE_SNAPSHOT_DESCRIPTION = "tsnap_desc";
	public static final String VM_TAKE_SNAPSHOT_INCLUDE_MEMORY = "tsnap_incmem";
	public static final String VM_CMD_REVERT = "reverttosnapshot";
	public static final String VM_REVERT_NAME = "rsnap_name";
	public static final String VM_CMD_SET_GUEST_CRED = "setguestcred";
	public static final String VM_SET_GUEST_CRED_USER = "setgcred_user";
	public static final String VM_SET_GUEST_CRED_PASS = "setgcred_pass";
	public static final String VM_CMD_DELETE_SNAPSHOT = "deletesnapshot";
	public static final String VM_DELETE_SNAPSHOT_DEL_CHILD = "dsnap_delchild";
	public static final String VM_DELETE_SNAPSHOT_NAME = "dsnap_name";
	public static final String VM_CMD_COPY_HOST_TO_GUEST = "copyhosttoguest";
	public static final String VM_COPY_HTOG_SOURCE = "copyhtog_source";
	public static final String VM_COPY_HTOG_DEST = "copyhtog_dest";
	public static final String VM_LIST_COUNT = "list_count";
}
