package org.smartfrog.services.vmware;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * VMWare VIX API library wrapper. For remarks or more detailed information please see http://pubs.vmware.com/vix-api/ReferenceGuide/.
 */
public interface VMWareVixLibrary extends Library {
    public static interface VixEventProc extends Callback {
        /**
         * Procedures of this type are called when an event happens on a handle.
         *
         * @param handle
         * @param eventType
         * @param moreEventInfo
         * @param clientData
         */
        void callback(int handle,
                      int eventType,
                      int moreEventInfo,
                      Pointer clientData);
    }

    // --------------------------
    // enums and constants

    /**
     * The API version. Is set to 3 in the original header but that's wrong, the VIX API is still at version 1.
     */
    public static final int     VIX_API_VERSION = 1;

    public static class VixHandle {
        public static final int VIX_INVALID_HANDLE = 0;
    }

    /**
     * These are the types of handles used as values for VixHandle variables. Handle types are returned by the Vix_GetHandleType() function. Handles are used to manage the entities they represent (such as virtual machines) and to retrieve properties representing information about the entities.
     */
    public static class VixHandleType {
                                /**
                                 * Indicates that no value has been assigned to this handle. Recommended for initializing handle variables.
                                 */
        public static final int VIX_HANDLETYPE_NONE = 0,
                                /**
                                 * The handle represents a host. This handle type is created when you call VixHost_Connect().
                                 */
                                VIX_HANDLETYPE_HOST = 2,
                                /**
                                 * The handle represents a virtual machine. This handle type is created when you call VixVM_Open().
                                 */
                                VIX_HANDLETYPE_VM = 3,
                                VIX_HANDLETYPE_NETWORK = 5,
                                /**
                                 * The handle represents an active job. Job handles are return values from asynchronous operations, including VixHost_Connect() and VixVM_Open().
                                 */
                                VIX_HANDLETYPE_JOB = 6,
                                /**
                                 * The handle represents a snapshot. This handle type is created when you call snapshot functions such as VixVM_GetCurrentSnapshot(). Use these handles to revert or remove snapshots.
                                 */
                                VIX_HANDLETYPE_SNAPSHOT = 7,
                                VIX_HANDLETYPE_METADATA_CONTAINER = 11;
    }

    /**
     * The error codes are returned by all public VIX routines.
     */
    public static class VixError {
        public static final int VIX_OK = 0,

                                // General errors
                                /**
                                 * Unknown error.
                                 */
                                VIX_E_FAIL = 1,
                                /**
                                 * Memory allocation failed: out of memory. 
                                 */
                                VIX_E_OUT_OF_MEMORY = 2,
                                /**
                                 * One of the parameters was invalid.
                                 */
                                VIX_E_INVALID_ARG = 3,
                                /**
                                 * A file was not found.
                                 */
                                VIX_E_FILE_NOT_FOUND = 4,
                                /**
                                 * This function cannot be performed because the handle is executing another function.
                                 */
                                VIX_E_OBJECT_IS_BUSY = 5,
                                /**
                                 * The operation is not supported for the specified parameters.
                                 */
                                VIX_E_NOT_SUPPORTED = 6,
                                /**
                                 * A file access error occurred on the host or guest operating system.
                                 */
                                VIX_E_FILE_ERROR = 7,
                                /**
                                 * An error occurred while writing a file; the disk is full. Data has not been saved. Free some space and try again.
                                 */
                                VIX_E_DISK_FULL = 8,
                                /**
                                 * An error occurred while accessing a file: wrong file type.
                                 */
                                VIX_E_INCORRECT_FILE_TYPE = 9,
                                /**
                                 * The operation was cancelled.
                                 */
                                VIX_E_CANCELLED = 10,
                                /**
                                 * The file is write-protected.
                                 */
                                VIX_E_FILE_READ_ONLY = 11,
                                /**
                                 * The file already exists.
                                 */
                                VIX_E_FILE_ALREADY_EXISTS = 12,
                                /**
                                 * You do not have access rights to this file.
                                 */
                                VIX_E_FILE_ACCESS_ERROR = 13,
                                /**
                                 * The file system does not support sufficiently large files.
                                 */
                                VIX_E_REQUIRES_LARGE_FILES = 14,
                                /**
                                 * The file is already in use.
                                 */
                                VIX_E_FILE_ALREADY_LOCKED = 15,
                                /**
                                 * The command is not supported on remote objects.
                                 */
                                VIX_E_NOT_SUPPORTED_ON_REMOTE_OBJECT = 20,
                                /**
                                 * The file is too big for the filesystem.
                                 */
                                VIX_E_FILE_TOO_BIG = 21,
                                /**
                                 * The file name is not valid.
                                 */
                                VIX_E_FILE_NAME_INVALID = 22,
                                /**
                                 * Already exists.
                                 */
                                VIX_E_ALREADY_EXISTS = 23,

                                // Handle Errors
                                /**
                                 * The handle is not a valid VIX object.
                                 */
                                VIX_E_INVALID_HANDLE = 1000,
                                /**
                                 * The operation is not supported on this type of handle.
                                 */
                                VIX_E_NOT_SUPPORTED_ON_HANDLE_TYPE = 1001,
                                /**
                                 * Too many handles are open.
                                 */
                                VIX_E_TOO_MANY_HANDLES = 1002,

                                // XML errors
                                /**
                                 * Invalid file - a required section of the file is missing.
                                 */
                                VIX_E_NOT_FOUND = 2000,
                                /**
                                 * Invalid file - an object has the wrong type.
                                 */
                                VIX_E_TYPE_MISMATCH = 2001,
                                /**
                                 * Invalid file - contents may be corrupt.
                                 */
                                VIX_E_INVALID_XML = 2002,

