import java.util.Random;

/**
 * This class is a red-black tree, a binary tree in which each node has been colored either red 
 * or black and maintains O(nlogn) for searches, insertions and deletions.
 * 
 * Left-leaning red-black trees follow these rules:
 * 1) A node is either red or black.
 * 2) The root is black.
 * 3) A black node may have at most one red child (which always is the left child).
 * 4) A red node cannot have a red child.
 * 5) Every path from a given node to the bottom of the tree contains the same number of black nodes.
 * 
 * 
 * @author Colin Monaghan
 * @param <K> the type of the Keys
 * @param <V> The type of the Value of the Keys
 */
public class RedBlackTree<K extends Comparable<K>,V> {
	
	//The possible color of Nodes
	public enum Color {RED, BLACK};
	private Node root;

	/**
	 * This is the constructor of the Red-Black Tree.
	 */
	public RedBlackTree() {
		root = null;
	}
	
	/**
	 * This method maps keys to values in the tree. 
	 *  
	 * @param key the key being added to the Red-Black Tree
	 * @param value the value of the key
	 */
	public void put(K key, V value) {
		root = findAndAdd(root, key, value);
		root.setColor(Color.BLACK);
	}
	
	/**
	 * This is the recursive helper function for the put() method.
	 * @param node the current node of the tree we have recursed to 
	 * @param key the key we are adding to the tree
	 * @param value the value of the key
	 * @return the Node of the tree we are in
	 */
	private Node findAndAdd(Node node, K key, V value) {
		// if the current "Node" is null, make new Node
		  if (node == null) {
		    return new Node(key, value);
		  }
		  // follow the BST rules
		  int cmp = key.compareTo(node.key);
		  if (cmp<0) node.left = findAndAdd(node.left, key, value);
		  else if (cmp>0) node.right = findAndAdd(node.right, key, value);
		  else node.value = value;
		  
		  // going up--fix coloring issues
		  if (isRed(node.right) && !isRed(node.left)) {
		    node = rotateLeft(node);
		  }
		  if (isRed(node.left) && isRed(node.left.left)) {
		    node = rotateRight(node);
		  }
		  if (isRed(node.right) && isRed(node.left)) {
		    node.flipColors();
		  }
		  // adjust the size & go home
		  node.recalcSize();
		  return node;
	}

	/**
	 * This method returns the value to which key maps.
	 * 
	 * @param key the key for the Red-Black Tree
	 * @return the value associated with the given key
	 */
	public V get(K key) {
		Node node = root;
		int cmp;
		while((cmp = key.compareTo(node.key)) != 0) {
			// follow the BST rules
			if (cmp<0 &&  node.left != null) node = node.left;
			else if (cmp>0 && node.right != null) node = node.right;
			else return null;
		}
		return node.value;
	}
	
	/**
	 * This method returns whether or not key is a valid key in the tree.
	 * 
	 * @param key the key being searched for
	 * @return whether or not key is a valid key in the tree
	 */
	public boolean contains(K key) {
		Node node = root;
		if(node == null) return false;
		int cmp;
		while((cmp = key.compareTo(node.key)) != 0) {
			// follow the BST rules
			if (cmp<0 &&  node.left != null) node = node.left;
			else if (cmp>0 && node.right != null) node = node.right;
			else return false;
		}
		return true;
	}

	/**
	 * This method removes a key and its value from the tree. 
	 * @param key the key being removed
	 * @return the value of the key
	 */
	public void delete(K key) {
		if(root != null && contains(key)) {
			//if it is the root (and the root is the only element)
			if(root.size == 1 && root.key.equals(key)) root = null;
			//if it is a red leaf
			//else if(simpleRemove(key));
			//else, we will delete it recursively 
			else {
				//change root to red and begin recursion
				root.setColor(Color.RED);
				Node node = deleteSpecialCase(root, (key.compareTo(root.key) > 1));
				if(node != null) root = node;
				root = findAndRemove(root, key);
				root.setColor(Color.BLACK);
			}
		}

	}
		
