package undc.general;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Represents an AVL Tree.
 */
public class AVL<T extends Comparable<? super T>> {
    static class Node<T extends Comparable<? super T>> {
        T data;
        Node<T> left;
        Node<T> right;
        int height;
        int balanceFactor;

        /**
         * Create an AVLNode with the given data.
         *
         * @param data the data stored in the new node
         */
        public Node(T data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Node containing: " + data;
        }
    }

    private Node<T> root;
    private int size;

    public AVL() {

    }

    /**
     * Adds the element to the tree.
     * @param data the data to add
     * @throws java.lang.IllegalArgumentException if data is null
     */
    public void add(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null.");
        }
        root = addHelper(root, data);
        size++;
    }

    /**
     * Helper method to add a node to the tree.
     * @param rootNode Root of the node to add to
     * @param data Data to add
     * @return Returns the root node with the new node added (or not, if it already exists)
     */
    private Node<T> addHelper(Node<T> rootNode, T data) {
        if (rootNode == null) {
            rootNode = new Node<>(data);
            return rootNode;
        }
        int diff = data.compareTo(rootNode.data);
        if (diff < 0) {
            rootNode.left = addHelper(rootNode.left, data);
        } else {
            rootNode.right = addHelper(rootNode.right, data);
        }
        updateNode(rootNode);

        //rotate if necessary
        if (rootNode.balanceFactor > 1) {
            rootNode = rotateRight(rootNode);
        } else if (rootNode.balanceFactor < -1) {
            rootNode = rotateLeft(rootNode);
        }
        return rootNode;
    }

    /**
     * Method to rotate a tree right.
     * @param rootNode Root of the tree to rotate
     * @return Returns the new tree after rotation
     */
    private Node<T> rotateRight(Node<T> rootNode) {
        //double rotation
        if (rootNode.left.balanceFactor < 0) {
            rootNode.left = rotateLeft(rootNode.left);
        }
        Node<T> newRoot;
        newRoot = rootNode.left;
        rootNode.left = newRoot.right;
        newRoot.right = rootNode;
        updateNode(rootNode);
        updateNode(newRoot);
        return newRoot;
    }

    /**
     * Method to rotate a tree left.
     * @param rootNode Root of the tree to rotate
     * @return Returns the new tree after rotation
     */
    private Node<T> rotateLeft(Node<T> rootNode) {
        if (rootNode.right.balanceFactor > 0) {
            rootNode.right = rotateRight(rootNode.right);
        }
        Node<T> newRoot;
        newRoot = rootNode.right;
        rootNode.right = newRoot.left;
        newRoot.left = rootNode;
        updateNode(rootNode);
        updateNode(newRoot);
        return newRoot;
    }

    /**
     * Method to update the height and balance factor of a node.
     * @param node Node to update
     */
    private void updateNode(Node<T> node) {
        int heightLeft = (node.left == null) ? -1 : node.left.height;
        int heightRight = (node.right == null) ? -1 : node.right.height;
        node.height = Math.max(heightLeft, heightRight) + 1;
        node.balanceFactor = heightLeft - heightRight;
    }

    /**
     * Removes and returns the element from the tree matching the given parameter.
     * @param data the data to remove
     * @return the data that was removed
     * @throws java.lang.IllegalArgumentException if the data is null
     * @throws java.util.NoSuchElementException   if the data is not found
     */
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null.");
        }
        Node<T> foundNode = new Node<>(null);
        root = removeHelper(root, data, foundNode);
        size--;
        if (size > 0) {
            updateNode(root);
        }
        return foundNode.data;
    }

    /**
     * Helper method to remove a node from a tree.
     * @param rootNode Root of the tree to search in
     * @param data Data to remove
     * @param found Dummy node to store the data that was found
     * @return Returns the new tree with the data removed
     */
    private Node<T> removeHelper(Node<T> rootNode, T data, Node<T> found) {
        if (rootNode == null) {
            throw new NoSuchElementException("Data cannot be found.");
        }
        int diff = data.compareTo(rootNode.data);
        // if less than the current node, then continue searching left. Otherwise, search right
        if (diff < 0) {
            rootNode.left = removeHelper(rootNode.left, data, found);
        } else if (diff > 0) {
            rootNode.right = removeHelper(rootNode.right, data, found);
        } else { // if the data has been found, then remove
            found.data = rootNode.data;
            // if there is <=1 children, then move it up
            if (rootNode.left == null) {
                return rootNode.right;
            } else if (rootNode.right == null) {
                return rootNode.left;
            }
            // two children, find and remove the predecessor.
            rootNode.left = predecessorHelper(rootNode, rootNode.left);
        }
        updateNode(rootNode);
        if (rootNode.balanceFactor > 1) {
            rootNode = rotateRight(rootNode);
        } else if (rootNode.balanceFactor < -1) {
            rootNode = rotateLeft(rootNode);
        }
        return rootNode;
    }

    /**
     * Method to find and replace a node's value with the predecessor.
     * @param node Node to replace the value of
     * @param searchNode Node used to search for the predecessor
     * @return Returns the new search node after removing the predecessor
     */
    private Node<T> predecessorHelper(Node<T> node, Node<T> searchNode) {
        //predecessor
        if (searchNode.right == null) {
            //swap values
            node.data = searchNode.data;
            //remove predecessor
            return searchNode.left;
        } else {
            searchNode.right = predecessorHelper(node, searchNode.right);
            updateNode(searchNode);
            return searchNode;
        }
    }

    /**
     * Returns the element from the tree matching the given parameter.
     * @param data the data to search for in the tree
     * @return the data in the tree equal to the parameter
     * @throws java.lang.IllegalArgumentException if data is null
     * @throws java.util.NoSuchElementException   if the data is not in the tree
     */
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null.");
        }
        return getHelper(root, data);
    }

    /**
     * Helper method to find and retrieve a data.
     * @param rootNode Root of the tree to search in
     * @param data The data to find
     * @return Returns the data that is found
     */
    private T getHelper(Node<T> rootNode, T data) {
        if (rootNode == null) {
            throw new NoSuchElementException("Data cannot be found.");
        }
        int diff = data.compareTo(rootNode.data);
        if (diff == 0) {
            return rootNode.data;
        } else if (diff < 0) {
            return getHelper(rootNode.left, data);
        } else {
            return getHelper(rootNode.right, data);
        }
    }

    /**
     * Returns whether or not data matching the given parameter is contained within the tree.
     * @param data the data to search for in the tree
     * @return true if the parameter is contained within the tree, false otherwise
     * @throws java.lang.IllegalArgumentException if data is null
     */
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null.");
        }
        try {
            get(data);
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    /**
     * Generate an in-order traversal of the tree.
     * @return the inorder traversal of the tree
     */
    public ArrayList<T> inOrder() {
        ArrayList<T> list = new ArrayList<>();
        orderHelper(1, list, root);
        return list;
    }

    /**
     * Helper method to produce an order traversal list.
     * @param order Order to use (0 for pre, 1 for in, 2 for post)
     * @param list List to add nodes to
     * @param rootNode Root of the tree to traverse
     */
    private void orderHelper(int order, ArrayList<T> list, Node<T> rootNode) {
        if (rootNode == null) {
            return;
        }
        if (order == 0) {
            list.add(rootNode.data);
        }
        orderHelper(order, list, rootNode.left);
        if (order == 1) {
            list.add(rootNode.data);
        }
        orderHelper(order, list, rootNode.right);
        if (order == 2) {
            list.add(rootNode.data);
        }
    }


    /**
     * Returns the height of the root of the tree.
     * @return the height of the root of the tree, -1 if the tree is empty
     */
    public int height() {
        return (root == null) ? -1 : root.height;
    }

    /**
     * Clears the tree.
     */
    public void clear() {
        size = 0;
        root = null;
    }

    /**
     * Returns the root of the tree.
     * @return the root of the tree
     */
    public Node<T> getRoot() {
        return root;
    }

    /**
     * Returns the size of the tree.
     * @return the size of the tree
     */
    public int size() {
        return size;
    }
}