                                // VM Control Errors
                                /**
                                 * Timeout error while waiting for the guest tools to start.
                                 */
                                VIX_E_TIMEOUT_WAITING_FOR_TOOLS = 3000,
                                /**
                                 * The command is not recognized by the virtual machine.
                                 */
                                VIX_E_UNRECOGNIZED_COMMAND = 3001,
                                /**
                                 * The requested operation is not supported on this guest operating system.
                                 */
                                VIX_E_OP_NOT_SUPPORTED_ON_GUEST = 3003,
                                /**
                                 * A program could not run on the guest operating system.
                                 */
                                VIX_E_PROGRAM_NOT_STARTED = 3004,
                                /**
                                 * Cannot power on a read-only virtual machine.
                                 */
                                VIX_E_CANNOT_START_READ_ONLY_VM = 3005,
                                /**
                                 * The virtual machine needs to be powered on.
                                 */
                                VIX_E_VM_NOT_RUNNING = 3006,
                                /**
                                 * The virtual machine should not be powered on. It appears to be running already.
                                 */
                                VIX_E_VM_IS_RUNNING = 3007,
                                /**
                                 * Cannot connect to the virtual machine.
                                 */
                                VIX_E_CANNOT_CONNECT_TO_VM = 3008,
                                /**
                                 * Cannot execute scripts.
                                 */
                                VIX_E_POWEROP_SCRIPTS_NOT_AVAILABLE = 3009,
                                /**
                                 * No operating system installed in the virtual machine.
                                 */
                                VIX_E_NO_GUEST_OS_INSTALLED = 3010,
                                /**
                                 * Not enough physical memory is available to power on this virtual machine.
                                 */
                                VIX_E_VM_INSUFFICIENT_HOST_MEMORY = 3011,
                                /**
                                 * An error occurred while suspending the virtual machine.
                                 */
                                VIX_E_SUSPEND_ERROR = 3012,
                                /**
                                 * This virtual machine is configured to run with 2 CPUs, but the host only has 1 CPU. The virtual machine cannot be powered on.
                                 */
                                VIX_E_VM_NOT_ENOUGH_CPUS = 3013,
                                /**
                                 * Insufficient permissions in host operating system.
                                 */
                                VIX_E_HOST_USER_PERMISSIONS = 3014,
                                /**
                                 * Authentication failure or insufficient permissions in guest operating system.
                                 */
                                VIX_E_GUEST_USER_PERMISSIONS = 3015,
                                /**
                                 * Guest tools is not running.
                                 */
                                VIX_E_TOOLS_NOT_RUNNING = 3016,
                                /**
                                 * Guest operations are not allowed on this virtual machine.
                                 */
                                VIX_E_GUEST_OPERATIONS_PROHIBITED = 3017,
                                /**
                                 * Guest operations are not allowed for the anonymous user on this virtual machine.
                                 */
                                VIX_E_ANON_GUEST_OPERATIONS_PROHIBITED = 3018,
                                /**
                                 * Guest operations are not allowed for administrative user on this virtual machine.
                                 */
                                VIX_E_ROOT_GUEST_OPERATIONS_PROHIBITED = 3019,
                                /**
                                 * The virtual machine configuration must specify guest account name to be used for anonymous guest operations.
                                 */
                                VIX_E_MISSING_ANON_GUEST_ACCOUNT = 3023,
                                /**
                                 * The virtual machine cannot authenticate users with guest.
                                 */
                                VIX_E_CANNOT_AUTHENTICATE_WITH_GUEST = 3024,
                                /**
                                 * The command is not recognized by the Guest OS tools.
                                 */
                                VIX_E_UNRECOGNIZED_COMMAND_IN_GUEST = 3025,
                                /**
                                 * Guest operations are not allowed for console user on this virtual machine.
                                 */
                                VIX_E_CONSOLE_GUEST_OPERATIONS_PROHIBITED = 3026,
                                /**
                                 * The command can only be run by console user.
                                 */
                                VIX_E_MUST_BE_CONSOLE_USER = 3027,

                                // VM Errors
                                /**
                                 * The virtual machine cannot be found.
                                 */
                                VIX_E_VM_NOT_FOUND = 4000,
                                /**
                                 * The operation is not supported for this virtual machine version.
                                 */
                                VIX_E_NOT_SUPPORTED_FOR_VM_VERSION = 4001,
                                /**
                                 * Cannot read the virtual machine configuration file.
                                 */
                                VIX_E_CANNOT_READ_VM_CONFIG = 4002,
                                /**
                                 * Cannot perform this operation on a template virtual machine.
                                 */
                                VIX_E_TEMPLATE_VM = 4003,
                                /**
                                 * The virtual machine has already been loaded.
                                 */
                                VIX_E_VM_ALREADY_LOADED = 4004,
                                /**
                                 * The virtual machine is already up-to-date.
                                 */
                                VIX_E_VM_ALREADY_UP_TO_DATE = 4006,

                                // Property Errors
                                /**
                                 * Unrecognized handle property identifier.
                                 */
                                VIX_E_UNRECOGNIZED_PROPERTY = 6000,
                                /**
                                 * Invalid property value.
                                 */
                                VIX_E_INVALID_PROPERTY_VALUE = 6001,
                                /**
                                 * Cannot change a read-only property.
                                 */
                                VIX_E_READ_ONLY_PROPERTY = 6002,
                                /**
                                 * This handle is missing a required property.
                                 */
                                VIX_E_MISSING_REQUIRED_PROPERTY = 6003,

                                // Completion Errors
                                /**
                                 * The index parameter does not correspond to a result set.
                                 */
                                VIX_E_BAD_VM_INDEX = 8000,

                                // Snapshot errors
                                /**
                                 * The snapshot is incompatable with the current host.
                                 */
                                VIX_E_SNAPSHOT_INVAL = 13000,
                                /**
                                 * Unable to open snapshot file.
                                 */
                                VIX_E_SNAPSHOT_DUMPER = 13001,
                                /**
                                 * Disk problem.
                                 */
                                VIX_E_SNAPSHOT_DISKLIB = 13002,
                                /**
                                 * A file by that name does not exist.
                                 */
                                VIX_E_SNAPSHOT_NOTFOUND = 13003,
                                /**
                                 * A file by that name already exists.
                                 */
                                VIX_E_SNAPSHOT_EXISTS = 13004,
                                /**
                                 * Snapshots are not allowed on this virtual machine.
                                 */
                                VIX_E_SNAPSHOT_VERSION = 13005,
                                /**
                                 * Insufficient permissions.
                                 */
                                VIX_E_SNAPSHOT_NOPERM = 13006,
                                /**
                                 * Something is wrong with the configuration file.
                                 */
                                VIX_E_SNAPSHOT_CONFIG = 13007,
                                /**
                                 * The state of the virtual machine has not changed since the last snapshot operation.
                                 */
                                VIX_E_SNAPSHOT_NOCHANGE = 13008,
                                /**
                                 * Unable to save snapshot file.
                                 */
                                VIX_E_SNAPSHOT_CHECKPOINT = 13009,
                                /**
                                 * A snapshot operation is already in progress.
                                 */
                                VIX_E_SNAPSHOT_LOCKED = 13010,
                                /**
                                 * The snapshot files are in an inconsistent state.
                                 */
                                VIX_E_SNAPSHOT_INCONSISTENT = 13011,
                                /**
                                 * The filename is too long.
                                 */
                                VIX_E_SNAPSHOT_NAMETOOLONG = 13012,
                                /**
                                 * Cannot snapshot all metadata files.
                                 */
                                VIX_E_SNAPSHOT_VIXFILE = 13013,
                                /**
                                 * One or more of the disks are busy.
                                 */
                                VIX_E_SNAPSHOT_DISKLOCKED = 13014,
                                /**
                                 * The virtual disk is used multiple times.
                                 */
                                VIX_E_SNAPSHOT_DUPLICATEDDISK = 13015,
                                /**
                                 * Powered on snapshot are not allowed on virtual machines with indpendent disks.
                                 */
                                VIX_E_SNAPSHOT_INDEPENDENTDISK = 13016,
                                /**
                                 * The name does not uniquely identify one snapshot.
                                 */
                                VIX_E_SNAPSHOT_NONUNIQUE_NAME = 13017,

