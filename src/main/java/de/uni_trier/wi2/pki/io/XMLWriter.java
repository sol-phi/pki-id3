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
        try (FileOutputStream fos = new FileOutputStream(path)) {
            writeXML(fos, decisionTree);
        }
    }

    /**
     * Serialize decision tree to the specified output stream.
     *
     * @param outputStream the stream to write to.
     * @param decisionTree the tree to serialize.
     */
    public static void writeXML(OutputStream outputStream, DecisionTree decisionTree) {
        // root creates an empty "<DecisionTree></DecisionTree>", which gets filled in with addNode().
        Element root = new Element(N_DECISION_TREE);
        addNode(decisionTree, root);
        Document doc = new Document(root); // Adds the XML header for completeness
        try {
            new XMLOutputter(Format.getPrettyFormat()).output(doc, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively adds nodes to the tree by being called for every subtree.
     *
     * @param decTreeNode   the decision tree node representing a subtree.
     * @param xmlParentElem the parent element in the XML to append to.
     */
    private static void addNode(DecisionTreeNode decTreeNode, Element xmlParentElem) {
        // The xmlParentElem reference persists across recursive method calls, which allows children to add themselves to their parent.
        if (decTreeNode instanceof DecisionTreeLeafNode) {
            // <LeafNode class="..."/>
            // Generates the leaf node and adds it to its parent
            Element leafElem = new Element(N_LEAF_NODE);
            leafElem.setAttribute(A_CLASS, ((DecisionTreeLeafNode) decTreeNode).getLabelClass());
            xmlParentElem.addContent(leafElem);

        } else if (decTreeNode instanceof DecisionTree) {
            DecisionTree tree = (DecisionTree) decTreeNode;

            // <Node attributeIndex="...">
            // Generates the attribute element as an outer shell, holding all its attribute value elements (<IF value="...">). Within those live its children.
            Element nodeElem = new Element(N_NODE);
            nodeElem.setAttribute(A_ATTRIBUTE, String.valueOf(tree.getAttributeIndex()));

            // Iterate over all children of decTreeNode
            for (Map.Entry<String, DecisionTreeNode> split : tree.getSplits().entrySet()) {
                // <IF value="...">
                // Generates the attribute value link between decTreeNode and child,
                Element ifElem = new Element(N_IF);
                ifElem.setAttribute(A_VALUE, split.getKey());

                // <Node attributeIndex="..."> or <LeafNode class="..."/>
                // below the link, generate child and everything below
                addNode(split.getValue(), ifElem);

                // and add the link now holding the entire subtree to decTreeNode
                nodeElem.addContent(ifElem);
            }

            // Add decTreeNode to its own parent
            xmlParentElem.addContent(nodeElem);
        }
    }

}
