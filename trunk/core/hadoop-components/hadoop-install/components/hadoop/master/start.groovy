package hadoop.master;
def host = command("hostname -f").text

sfLog().info("Start for Hadoop Master on $host")

command("sh masterScript.sh stop", "${destDir}/bin").waitFor()
command("sh masterScript.sh start", "${destDir}/bin").waitFor()