/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.services.hadoop.graph;

import java.util.HashSet;
import java.util.Hashtable;

/**
 * The Graph
 */
public class Graph {

    private HashSet<Node> nodes = new HashSet<Node>();
    private Hashtable<Node, HashSet<Node>> outgoing_links = new Hashtable<Node, HashSet<Node>>();
    private Hashtable<Node, HashSet<Node>> incoming_links = new Hashtable<Node, HashSet<Node>>();
    private int count_links = 0;

    public boolean addNode(Node node) {
        if (nodes.contains(node)) {
            return false;
        }

        nodes.add(node);
        if (!outgoing_links.containsKey(node)) {
            outgoing_links.put(node, new HashSet<Node>());
        }
        if (!incoming_links.containsKey(node)) {
            incoming_links.put(node, new HashSet<Node>());
        }
        return true;
    }

    public boolean addLink(Node source, Node destination) {
        if (source.equals(destination)) {
            return false;
        }

        addNode(source);
        addNode(destination);

        if (outgoing_links.get(source).contains(destination)) {
            return false;
        }

        outgoing_links.get(source).add(destination);
        incoming_links.get(destination).add(source);
        count_links++;

        return true;
    }

    public int countNodes() {
        return nodes.size();
    }

    public int countLinks() {
        return count_links;
    }

    public int countOutgoingLinks(Node node) {
        return outgoing_links.get(node).size();
    }

    public HashSet<Node> getIncomingLinks(Node node) {
        return incoming_links.get(node);
    }

    public HashSet<Node> getNodes() {
        return nodes;
    }

}
