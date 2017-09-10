import java.util.Iterator;
import edu.princeton.cs.algs4.StdRandom;


public class RandomizedQueue<Item> implements Iterable<Item>
{
    private Item[] q;
    private int N;
    private int head;
    private int tail;

    public RandomizedQueue()                 // construct an empty randomized queue
    {
        q = (Item[]) new Object[1];
        head = 0;
        tail = 0;
        N = 0;
    }

    public boolean isEmpty()                 // is the queue empty?
    { return N == 0; }
    public int size()                        // return the number of items on the queue
    { return N; }

    private void resize(int capacity)
    {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < N; i++)
            copy[i] = q[head + i];
        q = copy;
        head = 0;
        tail = N;
    }

    public void enqueue(Item item)           // add the item
    {
        if (item == null)
        {
            throw new java.lang.IllegalArgumentException();
        }
        if (tail == q.length) resize(2 * q.length);
        q[tail++] = item;
        N++;
    }

    public Item dequeue()                    // remove and return a random item
    {
        if (N == 0)
        {
            throw new java.util.NoSuchElementException();
        }
        int rndIdx = StdRandom.uniform(N);
        Item item = q[head + rndIdx];
        q[head + rndIdx] = q[tail - 1];
        q[tail - 1] = null;
        --tail;
        --N;
        if (N > 0 && N == q.length/4) resize(q.length/2);
        return item;
    }

    public Item sample()                     // return (but do not remove) a random item
    {
        if (N == 0)
        {
            throw new java.util.NoSuchElementException();
        }
        int randNum = StdRandom.uniform(N);
        return q[head + randNum];
    }

    public Iterator<Item> iterator()         // return an independent iterator over items in random order
    { return new ReverseArrayIterator(); }

    private class ReverseArrayIterator implements Iterator<Item>
    {
        private int cnt = 0;
        private final int n0;
        private final int[] ids;

        ReverseArrayIterator()
        {
            ids = new int[N];
            for (int i = 0; i < N; ++i)
            {
                ids[i] = i;
            }

            // shuffle array in-place
            for (int i = 0; i < N; ++i) {
                int r = i + StdRandom.uniform(N-i);     // between i and n-1
                int temp = ids[i];
                ids[i] = ids[r];
                ids[r] = temp;
            }
            n0 = N;
        }

        public boolean hasNext() { return cnt < n0 && N > 0; }
        public void remove() /* not supported */
        {
            throw new java.lang.UnsupportedOperationException();
        }

        public Item next()
        {
            if (N == 0 || n0 == 0)
            {
                throw new java.util.NoSuchElementException();
            }
            if (cnt == n0 || cnt > N)
            {
                throw new java.util.NoSuchElementException();
            }

            return q[ids[cnt++]];
        }
    }

    public static void main(String[] args) // unit testing (optional)
    {
        RandomizedQueue<Integer> rq = new RandomizedQueue<>();
        rq.enqueue(2);
        rq.enqueue(3);
        rq.enqueue(7);
        rq.enqueue(11);

        /* System.out.println(rq.dequeue());
        System.out.println(rq.dequeue());
        System.out.println(rq.dequeue());
        System.out.println(rq.dequeue());

         System.out.println(rq.size());
        System.out.println(rq.sample());
        System.out.println(rq.sample()); */

        for (int i : rq)
        {
            System.out.println(i);
        }
    }
}
