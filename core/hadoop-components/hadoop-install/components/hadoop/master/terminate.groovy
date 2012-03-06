package hadoop.master;

log("Stopping Hadoop Master")

def binDir = "${destDir}/bin"
def cmd = "bash $binDir/worker.sh $binDir "
exec("$cmd stop jobtracker", binDir)
exec("$cmd stop tasktracker", binDir)
exec("$cmd stop datanode", binDir)
exec("$cmd stop namenode", binDir)
log("Master has been shut down")