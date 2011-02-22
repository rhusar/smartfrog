package org.smartfrog.services.hadoop.graph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;


public class GraphRank {

    private final Log LOG = LogFactory.getLog(GraphRank.class);
    private Graph graph = new Graph();
    private Hashtable<Node, Double> rank_current = new Hashtable<Node, Double>();
    private Hashtable<Node, Double> rank_new = new Hashtable<Node, Double>();

    private Writer out = null;
    private double dumping_factor;
    private int iterations;

    public GraphRank(BufferedReader in, Writer out, double dumping_factor, int iterations) {
        this.out = out;
        this.dumping_factor = dumping_factor;
        this.iterations = iterations;

        try {
            MemoryUtil.printUsedMemory(LOG);
            load_data(in);
            MemoryUtil.printUsedMemory(LOG);

            initialize();
        } catch (IOException e) {
            LOG.error("Failed to initialize", e);
        }
    }

    public Hashtable<Node, Double> compute() {
        double teleport = (1.0d - dumping_factor) / graph.countNodes();
        LOG.info("Iterations: ");
        for (int i = 0; i < iterations; i++) {
            LOG.info(" Iteration " + (i + 1));

            double dangling_nodes = 0.0d;
            for (Node node : graph.getNodes()) {
                if (graph.countOutgoingLinks(node) == 0) {
                    dangling_nodes += rank_current.get(node);
                }
            }
            dangling_nodes = (dumping_factor * dangling_nodes) / graph.countNodes();

            for (Node node : graph.getNodes()) {
                double r = 0.0d;
                for (Node source : graph.getIncomingLinks(node)) {
                    r += rank_current.get(source) / graph.countOutgoingLinks(source);
                }
                r = dumping_factor * r + dangling_nodes + teleport;
                rank_new.put(node, r);
            }

            for (Node node : graph.getNodes()) {
                rank_current.put(node, rank_new.get(node));
            }
        }
        LOG.info("Finished");

        try {
            dump(out);
        } catch (IOException e) {
            LOG.error("Failed to dump", e);
        }

        return rank_current;
    }

    private void initialize() {
        Double initial_rank = (1.0d / graph.countNodes());
        for (Node node : graph.getNodes()) {
            rank_current.put(node, initial_rank);
        }
    }

    private void load_data(BufferedReader in) throws IOException {
        long start = System.currentTimeMillis();

        try {
            String line;
            while ((line = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                Node source = new Node(st.nextToken());
                graph.addNode(source);
                HashSet<String> seen = new HashSet<String>();
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (!seen.contains(token)) { // no multiple links to the same page
                        Node destination = new Node(token);
                        if (destination != source) { // no self-links
                            graph.addNode(destination);
                            graph.addLink(source, destination);
                        }
                        seen.add(token);
                    }
                }
            }
        } finally {
            in.close();
        }

        LOG.info("Loaded " + graph.countNodes() + " nodes and "
                + graph.countLinks() + " links in "
                + (System.currentTimeMillis() - start) + " ms");
    }

    private void dump(Writer out) throws IOException {
        try {
            DecimalFormat f = new DecimalFormat("#.####################");
            for (Node node : graph.getNodes()) {
                out.write(node.getId() + " " + f.format(rank_current.get(node)) + "\n");
            }
        } finally {
            out.close();
        }
    }

}
