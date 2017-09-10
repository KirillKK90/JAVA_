import java.util.Iterator;

public class Deque<Item> implements Iterable<Item>
{
    private Node first = null;
    private Node last = null;
    private int sz = 0;

    private class Node
    {
        Item item;
        Node next;
        Node previous;
    }

    public Deque() { } // construct an empty deque

    public boolean isEmpty() // is the deque empty?
    {
        return sz == 0;
    }

    public int size() // return the number of items on the deque
    {
        return sz;
    }

    public void addFirst(Item item)
    {
        if (item == null)
        {
            throw new java.lang.IllegalArgumentException();
        }
        Node oldfirst = first;
        first = new Node();
        if (oldfirst != null)
        {
            oldfirst.previous = first;
        }
        first.item = item;
        first.next = oldfirst;
        first.previous = null;
        ++sz;
        if (sz == 1) last = first;
    }

    public void addLast(Item item)  // add the item to the end
    {
        if (item == null)
        {
            throw new java.lang.IllegalArgumentException();
        }
        Node oldlast = last;
        last = new Node();
        if (oldlast != null)
        {
            oldlast.next = last;
        }
        last.item = item;
        last.next = null;
        last.previous = oldlast;
        ++sz;
        if (sz == 1) first = last;
    }

    public Item removeFirst()                // remove and return the item from the front
    {
        if (sz == 0)
        {
            throw new java.util.NoSuchElementException();
        }
        Item item = first.item;
        if (first.next != null)
        {
            first = first.next;
            first.previous = null;
        }
        --sz;
        return item;
    }

    public Item removeLast()                 // remove and return the item from the end
    {
        if (sz == 0)
        {
            throw new java.util.NoSuchElementException();
        }
        Item item = last.item;
        if (last.previous != null)
        {
            last = last.previous;
            last.next = null;
        }
        --sz;
        return item;
    }

    private class ListIterator implements Iterator<Item>
    {
        private Node current = first;
        public boolean hasNext() { return current != null; }

        public void remove() /* not supported */
        {
            throw new java.lang.UnsupportedOperationException();
        }

        public Item next()
        {
            if (current == null)
            {
                throw new java.util.NoSuchElementException();
            }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    public Iterator<Item> iterator()         // return an iterator over items in order from front to end
    {
        return new ListIterator();
    }

    public static void main(String[] args)   // unit testing (optional)
    {
        Deque<Integer> d = new Deque<>();
        d.addFirst(77);
        int x = d.removeLast();
        System.out.format("int x = d.removeFirst(); : %d \n", x);
        System.out.format("Is empty ? %b \n", d.isEmpty());

        /* Deque<Integer> d = new Deque<>();
        System.out.format("Is empty ? %b \n", d.isEmpty());

        d.addFirst(1);
        d.addFirst(2);
        d.addFirst(3);
        d.addLast(4);
        for (int i : d) { System.out.println(i); }

        System.out.format("Size = number of elements in deck: %d \n", d.size());
        int x = d.removeFirst();
        System.out.format("removeFirst() called. \n");
        for (int i : d) { System.out.println(i); }
        x = d.removeLast();
        System.out.format("removeLast() called. \n");
        for (int i : d) { System.out.println(i); } */
    }
}
