import edu.princeton.cs.algs4.StdIn;

public class Permutation
{
    public static void main(String[] args)
    {
        int k = Integer.parseInt(args[0]);
        String str;
        RandomizedQueue<String> rq = new RandomizedQueue<>();

        while (!StdIn.isEmpty())
        {
            str = StdIn.readString();
            rq.enqueue(str);
        }

        int j = 0;
        for (String i : rq)
        {
            if (j == k)
            {
                break;
            }
            System.out.println(i);
            ++j;
        }
    };
}