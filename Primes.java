// todo author and problem statement comment
// Finds and prints all primes on a program-specified range.

import java.util.Scanner;
import java.util.ArrayList;
import java.util.TreeSet;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Primes
{
    private static final int NUM_THREADS = 8;
    private static final int UPPER_LIMIT = 100000000;
    // Amount of maximum primes to track.
    private static final int AMT_LARGEST = 10;

    // Determines the primality of an odd number n.
    private static boolean isPrime(int n)
    {
        // Handle even numbers.
        if (n < 2 || n % 2 == 0)
            return (n == 2);

        // Check remaining odd numbers up to sqrt(n).
        for (int i = 3; (i * i) <= n; i += 2)
        {
            if (n % i == 0)
                return false;
        }

        return true;
    }

    // Updates the set storing the 10 maximum primes.
    private synchronized static void updateMaxes(TreeSet<Integer> maxes, int n)
    {
        // Add the value if the set has room or the value is larger than the smallest in the set.
        if (maxes.size() < AMT_LARGEST)
        {
            maxes.add(n);
        }
        else if (maxes.first() < n)
        {
            maxes.pollFirst();
            maxes.add(n);
        }
    }

    public static void main(String [] args)
    {
        long start1 = System.nanoTime();

        // Safety check for valid upper limit.
        if (UPPER_LIMIT < 2)
            System.out.println("There are no primes less than 2!");

        // Will be used to increment through odd numbers.
        AtomicInteger counter = new AtomicInteger(3);
        // Holds a group of the largest primes found.
        TreeSet<Integer> maxPrimes = new TreeSet<>();
        maxPrimes.add(2); // We know the only even prime is in the range.

        // Initialize list of tasks for threads to do.
        ArrayList<FutureTask<PrimesInfo>> tasks = new ArrayList<FutureTask<PrimesInfo>>();
        for (int i = 0; i < NUM_THREADS; i++)
        {
            tasks.add(new FutureTask<>(new Callable<PrimesInfo>() {

                public PrimesInfo call()
                {
                    // Maintains thread-specific numPrimes and sumPrimes trackers.
                    PrimesInfo infoP = new PrimesInfo();

                    int cur = counter.getAndAdd(2);
                    while (cur <= UPPER_LIMIT)
                    {
                        if (isPrime(cur))
                        {
                            infoP.numPrimes++;
                            infoP.sumPrimes += cur;
                            updateMaxes(maxPrimes, cur);
                        }
                        cur = counter.getAndAdd(2);
                    }
        
                    return infoP;
                }
            }));
        }

        // Initialize and start threads with callable tasks.
        Thread [] threads = new Thread [NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++)
        {
            threads[i] = new Thread(tasks.get(i));
            threads[i].start();
        }

        // Wait for all threads to die.
        for (Thread t : threads)
        {
            try {
                t.join();
            } catch(InterruptedException ie) {
                System.out.println(ie.getMessage());
            }
        }

        // Initialize result variables on the basis that 2 is included in the range.
        int totalNumPrimes = 1;
        long totalSumPrimes = 2;

        // Increment the result variables by each thread's individual results.
        for (int i = 0; i < NUM_THREADS; i++)
        {
            try 
            {
                totalNumPrimes += tasks.get(i).get().numPrimes;
                totalSumPrimes += tasks.get(i).get().sumPrimes;
            } 
            catch (Exception ie) 
            {
                System.out.println(ie.getMessage());
            }
        }

        long end = System.nanoTime();
        System.out.println((end - start) + " " + totalNumPrimes + " " + totalSumPrimes);

        // Print the maximum primes found.
        while (!maxPrimes.isEmpty())
            System.out.print(maxPrimes.pollFirst() + " ");
        System.out.println();
    }
}

// Stores information about a group of prime numbers.
class PrimesInfo
{
    public int numPrimes = 0;
    public long sumPrimes = 0;
}
