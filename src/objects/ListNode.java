package src.objects;

class ListNode {
    int val;
    ListNode next;

    public ListNode(int val) {
        this.val = val;
    }

    // Insert a new node with the given value after this node
    public void insertAfter(int value) {
        ListNode newNode = new ListNode(value);
        newNode.next = this.next;
        this.next = newNode;
    }

    // Delete the next node in the linked list
    public void deleteNext() {
        if (next != null) {
            next = next.next;
        }
    }

    // Find the length of the linked list starting from this node
    public int findLength() {
        int length = 0;
        ListNode current = this;

        while (current != null) {
            length++;
            current = current.next;
        }

        return length;
    }

    // Optional: Add getters, setters, or other methods as needed
}
