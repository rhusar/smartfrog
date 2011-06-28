def host = command("hostname -f").text

sfLog().info("Start for Hadoop Master on $host")

command("sh masterScript.sh stop", "${sfResolve("directory")}/bin").waitFor()
command("sh masterScript.sh start", "${sfResolve("directory")}/bin").waitFor()