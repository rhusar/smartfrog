def host = command("hostname").text

sfLog().info("Start for Hadoop Slave on $host")

def directory = sfResolve("directory")
command("sh slaveScript.sh stop", "${directory}/bin").waitFor()
command("sh slaveScript.sh start", "${directory}/bin").waitFor()