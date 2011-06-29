package hadoop.worker;

sfLog().info("Starting Hadoop Worker")

def binDir = "${destDir}/bin"
def cmd = "bash $binDir/worker.sh $binDir "
exec("$cmd stop tasktracker", binDir)
exec("$cmd $binDir stop datanode", binDir)

failIf(exec("$cmd start datanode", binDir), "could not start namenode")
failIf(exec("$cmd start tasktracker", binDir), "could not start tasktracker")
sfLog().info("Started Hadoop Worker in ${binDir}")