                                // Guest Errors
                                /**
                                 * The object is not a file.
                                 */
                                VIX_E_NOT_A_FILE = 20001,
                                /**
                                 * The object is not a directory.
                                 */
                                VIX_E_NOT_A_DIRECTORY = 20002,
                                /**
                                 * No such process.
                                 */
                                VIX_E_NO_SUCH_PROCESS = 20003,
                                /**
                                 * File name too long.
                                 */
                                VIX_E_FILE_NAME_TOO_LONG = 20004;
    }

    /**
     * These are the possible types for Vix properties. Property types are returned by the Vix_GetPropertyType() function.
     */
    public static class VixPropertyType {
                                /**
                                 * Indicates that no property type has been assigned to this variable. Recommended for initializing property type variables.
                                 */
        public static final int VIX_PROPERTYTYPE_ANY = 0,
                                /**
                                 * The property type is 'int'.
                                 */
                                VIX_PROPERTYTYPE_INTEGER = 1,
                                /**
                                 * The property type is 'char *'.
                                 */
                                VIX_PROPERTYTYPE_STRING = 2,
                                /**
                                 * The property type is Boolean.
                                 */
                                VIX_PROPERTYTYPE_BOOL = 3,
                                /**
                                 * The property type is VixHandle.
                                 */
                                VIX_PROPERTYTYPE_HANDLE = 4,
                                /**
                                 * The property type is 'int64'.
                                 */
                                VIX_PROPERTYTYPE_INT64 = 5,
                                /**
                                 * The property type is 'char *". When returned as a job property, the blob is returned as two values: first an 'int' containing the blob size in bytes, then a pointer to the blob.
                                 */
                                VIX_PROPERTYTYPE_BLOB = 6;
    }

    /**
     * These are the possible IDs for Vix properties. To retrieve a property from a handle, pass the ID for the property you want.
     */
    public static class VixPropertyID {
                                /**
                                 * Indicates that no value has been assigned to this property variable. Recommended for initializing property variables.
                                 */
        public static final int VIX_PROPERTY_NONE = 0,

                                // Properties used by several handle types.
                                VIX_PROPERTY_META_DATA_CONTAINER = 2,

                                // VIX_HANDLETYPE_HOST properties
                                VIX_PROPERTY_HOST_HOSTTYPE = 50,
                                VIX_PROPERTY_HOST_API_VERSION = 51,

                                // VIX_HANDLETYPE_VM properties
                                /**
                                 * The number of virtual CPUs configured for the virtual machine.
                                 */
                                VIX_PROPERTY_VM_NUM_VCPUS = 101,
                                /**
                                 * The path to the virtual machine configuration file.
                                 */
                                VIX_PROPERTY_VM_VMX_PATHNAME = 103,
                                /**
                                 * The path to the virtual machine team.
                                 */
                                VIX_PROPERTY_VM_VMTEAM_PATHNAME = 105,
                                /**
                                 * The memory size of the virtual machine.
                                 */
                                VIX_PROPERTY_VM_MEMORY_SIZE = 106,
                                VIX_PROPERTY_VM_READ_ONLY = 107,
                                /**
                                 * Whether the virtual machine is a member of a team.
                                 */
                                VIX_PROPERTY_VM_IN_VMTEAM = 128,
                                /**
                                 * The power state of the virtual machine, such as VIX_POWERSTATE_POWERED_ON.
                                 */
                                VIX_PROPERTY_VM_POWER_STATE = 129,
                                /**
                                 * The state of the VMware Tools suite in the guest.
                                 */
                                VIX_PROPERTY_VM_TOOLS_STATE = 152,
                                /**
                                 * Whether the virtual machine is running.
                                 */
                                VIX_PROPERTY_VM_IS_RUNNING = 196,
                                VIX_PROPERTY_VM_SUPPORTED_FEATURES = 197,

                                // Result properties; these are returned by various procedures
                                /**
                                 * The most recent error encountered by the job.
                                 */
                                VIX_PROPERTY_JOB_RESULT_ERROR_CODE = 3000,
                                VIX_PROPERTY_JOB_RESULT_VM_IN_GROUP = 3001,
                                /**
                                 * A user message blocking the virtual machine.
                                 */
                                VIX_PROPERTY_JOB_RESULT_USER_MESSAGE = 3002,
                                VIX_PROPERTY_JOB_RESULT_EXIT_CODE = 3004,
                                VIX_PROPERTY_JOB_RESULT_COMMAND_OUTPUT = 3005,
                                /**
                                 * A handle resulting from an asynchronous operation.
                                 */
                                VIX_PROPERTY_JOB_RESULT_HANDLE = 3010,
                                VIX_PROPERTY_JOB_RESULT_GUEST_OBJECT_EXISTS = 3011,
                                /**
                                 * The time it took to execute a program with VixVM_RunProgramInGuest().
                                 */
                                VIX_PROPERTY_JOB_RESULT_GUEST_PROGRAM_ELAPSED_TIME = 3017,
                                /**
                                 * The exit code resulting from VixVM_RunProgramInGuest().
                                 */
                                VIX_PROPERTY_JOB_RESULT_GUEST_PROGRAM_EXIT_CODE = 3018,
                                /**
                                 * For VixJob_GetNthProperties(), the name of an item found.
                                 */
                                VIX_PROPERTY_JOB_RESULT_ITEM_NAME = 3035,
                                /**
                                 * For VixJob_GetNthProperties(), the description of an item found.
                                 */
                                VIX_PROPERTY_JOB_RESULT_FOUND_ITEM_DESCRIPTION = 3036,
                                VIX_PROPERTY_JOB_RESULT_SHARED_FOLDER_COUNT = 3046,
                                VIX_PROPERTY_JOB_RESULT_SHARED_FOLDER_HOST = 3048,
                                VIX_PROPERTY_JOB_RESULT_SHARED_FOLDER_FLAGS = 3049,
                                VIX_PROPERTY_JOB_RESULT_PROCESS_ID = 3051,
                                VIX_PROPERTY_JOB_RESULT_PROCESS_OWNER = 3052,
                                VIX_PROPERTY_JOB_RESULT_PROCESS_COMMAND = 3053,
                                VIX_PROPERTY_JOB_RESULT_FILE_FLAGS = 3054,
                                VIX_PROPERTY_JOB_RESULT_PROCESS_START_TIME = 3055,
                                VIX_PROPERTY_JOB_RESULT_VM_VARIABLE_STRING = 3056,
                                VIX_PROPERTY_JOB_RESULT_PROCESS_BEING_DEBUGGED = 3057,

