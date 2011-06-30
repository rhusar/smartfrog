package hadoop.worker;

log("Stopping Hadoop Worker")

def binDir = "${destDir}/bin"
def cmd = "bash $binDir/worker.sh $binDir "
exec("$cmd stop tasktracker", binDir)
exec("$cmd stop datanode", binDir)
