package src.objects;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    public TreeNode(int val) {
        this.val = val;
    }

    // Insert a value into the binary tree
    public void insert(int value) {
        if (value < this.val) {
            if (left == null) {
                left = new TreeNode(value);
            } else {
                left.insert(value);
            }
        } else {
            if (right == null) {
                right = new TreeNode(value);
            } else {
                right.insert(value);
            }
        }
    }

    // Search for a value in the binary tree
    public boolean search(int value) {
        if (value == this.val) {
            return true;
        } else if (value < this.val && left != null) {
            return left.search(value);
        } else if (value > this.val && right != null) {
            return right.search(value);
        }
        return false;
    }

    // In-order traversal of the binary tree
    public void inOrderTraversal() {
        if (left != null) {
            left.inOrderTraversal();
        }
        System.out.print(val + " ");
        if (right != null) {
            right.inOrderTraversal();
        }
    }

    // Optional: Add getters, setters, or other methods as needed
}