                                // Event properties; these are sent in the moreEventInfo for some events.
                                VIX_PROPERTY_FOUND_ITEM_LOCATION = 4010,

                                // VIX_HANDLETYPE_SNAPSHOT properties
                                VIX_PROPERTY_SNAPSHOT_DISPLAYNAME = 4200,
                                VIX_PROPERTY_SNAPSHOT_DESCRIPTION = 4201,
                                VIX_PROPERTY_SNAPSHOT_POWERSTATE = 4205;
    }

    /*
     * These are events that may be signalled by calling a procedure
     * of type VixEventProc.
     */
    public static class VixEventType {
        public static final int VIX_EVENTTYPE_JOB_COMPLETED = 2,
                                VIX_EVENTTYPE_JOB_PROGRESS = 3,
                                VIX_EVENTTYPE_FIND_ITEM = 8,
                                VIX_EVENTTYPE_CALLBACK_SIGNALLED = 2;  // Deprecated - Use VIX_EVENTTYPE_JOB_COMPLETED instead.
    }

    /*
     * These are the property flags for each file.
     */
    public static class VixFileProperties {
        public static final int VIX_FILE_ATTRIBUTES_DIRECTORY = 0x0001,
                                VIX_FILE_ATTRIBUTES_SYMLINK = 0x0002;
    }

    public static class VixHostOptions {
        public static final int VIX_HOSTOPTION_USE_EVENT_PUMP = 0x0008;
    }

    public static class VixServiceProvider {
        public static final int VIX_SERVICEPROVIDER_DEFAULT = 1,
                                VIX_SERVICEPROVIDER_VMWARE_SERVER = 2,
                                VIX_SERVICEPROVIDER_VMWARE_WORKSTATION = 3;
    }

    public static class VixFindItemType {
        public static final int VIX_FIND_RUNNING_VMS = 1,
                                VIX_FIND_REGISTERED_VMS = 4;
    }

    public static class VixPumpEventsOptions {
        public static final int VIX_PUMPEVENTOPTION_NONE = 0;
    }

    public static class VixVMPowerOpOptions {
        public static final int VIX_VMPOWEROP_NORMAL = 0,
                                VIX_VMPOWEROP_SUPPRESS_SNAPSHOT_POWERON = 0x0080,
                                VIX_VMPOWEROP_LAUNCH_GUI = 0x0200;
    }

    public static class VixVMDeleteOptions {
        public static final int VIX_VMDELETE_DISK_FILES = 0x0002;
    }

    /**
     * These are the possible values reported for VIX_PROPERTY_VM_POWER_STATE. They represent runtime information about the state of the virtual machine. To test the value of the property, use the Vix_GetProperties() function.
     */
    public static class VixPowerState {
                                /**
                                 * Indicates that VixVM_PowerOff() has been called, but the operation itself has not completed.
                                 */
        public static final int VIX_POWERSTATE_POWERING_OFF = 0x0001,
                                /**
                                 * Indicates that the virtual machine is not running.
                                 */
                                VIX_POWERSTATE_POWERED_OFF = 0x0002,
                                /**
                                 * Indicates that VixVM_PowerOn() has been called, but the operation itself has not completed.
                                 */
                                VIX_POWERSTATE_POWERING_ON = 0x0004,
                                /**
                                 * Indicates that the virtual machine is running.
                                 */
                                VIX_POWERSTATE_POWERED_ON = 0x0008,
                                /**
                                 * Indicates that VixVM_Suspend() has been called, but the operation itself has not completed.
                                 */
                                VIX_POWERSTATE_SUSPENDING = 0x0010,
                                /**
                                 * Indicates that the virtual machine is suspended. Use VixVM_PowerOn() to resume the virtual machine.
                                 */
                                VIX_POWERSTATE_SUSPENDED = 0x0020,
                                /**
                                 * Indicates that the virtual machine is running and the VMware Tools suite is active. See also the VixToolsState property.
                                 */
                                VIX_POWERSTATE_TOOLS_RUNNING = 0x0040,
                                /**
                                 * Indicates that VixVM_Reset() has been called, but the operation itself has not completed.
                                 */
                                VIX_POWERSTATE_RESETTING = 0x0080,
                                /**
                                 * Indicates that a virtual machine state change is blocked, waiting for user interaction.
                                 */
                                VIX_POWERSTATE_BLOCKED_ON_MSG = 0x0100;
    }

    /**
     * These are the possible values reported for VIX_PROPERTY_VM_TOOLS_STATE. They represent runtime information about the VMware Tools suite in the guest operating system. To test the value of the property, use the Vix_GetProperties() function.
     */
    public static class VixToolsState {
                                /**
                                 * Indicates that Vix is unable to determine the VMware Tools status.
                                 */
        public static final int VIX_TOOLSSTATE_UNKNOWN = 0x0001,
                                /**
                                 * Indicates that VMware Tools is running in the guest operating system.
                                 */
                                VIX_TOOLSSTATE_RUNNING = 0x0002,
                                /**
                                 * Indicates that VMware Tools is not installed in the guest operating system.
                                 */
                                VIX_TOOLSSTATE_NOT_INSTALLED = 0x0004;
    }

    /*
     * These flags describe optional functions supported by different
     * types of VM.
     */
    public static class VixSupportOptions {
        public static final int VIX_VM_SUPPORT_SHARED_FOLDERS = 0x0001,
                                VIX_VM_SUPPORT_MULTIPLE_SNAPSHOTS = 0x0002,
                                VIX_VM_SUPPORT_TOOLS_INSTALL = 0x0004,
                                VIX_VM_SUPPORT_HARDWARE_UPGRADE = 0x0008;
    }

    /*
     * These are special names for an anonymous user and the system administrator.
     * The password is ignored if you specify these.
     */
    public static final String  VIX_ANONYMOUS_USER_NAME = "__VMware_Vix_Guest_User_Anonymous__",
                                VIX_ADMINISTRATOR_USER_NAME = "__VMware_Vix_Guest_User_Admin__",
                                VIX_CONSOLE_USER_NAME = "__VMware_Vix_Guest_Console_User__";

    public static class VixRunProgramOptions {
        public static final int VIX_RUNPROGRAM_RETURN_IMMEDIATELY = 0x0001,
                                VIX_RUNPROGRAM_ACTIVATE_WINDOW = 0x0002;
    }

    public static class VixGuestVariableTypes {
        public static final int VIX_VM_GUEST_VARIABLE = 1,
                                VIX_VM_CONFIG_RUNTIME_ONLY = 2,
                                VIX_GUEST_ENVIRONMENT_VARIABLE = 3;
    }

