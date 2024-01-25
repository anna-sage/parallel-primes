// todo author and problem statement comment
// Finds and prints all primes on an input-defined range.

import java.util.Scanner;
import java.util.ArrayList;
import java.util.TreeSet;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Primes
{
    private static final int NUM_THREADS = 8;
    // Amount of maximum primes to track.
    private static final int AMT_LARGEST = 10;

    static Scanner in = new Scanner(System.in);

    // Determines the primality of n.
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
    // Any n passed in will be larger than the minimum number in the set?? (todo)
    private synchronized static void updateMaxes(TreeSet<Integer> maxes, int n)
    {
        if (maxes.size() == AMT_LARGEST)
            maxes.pollFirst();

        maxes.add(n);
    }

    public static void main(String [] args)
    {
        // Upper limit input validation.
        int upLim = (int) Math.floor(in.nextDouble());

        // Threaded approach.
        long start = System.nanoTime();

        // Take care of edge case. (todo)
        if (upLim < 2)
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
                    while (cur <= upLim)
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

        long end = System.nanoTime(); // (todo) right end location?
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