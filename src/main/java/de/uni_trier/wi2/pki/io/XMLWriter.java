package de.uni_trier.wi2.pki.io;

import de.uni_trier.wi2.pki.tree.DecisionTree;
import de.uni_trier.wi2.pki.tree.DecisionTreeLeafNode;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Serializes the decision tree in form of an XML structure.
 */
public class XMLWriter {

    public static final String N_DECISION_TREE = "DecisionTree";
    public static final String N_NODE = "Node";
    public static final String N_IF = "IF";
    public static final String N_LEAF_NODE = "LeafNode";

    public static final String A_ATTRIBUTE = "attributeIndex";
    public static final String A_VALUE = "value";
    public static final String A_CLASS = "class";

    /**
     * Serialize decision tree to specified path.
     *
     * @param path         the path to write to.
     * @param decisionTree the tree to serialize.
     * @throws IOException if something goes wrong.
     */
    public static void writeXML(String path, DecisionTree decisionTree) throws IOException {

    }

    /**
     * Serialize decision tree to the specified output stream.
     *
     * @param outputStream the stream to write to.
     * @param decisionTree the tree to serialize.
     */
    public static void writeXML(OutputStream outputStream, DecisionTree decisionTree) {

    }

    /**
     * Recursively adds nodes to the tree by being called for every subtree.
     *
     * @param decTreeNode   the decision tree node representing a subtree.
     * @param xmlParentElem the parent element in the XML to append to.
     */
    private static void addNode(DecisionTreeNode decTreeNode, Element xmlParentElem) {

    }

}
