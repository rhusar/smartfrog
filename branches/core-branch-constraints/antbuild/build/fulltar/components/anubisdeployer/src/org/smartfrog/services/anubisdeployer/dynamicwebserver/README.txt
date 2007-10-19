
This colleciton of classes is an implementation of the dynamic web server example that makes use of the
Anubis system for discovering nodes, and the Anubis Deployer to deploy new instances of a web server.

The example differs in a number of ways:

1) Every node that needs to take part in the example has to be initialised with the Anubis system, announcing
itself to the world. The descriptions for this are in the file nodeDescription.sf. Start the daemon with this.

2) The thresholder no longer needs a static list of nodes that may be used to deploy web sevrers, rather it
uses the Anubis layer to discover the nodes. Indeed the thresholder never actually needs to know which they are,
it uses the AnubisDeployer to select an appropriate node (assuming one exists).

3) The thresholder works in a slightly different way - it now simply monitors the number of instances it
has running, and decides if it needs another or not, and simply attempts to deploy another if it
is short of web severs, or removes one if it has too many. It handles the failure of a node containing a web sevrer
by simply removing it as a child. Consequently, failures will simply be handled by a the normal process of deciding
whether another is required or not.

4) Thje process of deciding whether a change in number is required is through looking at the measures against the
thrwsholds, and the mimimun and maximum numbers. It adds or removes at most one sevrer (other than through failure)
each tick of the polling. Thus initial start up with a minimum of more than 1 will ramp up over time (each tick is
about 5 secs in the default configuration).

The example simply reimplements the ThresholderImpl component, and changes the sf files to match. Everything
else simply uses the deifnitions given in the original non-Anubis version of the example.