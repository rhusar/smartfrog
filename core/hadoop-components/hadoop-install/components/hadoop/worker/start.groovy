package hadoop.worker;

log("Starting Hadoop Worker")

def binDir = "${destDir}/bin"
def cmd = "bash $binDir/worker.sh $binDir "
exec("$cmd stop tasktracker", binDir)
exec("$cmd stop datanode", binDir)

failIf(
        exec("$cmd start datanode", binDir)
        , "could not start datanode")
failIf(exec("$cmd start tasktracker", binDir), "could not start tasktracker")
log("Started Hadoop Worker in ${binDir}")