	/**
	 * This is the recursive helper function for the delete() method.
	 * @param node the current node of the tree we have recursed to 
	 * @param key the key we are removing from the tree
	 * @return the Node of the tree we are in
	 */
	private Node findAndRemove(Node node, K key) {
		//1. Go down the tree pushing the red with us
		//We may assume that the node we are currently in is red.
		//The task is to make sure that the node we’re about to jump into is red.
		int cmp = key.compareTo(node.key);
		
		if(cmp == 0) {
			//we are at the node, so we need to delete it now
			
			if(node.left != null && node.right != null) {
				//2 children
				Node switcher;
				if(new Random().nextInt(100) < 49) {
					//findPredecessor
					if(node.left.right == null) {
						switcher = node.left;
						switcher.right = node.right;
						switcher.recalcSize();
						switcher.setColor(Color.RED);
						return switcher;
					}
					else {
						switcher = predecessorDeleteHelper(node, node.left);
						switcher.right = node.right;
						switcher.left = node.left;
						switcher.recalcSize();
						switcher.setColor(Color.RED);
						return switcher;
					}
					
				}
				else {
					//findSuccessor
					if(node.right.left == null) {
						switcher = node.right;
						switcher.left = node.left;
						switcher.recalcSize();
						switcher.setColor(Color.RED);
						return switcher;
					}
					else {
						switcher = successorDeleteHelper(node, node.right);
						switcher.left = node.left;
						switcher.right = node.right;
						switcher.recalcSize();
						switcher.setColor(Color.RED);
						return switcher;
					}
				}
				
				//swap node and switcher, then node will only have 
			}
			else if(node.left != null && node.right == null) {
				//1 red left child, so promote the child to this spot
				return node.left;
			}
			else {
				//no children, so just let the tree end here
				return null;
			}
			
		}
		
		//we are not the right node, so we will keep looking
		//we can go right into a red node if we need to (this was made possible by the special case method)
		if(isRed(node.right) && cmp>0) {
			//go right
			Node temp = deleteSpecialCase(node.right, true);
			if(temp != null) node.right = temp;
			node.right = findAndRemove(node.right, key);

		}
		
		//if it has one child, that child must be a left red leaf.
		else if(isRed(node.left) && cmp<0) {
			//go left
			if(node.left != null) {
				Node temp = deleteSpecialCase(node.left, key.compareTo(node.left.key) > 1);
				if(temp != null) node.left = temp;
			}
			node.left = findAndRemove(node.left, key);

		}
		//if it has two black children, we color flip.
		else {
			node.flipColors();
			if (cmp<0) {
				//go left
				if(node.left != null) {
					Node temp = deleteSpecialCase(node.left, key.compareTo(node.left.key) > 1);
					if(temp != null) node.left = temp;
				}
				node.left = findAndRemove(node.left, key);
			}
			else {
				//go right
				Node temp = deleteSpecialCase(node.right, true);
				if(temp != null) node.right = temp;
				node.right = findAndRemove(node.right, key);
			}
		}
		
		//2. go back up and fix coloring issues
		if (isRed(node.right) && !isRed(node.left)) {
		    node = rotateLeft(node);
		}
		if (isRed(node.left) && isRed(node.left.left)) {
			node = rotateRight(node);
		}
		if (isRed(node.right) && isRed(node.left)) {
		    node.flipColors();
		}
		  
		// adjust the size & go home
		node.recalcSize();
		return node;
	}
	
	/**
	 * This method checks for a special case of the delete 
	 * function where a future node will have a left red child 
	 * and right black child then handles the situation.
	 * @param nextNode the node we are checking
	 */
	private Node deleteSpecialCase(Node nextNode, boolean right) {
		//When you’re about to jump into a red node, first check if it has a red left child and a black right child.
		if(isRed(nextNode.left) && isBlack(nextNode.right) && right) {
			//If so, do the rotation and flip now, before you enter that node.
			Node newRoot = rotateRight(nextNode);
			nextNode.flipColors();
			return newRoot;
		}
		return null;
	}
	
	/**
	 * This method helps the findAndDelete method recursively find the 
	 * predecessor of the node to be deleted, removes and returns the 
	 * predecessor (fixing the sizes of the nodes on the way back up). 
	 * 
	 * @param last the previous Node (pointing to this one)
	 * @param node the current Node
	 * @return the predecessor to the root node
	 */
	private Node predecessorDeleteHelper(Node last, Node node) {
		Node temp;
		if(node.right != null) {
			//go right
			temp = predecessorDeleteHelper(node, node.right);
			node.recalcSize();
			return temp;
		}
		else {
			if(node.left != null) last.right = node.left;
			else last.right = null;
			last.recalcSize();
			return node;
		}
	}
	