    public static class VixRemoveSnapshotOptions {
        public static final int VIX_SNAPSHOT_REMOVE_CHILDREN = 0x0001;
    }

    public static class VixCreateSnapshotOptions {
        public static final int VIX_SNAPSHOT_INCLUDE_MEMORY = 0x0002;
    }

    /*
     * These are the flags describing each shared folder.
     */
    public static class VixMsgSharedFolderOptions {
        public static final int VIX_SHAREDFOLDER_WRITE_ACCESS = 0x04;
    }

    // --------------------------
    // functions

    /**
     * Creates a host handle.
     *
     * @param apiVersion         Must be VIX_API_VERSION.
     * @param hostType           VIX_SERVICEPROVIDER_VMWARE_SERVER or VIX_SERVICEPROVIDER_VMWARE_WORKSTATION.
     * @param hostName           DNS name or IP address of remote host. Use NULL to connect to local host.
     * @param hostPort           TCP/IP port of remote host, typically 902. Use zero for local host.
     * @param userName           Username to authenticate with on remote machine. Use NULL to authenticate as current user on local host.
     * @param password           Password to authenticate with on remote machine. Use NULL to authenticate as current user on local host.
     * @param options            Optionally VIX_HOSTOPTION_USE_EVENT_PUMP (See Remarks section), otherwise zero.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       Optional callback of type VixEventProc.
     * @param clientData         Optional user supplied opaque data to be passed to optional callback.
     * @return A job handle. When the job completes, retrieve the Host handle from the job handle using the VIX_PROPERTY_JOB_RESULT_HANDLE property.
     */
    public int VixHost_Connect(int apiVersion,
                               int hostType,
                               String hostName,
                               int hostPort,
                               String userName,
                               String password,
                               int options,
                               int propertyListHandle,
                               VixEventProc callbackProc,
                               Pointer clientData);

    /**
     * Wait for a particular job to complete.
     *
     * @param jobHandle       The handle of a job object, returned from any asynchronous Vix function.
     * @param firstPropertyID The identity of a property, or else VIX_PROPERTY_NONE.
     * @param args
     * @return VixError. The error resulting from the asynchronous operation that returned the job handle.
     */
    public long VixJob_Wait(int jobHandle,
                            int firstPropertyID,
                            Object... args);

    /**
     * This function decrements the reference count for a handle and destroys the handle when there are no references.
     *
     * @param handle Any handle returned by a Vix function.
     */
    public void Vix_ReleaseHandle(int handle);

    /**
     * Returns a human-readable string that describes the error.
     *
     * @param err    A Vix error code returned by any other Vix function.
     * @param locale Must be NULL.
     * @return A human-readable string that describes the error.
     */
    public String Vix_GetErrorText(long err, String locale);

    /**
     * This function allows you to get one or more properties from a handle.
     *
     * @param handle          handle returned by a Vix function.
     * @param firstPropertyID A property ID. See below for valid values.
     * @param args
     * @return VixError. This function returns VIX_OK if it succeeded, otherwise the return value indicates an error.
     */
    public long Vix_GetProperties(int handle,
                                  int firstPropertyID,
                                  Object... args);

    /**
     * Given a property ID, this function returns the type of that property.
     *
     * @param handle       Any handle returned by a VIX function.
     * @param propertyID   A property ID. See below for valid values.
     * @param propertyType The type of the data stored by the property.
     * @return VixError. This function returns VIX_OK if it succeeded.
     */
    public int Vix_GetPropertyType(int handle,
                                   int propertyID,
                                   IntByReference propertyType);

    /**
     * When Vix_GetProperties() or Vix_JobWait() returns a string property, it allocates a buffer for the string. Client applications are responsible for calling Vix_FreeBuffer() to free the string buffer when no longer needed.
     *
     * @param p A string pointer returned by a call to Vix_GetProperties() or Vix_JobWait().
     */
    void Vix_FreeBuffer(Pointer p);

    /**
     * Given a handle, this returns the handle type.
     *
     * @param handle Any handle returned by a Vix function.
     * @return An enumerated type that identifies what kind of handle this is.
     */
    public int Vix_GetHandleType(int handle);


    /**
     * Destroys the state for a particular host handle.
     *
     * @param hostHandle The host handle returned by VixHost_Connect().
     */
    public void VixHost_Disconnect(int hostHandle);

    /**
     * This function opens a virtual machine on the host that is identified by the hostHandle parameter and returns a context to that machine as a virtual machine handle.
     *
     * @param hostHandle      The handle of a host object, typically returned from VixHost_Connect().
     * @param vmxFilePathName The path name of the virtual machine configuration file on the local host.
     * @param callbackProc    A callback function that will be invoked when the operation is complete.
     * @param clientData      A parameter that will be passed to the callbackProc procedure.
     * @return VixHandle. A job handle that describes the state of this asynchronous call.
     */
    public int VixVM_Open(int hostHandle,
                          String vmxFilePathName,
                          VixEventProc callbackProc,
                          Pointer clientData);

    /**
     * This function asynchronously finds Vix objects and calls the application's callback function to report each object found. For example, when used to find all running virtual machines, VixHost_FindItems() returns a series of virtual machine file path names.
     *
     * @param hostHandle     The host handle returned by VixHost_Connect().
     * @param searchType     The type of items to find. Values are listed in the Types Reference under VixFindItemType.
     * @param searchCriteria Must be VIX_INVALID_HANDLE.
     * @param timeout        Must be -1.
     * @param callbackProc   A function to be invoked when VixHost_FindItems() completes.
     * @param clientData     A user-supplied parameter to be passed to the callback function.
     * @return VixHandle. A job handle that describes the state of this asynchronous call.
     */
    public int VixHost_FindItems(int hostHandle,
                                 int searchType,
                                 int searchCriteria,
                                 int timeout,
                                 VixEventProc callbackProc,
                                 Pointer clientData);

    /**
     * This function adds a virtual machine to the host's inventory.
     *
     * @param hostHandle   The host handle returned by VixHost_Connect().
     * @param vmxFilePath  The path name of the .vmx file on the host.
     * @param callbackProc A function to be invoked when VixHost_RegisterVM() completes.
     * @param clientData   A user-supplied parameter to be passed to the callback function.
     * @return VixHandle. A job handle that describes the state of this asynchronous call.
     */
    public int VixHost_RegisterVM(int hostHandle,
                                  String vmxFilePath,
                                  VixEventProc callbackProc,
                                  Pointer clientData);

    /**
     * This function removes a virtual machine from the host's inventory.
     *
     * @param hostHandle   The host handle returned by VixHost_Connect().
     * @param vmxFilePath  The path name of the .vmx file on the host.
     * @param callbackProc A function to be invoked when VixHost_UnregisterVM() completes.
     * @param clientData   A user-supplied parameter to be passed to the callback function.
     * @return VixHandle. A job handle that describes the state of this asynchronous call.
     */
    public int VixHost_UnregisterVM(int hostHandle,
                                    String vmxFilePath,
                                    VixEventProc callbackProc,
                                    Pointer clientData);

    /**
     * Vix_PumpEvents is used in single threaded applications that require the Vix library to be single threaded. Tasks that would normally be executed in a separate thread by the Vix library will be executed when Vix_PumpEvents() is called.
     *
     * @param hostHandle The handle to the local host object.
     * @param options    Must be 0.
     */
    public void Vix_PumpEvents(int hostHandle, int options);

    /**
     * Powers on a virtual machine.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param powerOnOptions     VIX_VMPOWEROP_NORMAL or VIX_VMPOWEROP_LAUNCH_GUI.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the power operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_PowerOn(int vmHandle,
                             int powerOnOptions,
                             int propertyListHandle,
                             VixEventProc callbackProc,
                             Pointer clientData);

    /**
     * This function powers off a virtual machine.
     *
     * @param vmHandle        Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param powerOffOptions Must be 0.
     * @param callbackProc    A callback function that will be invoked when the power operation is complete.
     * @param clientData      A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_PowerOff(int vmHandle,
                              int powerOffOptions,
                              VixEventProc callbackProc,
                              Pointer clientData);

    /**
     * This function resets a virtual machine, which is the equivalent of pressing the reset button on a physical machine.
     *
     * @param vmHandle       Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param powerOnOptions Must be VIX_VMPOWEROP_NORMAL.
     * @param callbackProc   A callback function that will be invoked when the power operation is complete.
     * @param clientData     A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_Reset(int vmHandle,
                           int powerOnOptions,
                           VixEventProc callbackProc,
                           Pointer clientData);

    /**
     * This function suspends a virtual machine.
     *
     * @param vmHandle        Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param powerOffOptions Must be 0.
     * @param callbackProc    A callback function that will be invoked when the power operation is complete.
     * @param clientData      A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_Suspend(int vmHandle,
                             int powerOffOptions,
                             VixEventProc callbackProc,
                             Pointer clientData);

    /**
     * This function permanently deletes a virtual machine from your host system.
     *
     * @param vmHandle      Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param deleteOptions Must be 0.
     * @param callbackProc  A callback function that will be invoked when the operation is complete.
     * @param clientData    A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous call.
     */
    public int VixVM_Delete(int vmHandle,
                            int deleteOptions,
                            VixEventProc callbackProc,
                            Pointer clientData);

    /**
     * This function signals the job handle when VMware Tools has successfully started in the guest operating system. VMware Tools is a collection of services that run in the guest.
     *
     * @param vmHandle         Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param timeoutInSeconds The timeout in seconds. If VMware Tools has not started by this time, the function completes with an error. If the value of this argument is zero or negative, then there will be no timeout.
     * @param callbackProc     A callback function that will be invoked when the operation is complete.
     * @param clientData       A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_WaitForToolsInGuest(int vmHandle,
                                         int timeoutInSeconds,
                                         VixEventProc callbackProc,
                                         Pointer clientData);

    /**
     * This function establishes a guest operating system authentication context that can be used with guest functions for the given virtual machine handle.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param userName     The name of a user account on the guest operating system.
     * @param password     The password of the account identified by userName.
     * @param options      Must be 0.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_LoginInGuest(int vmHandle,
                                  String userName,
                                  String password,
                                  int options,
                                  VixEventProc callbackProc,
                                  Pointer clientData);

    /**
     * This function removes any guest operating system authentication context created by a previous call to VixVM_LoginInGuest().
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_LogoutFromGuest(int vmHandle,
                                     VixEventProc callbackProc,
                                     Pointer clientData);

    /**
     * This function runs a program in the guest operating system. The program must be stored on a file system available to the guest before calling this function.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param guestProgramName   The path name of an executable file on the guest operating system.
     * @param commandLineArgs    A string to be passed as command line arguments to the executable identified by guestProgramName.
     * @param options            Run options for the program. See the remarks below.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_RunProgramInGuest(int vmHandle,
                                       String guestProgramName,
                                       String commandLineArgs,
                                       int options,
                                       int propertyListHandle,
                                       VixEventProc callbackProc,
                                       Pointer clientData);

    /**
     * This function lists the running processes in the guest operating system.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param options      Must be 0.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_ListProcessesInGuest(int vmHandle,
                                          int options,
                                          VixEventProc callbackProc,
                                          Pointer clientData);

    /**
     * This function terminates a process in the guest operating system.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param pid          The ID of the process to be killed.
     * @param options      Must be 0.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_KillProcessInGuest(int vmHandle,
                                        long pid,
                                        int options,
                                        VixEventProc callbackProc,
                                        Pointer clientData);

    /**
     * This function runs a script in the guest operating system.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param interpreter        The path to the script interpreter.
     * @param scriptText         The text of the script.
     * @param options            Run options for the program. See the notes below.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_RunScriptInGuest(int vmHandle,
                                      String interpreter,
                                      String scriptText,
                                      int options,
                                      int propertyListHandle,
                                      VixEventProc callbackProc,
                                      Pointer clientData);

    /**
     * This function opens a browser window on the specified URL in the guest operating system.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param url                The URL to be opened.
     * @param windowState        Must be 0.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_OpenUrlInGuest(int vmHandle,
                                    String url,
                                    int windowState,
                                    int propertyListHandle,
                                    VixEventProc callbackProc,
                                    Pointer clientData);

    /**
     * Copies a file or directory from the local system (where the Vix client is running) to the guest operating system.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param hostPathName       The path name of a file on a file system available to the Vix client.
     * @param guestPathName      The path name of a file on a file system available to the guest.
     * @param options            Must be 0.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_CopyFileFromHostToGuest(int vmHandle,
                                             String hostPathName,
                                             String guestPathName,
                                             int options,
                                             int propertyListHandle,
                                             VixEventProc callbackProc,
                                             Pointer clientData);

    /**
     * Copies a file or directory from the guest operating system to the local system (where the Vix client is running).
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param guestPathName      The path name of a file on a file system available to the guest.
     * @param hostPathName       The path name of a file on a file system available to the Vix client.
     * @param options            Must be 0.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_CopyFileFromGuestToHost(int vmHandle,
                                             String guestPathName,
                                             String hostPathName,
                                             int options,
                                             int propertyListHandle,
                                             VixEventProc callbackProc,
                                             Pointer clientData);

    /**
     * This function deletes a file in the guest operating system.
     *
     * @param vmHandle      Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param guestPathName The path to the file to be deleted.
     * @param callbackProc  A callback function that will be invoked when the operation is complete.
     * @param clientData    A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_DeleteFileInGuest(int vmHandle,
                                       String guestPathName,
                                       VixEventProc callbackProc,
                                       Pointer clientData);

    /**
     * This function tests the existence of a file in the guest operating system.
     *
     * @param vmHandle      Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param guestPathName The path to the file to be tested.
     * @param callbackProc  A callback function that will be invoked when the operation is complete.
     * @param clientData    A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_FileExistsInGuest(int vmHandle,
                                       String guestPathName,
                                       VixEventProc callbackProc,
                                       Pointer clientData);

    /**
     * This function renames a file or directory in the guest operating system.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param oldName            The path to the file to be renamed.
     * @param newName            The path to the new file.
     * @param options            Must be 0.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_RenameFileInGuest(int vmHandle,
                                       String oldName,
                                       String newName,
                                       int options,
                                       int propertyListHandle,
                                       VixEventProc callbackProc,
                                       Pointer clientData);

    /**
     * This function creates a temporary file in the guest operating system.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param options            Must be 0.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_CreateTempFileInGuest(int vmHandle,
                                           int options,
                                           int propertyListHandle,
                                           VixEventProc callbackProc,
                                           Pointer clientData);

    /**
     * This function lists a directory in the guest operating system.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param pathName     The path name of a directory to be listed.
     * @param options      Must be 0.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_ListDirectoryInGuest(int vmHandle,
                                          String pathName,
                                          int options,
                                          VixEventProc callbackProc,
                                          Pointer clientData);

    /**
     * This function creates a directory in the guest operating system.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param pathName           The path to the directory to be created.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData
     * @return A parameter that will be passed to the callbackProc function.
     */
    public int VixVM_CreateDirectoryInGuest(int vmHandle,
                                            String pathName,
                                            int propertyListHandle,
                                            VixEventProc callbackProc,
                                            Pointer clientData);