	/**
	 * This method helps the findAndDelete method recursively find the 
	 * successor of the node to be deleted, removes and returns the 
	 * successor (fixing the sizes of the nodes on the way back up). 
	 * 
	 * @param last the previous Node (pointing to this one)
	 * @param node the current Node
	 * @return the predecessor to the root node
	 */
	private Node successorDeleteHelper(Node last, Node node) {
		Node temp;
		if(node.left != null) {
			//go left
			temp = predecessorDeleteHelper(node, node.left);
			node.recalcSize();
			return temp;
		}
		else {
			if(node.right != null) last.left = node.right;
			else last.left = null;
			last.recalcSize();
			return node;
		}
	}
	
	/**
	 * This method returns the number of elements in the Red-Black Tree
	 * 
	 * @return the number of elements in the Red-Black Tree
	 */
	public int size() {
		return root.size;
	}
	
	/**
	 * This method returns true if the structure is empty, in constant time.
	 * @return true if the structure is empty
	 */
	public boolean isEmpty() {
		return root == null;
	}
	
	/**
	 * This method returns the first key (according to their order).
	 * @return the first key (according to their order)
	 */
	public K getMinKey() {
		if(isEmpty()) return null;
		Node node = root;
		while(node.left != null) node = node.left;
		return node.key;
	}
	
	/**
	 * This method returns the last key (according to their order).
	 * @return the last key (according to their order)
	 */
	public K getMaxKey() {
		if(isEmpty()) return null;
		Node node = root;
		while(node.right != null) node = node.right;
		return node.key;
	}
	
	/**
	 * This method returns the key before the given one 
	 * (or null if the given key was the first, or if the 
	 * given key is not in the tree).
	 * @param key the input key
	 * @return the key before the input key (according to their order)
	 */
	public K findPredecessor(K key) {
		Node node = root;
		Node right = null;
		while(true) {
			//if we reached the bottom of the tree, we need to return null (it is not in there)
			if(node == null) return null;
			int cmp = key.compareTo(node.key);
			if(cmp > 0) {
				//go right
				right = node;
				node = node.right;
				
			}
			else if(cmp < 0) {
				//go left
				node = node.left;
			}
			else {
				//they are the same key
				//If a node has a left child, its predecessor is obtained by traveling left once, and then right as much as possible.
				if(node.left != null) {
					node = node.left;
					while(node.right != null) node = node.right;
					return node.key;
				}
				//Otherwise, it is the most recent ancestor from which we traveled right.
				else {
					if(right != null) return right.key;
					else return null;
				}
			}
		}
	}
	
	/**
	 * This method returns the key after the given one 
	 * (or null if the given key was the last, or if the 
	 * given key is not in the tree).
	 * @param key the input key
	 * @return the key after the input key (according to their order)
	 */
	public K findSucessor(K key){
		Node node = root;
		Node left = null;
		while(true) {
			//if we reached the bottom of the tree, we need to return null (it is not in there)
			if(node == null) return null;
			int cmp = key.compareTo(node.key);
			if(cmp > 0) {
				//go right
				node = node.right;
				
			}
			else if(cmp < 0) {
				//go left
				left = node;
				node = node.left;
			}
			else {
				//they are the same key
				//If a node has a right child, its successor is obtained by traveling right once, and then left as much as possible.
				if(node.right != null) {
					node = node.right;
					while(node.left != null) node = node.left;
					return node.key;
				}
				//Otherwise, it is the most recent ancestor from which we traveled left.
				else {
					if(left != null) return left.key;
					else return null;
				}
			}
		}
	}
	
	
	/**
	 * This method determines the key’s rank.
	 * @param key the key we seek to know the rank of
	 * @return the rank of the inputed key
	 */
	public int findRank(K key) {
		int rank = 0;
		Node node = root;
		while(node != null && !node.key.equals(key)) {
			int cmp = node.key.compareTo(key);
			if(cmp < 0) {
				//go right
				if(node.left != null) rank += node.left.size+1;
				else rank += 1;
				node = node.right;
			}
			else {
				//go left
				node = node.left;
			}
		}
		if(node != null) {
			if(node.left != null) rank += node.left.size;
			return rank;
		}
		else return -1;
	}
	/**
	 * This method returns the key with integer rank rank.
	 * @param rank the rank of the desired key
	 * @return the key with the desired rank
	 */
	public K select(int rank) {
		Node node = root;
		int leftSize;
		
		 while(true) {
			//if we are at the bottom of the tree and haven't found it, return null
			if(node == null) return null;
			
			//update the leftSize variable
			if(node.left != null) leftSize = node.left.size;
			else leftSize = 0;
			
			//go right
			if(rank > leftSize) {
				rank -= leftSize+1;
				node = node.right;
			}
			//go left
			else if(rank < leftSize)  node = node.left;
			//we found it
			else return node.key;
			
		}
		
	}
	