    /**
     * This function deletes a directory in the guest operating system. Any files or subdirectories in the specified directory will also be deleted.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param pathName     The path to the directory to be deleted.
     * @param options      Must be 0.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_DeleteDirectoryInGuest(int vmHandle,
                                            String pathName,
                                            int options,
                                            VixEventProc callbackProc,
                                            Pointer clientData);

    /**
     * This function tests the existence of a directory in the guest operating system.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param pathName     The path to the directory in the guest to be checked.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_DirectoryExistsInGuest(int vmHandle,
                                            String pathName,
                                            VixEventProc callbackProc,
                                            Pointer clientData);

//    /**
//     * Not documented in the vmware API documentation.
//     *
//     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
//     * @param variableType VIX_VM_GUEST_VARIABLE, VIX_VM_CONFIG_RUNTIME_ONLY or VIX_GUEST_ENVIRONMENT_VARIABLE
//     * @param name         Name of the variable.
//     * @param options      Must be 0.
//     * @param callbackProc A callback function that will be invoked when the operation is complete.
//     * @param clientData   A parameter that will be passed to the callbackProc function.
//     * @return A job handle that describes the state of this asynchronous operation.
//     */
//    public int VixVM_ReadVariable(int vmHandle,
//                                  int variableType,
//                                  String name,
//                                  int options,
//                                  VixEventProc callbackProc,
//                                  Pointer clientData);

//    /**
//     * Not documented in the vmware API documentation.
//     *
//     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
//     * @param variableType VIX_VM_GUEST_VARIABLE, VIX_VM_CONFIG_RUNTIME_ONLY or VIX_GUEST_ENVIRONMENT_VARIABLE
//     * @param valueName    Name of the variable.
//     * @param value        Value of the variable.
//     * @param options      Must be 0.
//     * @param callbackProc A callback function that will be invoked when the operation is complete.
//     * @param clientData   A parameter that will be passed to the callbackProc function.
//     * @return A job handle that describes the state of this asynchronous operation.
//     */
//    public int VixVM_WriteVariable(int vmHandle,
//                                   int variableType,
//                                   String valueName,
//                                   String value,
//                                   int options,
//                                   VixEventProc callbackProc,
//                                   Pointer clientData);

    /**
     * This function returns the number of top-level (root) snapshots belonging to a virtual machine.
     *
     * @param vmHandle Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param result   The number of root snapshots on this virtual machine.
     * @return VixError.
     */
    public long VixVM_GetNumRootSnapshots(int vmHandle,
                                          IntByReference result);

    /**
     * This function returns the handle of the specified snapshot belonging to the virtual machine referenced by vmHandle.
     *
     * @param vmHandle       Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param index          Identifies a root snapshot. See below for range of values.
     * @param snapshotHandle (output) A handle to a snapshot.
     * @return VixError.
     */
    public long VixVM_GetRootSnapshot(int vmHandle,
                                      int index,
                                      IntByReference snapshotHandle);

    /**
     * This function returns the handle of the current active snapshot belonging to the virtual machine referenced by vmHandle.
     *
     * @param vmHandle       Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param snapshotHandle An output parameter that receives a handle to a snapshot.
     * @return VixError.
     */
    public long VixVM_GetCurrentSnapshot(int vmHandle,
                                         IntByReference snapshotHandle);

    /**
     * This function returns the handle of the snapshot matching the given name in the virtual machine referenced by vmHandle.
     *
     * @param vmHandle       Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param name           Idenitfies a snapshot name.
     * @param snapshotHandle An output parameter that receives a handle to a snapshot.
     * @return VixError.
     */
    public long VixVM_GetNamedSnapshot(int vmHandle,
                                       String name,
                                       IntByReference snapshotHandle);