	/**
	 * This method performs the rotation method on the inputed 
	 * node and its right child.
	 * @param node the parent node
	 * @return the new parent node
	 */
	private Node rotateLeft(Node parent) {
		//move their positions
		Node newParent = parent.right;
		parent.right = newParent.left;
		newParent.left = parent;
		//swap their colors
		newParent.setColor(parent.color);
		parent.setColor(Color.RED);
		//recalculate sizes
		parent.recalcSize();
		newParent.recalcSize();
		return newParent;
	}
	
	/**
	 * This method performs the rotation method on the inputed 
	 * node and its left child.
	 * @param node the parent node
	 * @return the new parent node
	 */
	private Node rotateRight(Node parent) {
		//move their positions
		Node newParent = parent.left;
		parent.left = newParent.right;
		newParent.right = parent;
		//swap their colors
		newParent.setColor(parent.color);
		parent.setColor(Color.RED);
		//recalculate sizes
		parent.recalcSize();
		newParent.recalcSize();
		return newParent;
	}
	
	/**
	 * This method checks if an inputed Node is red. If the inputed 
	 * node is null, or if the node is not red, it will return false. 
	 * @param node the node we are checking
	 * @return true if the inputed node is red
	 */
	private boolean isRed(Node node) {
		return (node != null && node.color.equals(Color.RED));
	}
	
	/**
	 * This method checks if an inputed Node is black. If the inputed 
	 * node is null, or if the node is not black, it will return false. 
	 * @param node the node we are checking
	 * @return true if the inputed node is black
	 */
	private boolean isBlack(Node node) {
		return (node != null && node.color.equals(Color.BLACK));
	}
	
	/**
	 * This method helps recursively print out the tree for visual examination.
	 * @param root the root of the tree (or subtree)
	 * @param space the distance between levels
	 */
	private void print2DUtil(Node root, int space)  
	{  
	    // Base case  
	    if (root == null)  
	        return;  
	  
	    // Increase distance between levels  
	    space += 4;  
	  
	    // Process right child first  
	    print2DUtil(root.right, space);  

	    
	    // Print current node after space  
	    // count  

	    System.out.print("\n");  
	    for (int i = 4; i < space+4; i++)  
	        System.out.print(" ");  
	    System.out.print(root.left+ "\n"); 
	  
	    // Process left child  
	    print2DUtil(root.left, space);  
	}  
	  
	/**
	 * This method recursively print out the tree for visual examination.
	 */
	public void print2D()  {  
	    // Pass initial space count as 0  
	    print2DUtil(root, 0);  
	}
	
	/**
	 * A node of the red-black tree. It has pointers to its two children, a key, a value, a color and a size. 
	 * @author Colin Monaghan
	 */
	private class Node {
		

		private K key;
		private V value;
		private Node left;
		private Node right;
		private Color color;
		private int size;
		
		/**
		 * The constructor for the Node.
		 * @param key the key to set the Node's key to
		 * @param value the value to set the Node's value to
		 */
		private Node(K key, V value) {
			this.key = key;
			this.value = value;
			left = null;
			right = null;
			color = Color.RED;
			size = 1;
		}
		
		/**
		 * This method overrides the inherited toString method and 
		 * changes it into a meaningful output of the form "(key, value [color], size)".
		 */
		@Override
		public String toString() {
			return "("+key.toString()+", value:"+value.toString()+" ["+color+"], size: "+size+")";
			
		}
		
		/**
		 * This method sets the Node to the inputed color.
		 * @param newColor the new color of the Node
		 */
		public void setColor(Color newColor) {
			this.color = newColor;
		}
	
		/**
		 * This method recalculates the size of the tree 
		 * beneath this node (including this Node as the 
		 * root of the tree).
		 */
		public void recalcSize() {
			size = 1;
			if(left != null)  size += left.size;
			if(right != null) size += right.size;
		}
	
		/**
		 * This method changes the color of the Node and its two children. 
		 * It should only be called when the parent is the opposite color of both its children.
		 */
		public void flipColors() {
			if(isBlack(this)) {
				//we are a black node with 2 red children
				this.setColor(Color.RED);
				left.setColor(Color.BLACK);
				right.setColor(Color.BLACK);
			}
			else {
				//we are a red node with 2 black children
				this.setColor(Color.BLACK);
				left.setColor(Color.RED);
				right.setColor(Color.RED);	
			}
		}
	}
}