    /**
     * This function deletes all saved states for the specified snapshot.
     *
     * @param vmHandle       Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param snapshotHandle A handle to a snapshot. Call VixVM_GetRootSnapshot() to get a snapshot handle.
     * @param options        Flags to specify optional behavior. Any combination of the following or 0:  VIX_SNAPSHOT_REMOVE_CHILDREN - Remove snapshots that are children of the given snapshot.
     * @param callbackProc   A callback function that will be invoked when the operation is complete.
     * @param clientData     A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_RemoveSnapshot(int vmHandle,
                                    int snapshotHandle,
                                    int options,
                                    VixEventProc callbackProc,
                                    Pointer clientData);

    /**
     * Restores the virtual machine to the state when the specified snapshot was created.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param snapshotHandle     A handle to a snapshot. Call VixVM_GetRootSnapshot() to get a snapshot handle.
     * @param options            Any applicable VixVMPowerOpOptions. If the virtual machine was powered on when the snapshot was created, then this will determine how the virtual machine is powered back on. To prevent the virtual machine from being powered on regardless of the power state when the snapshot was created, use the VIX_VMPOWEROP_SUPPRESS_SNAPSHOT_POWERON flag. VIX_VMPOWEROP_SUPPRESS_SNAPSHOT_POWERON is mutually exclusive to all other VixVMPowerOpOptions.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_RevertToSnapshot(int vmHandle,
                                      int snapshotHandle,
                                      int options,
                                      int propertyListHandle,
                                      VixEventProc callbackProc,
                                      Pointer clientData);

    /**
     * This function saves a copy of the virtual machine state as a snapshot object. The handle of the snapshot object is returned in the job object properties.
     *
     * @param vmHandle           Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param name               A user-defined name for the snapshot; need not be unique.
     * @param description        A user-defined description for the snapshot.
     * @param options            Flags to specify how the shapshot should be created. Any combination of the following or 0: VIX_SNAPSHOT_INCLUDE_MEMORY - Captures the full state of a running virtual machine, including the memory.
     * @param propertyListHandle Must be VIX_INVALID_HANDLE.
     * @param callbackProc       A callback function that will be invoked when the operation is complete.
     * @param clientData         A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_CreateSnapshot(int vmHandle,
                                    String name,
                                    String description,
                                    int options,
                                    int propertyListHandle,
                                    VixEventProc callbackProc,
                                    Pointer clientData);

    /**
     * This function enables or disables all shared folders as a feature for a virtual machine.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param enabled      TRUE if enabling shared folders is desired. FALSE otherwise.
     * @param options      Must be 0.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_EnableSharedFolders(int vmHandle,
                                         boolean enabled,
                                         int options,
                                         VixEventProc callbackProc,
                                         Pointer clientData);

    /**
     * This function returns the number of shared folders mounted in the virtual machine.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_GetNumSharedFolders(int vmHandle,
                                         VixEventProc callbackProc,
                                         Pointer clientData);

    /**
     * This function returns the state of a shared folder mounted in the virtual machine.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param index        Identifies the shared folder.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_GetSharedFolderState(int vmHandle,
                                          int index,
                                          VixEventProc callbackProc,
                                          Pointer clientData);

    /**
     * This function modifies the state of a shared folder mounted in the virtual machine.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param shareName    Specifies the name of the shared folder.
     * @param hostPathName Specifies the host path of the shared folder.
     * @param flags        The new flag settings.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_SetSharedFolderState(int vmHandle,
                                          String shareName,
                                          String hostPathName,
                                          int flags,
                                          VixEventProc callbackProc,
                                          Pointer clientData);

    /**
     * This function mounts a new shared folder in the virtual machine.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param shareName    Specifies the guest path name of the new shared folder.
     * @param hostPathName Specifies the host path of the shared folder.
     * @param flags        The folder options.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_AddSharedFolder(int vmHandle,
                                     String shareName,
                                     String hostPathName,
                                     int flags,
                                     VixEventProc callbackProc,
                                     Pointer clientData);

    /**
     * This function removes a shared folder in the virtual machine.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param shareName    Specifies the guest pathname of the shared folder to delete.
     * @param flags        Must be 0.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_RemoveSharedFolder(int vmHandle,
                                        String shareName,
                                        int flags,
                                        VixEventProc callbackProc,
                                        Pointer clientData);

    /**
     * Upgrades the virtual hardware version of the virtual machine to match the version of the VIX library. This has no effect if the virtual machine is already at the same version or at a newer version than the VIX library.
     *
     * @param vmHandle     Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param options      Must be 0.
     * @param callbackProc A callback function that will be invoked when the operation is complete.
     * @param clientData   A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_UpgradeVirtualHardware(int vmHandle,
                                            int options,
                                            VixEventProc callbackProc,
                                            Pointer clientData);

    /**
     * Installs VMware Tools on the guest operating system.
     *
     * @param vmHandle        Identifies a virtual machine. Call VixVM_Open() to create a virtual machine handle.
     * @param options         Must be 0.
     * @param commandLineArgs Must be NULL.
     * @param callbackProc    A callback function that will be invoked when the operation is complete.
     * @param clientData      A parameter that will be passed to the callbackProc function.
     * @return VixHandle. A job handle that describes the state of this asynchronous operation.
     */
    public int VixVM_InstallTools(int vmHandle,
                                  int options,
                                  String commandLineArgs,
                                  VixEventProc callbackProc,
                                  Pointer clientData);

    /**
     * This function performs a non-blocking test for completion of an asynchronous operation.
     *
     * @param jobHandle The handle of a job object, returned from any asynchronous Vix function.
     * @param complete  An indicator of whether the job has completed.
     * @return VixError. This function returns VIX_OK if it succeeded.
     */
    public long VixJob_CheckCompletion(int jobHandle,
                                       IntByReference complete);

    /**
     * Retrieves the error code from a job that has completed.
     *
     * @param jobHandle The handle of a job object, returned from any asynchronous Vix function.
     * @return VixError. The result returned by a completed asynchronous function.
     */
    public int VixJob_GetError(int jobHandle);

    /**
     * Retrieves the number of instances of the specified property. Used to work with returned property lists.
     *
     * @param jobHandle        The handle of a job object, returned from any asynchronous Vix function.
     * @param resultPropertyID A property ID.
     * @return The number of properties with an ID of resultPropertyID.
     */
    public int VixJob_GetNumProperties(int jobHandle,
                                       int resultPropertyID);

    /**
     * Retrieves the property at a specific index in a list. You can use this to iterate through returned property lists.
     *
     * @param jobHandle  The handle of a job object, returned from any asynchronous Vix function.
     * @param index      Index into the property list of the job object.
     * @param propertyID A property ID.
     * @param args
     * @return VixError. The result returned by a completed asynchronous function.
     */
    public int VixJob_GetNthProperties(int jobHandle,
                                       int index,
                                       int propertyID,
                                       Object... args);

    /**
     * This function returns the number of child snapshots of a specified snapshot.
     *
     * @param parentSnapshotHandle A snapshot handle.
     * @param numChildSnapshots    (output) The number of child snapshots belonging to the specified snapshot.
     * @return VixError
     */
    public int VixSnapshot_GetNumChildren(int parentSnapshotHandle,
                                          IntByReference numChildSnapshots);

    /**
     * This function returns the specified child snapshot.
     *
     * @param parentSnapshotHandle A snapshot handle.
     * @param index                Index into the list of snapshots.
     * @param childSnapshotHandle  (output) A handle to the child snapshot.
     * @return VixError
     */
    public int VixSnapshot_GetChild(int parentSnapshotHandle,
                                    int index,
                                    IntByReference childSnapshotHandle);

    /**
     * This function returns the parent of a snapshot.
     *
     * @param snapshotHandle       A snapshot handle.
     * @param parentSnapshotHandle (output) A handle to the parent of the specified snapshot.
     * @return VixError
     */
    public int VixSnapshot_GetParent(int snapshotHandle,
                                     IntByReference parentSnapshotHandle);
}
