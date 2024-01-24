// todo author and problem statement comment
// Finds and prints all primes on an input-defined range.

import java.lang.Math;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Primes
{
    private static final int NUM_THREADS = 8;
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

    public static void main(String [] args)
    {
        // Upper limit input validation.
        int upLim = (int) Math.floor(in.nextDouble());

        long startSeq = System.nanoTime();
        int numPrimesSeq = 0;
        long sumPrimesSeq = 0;

        for (int i = 0; i < upLim; i++)
        {
            if (isPrime(i))
            {
                numPrimesSeq++;
                sumPrimesSeq += i;
            }
        }
        long endSeq = System.nanoTime();

        System.out.println((endSeq - startSeq) + " " + numPrimesSeq + " " + sumPrimesSeq);

        // Threaded approach.
        long start = System.nanoTime();

        // Take care of edge case.
        if (upLim < 2)
            System.out.println("There are no primes less than 2!");

        // Will be used to increment through odd numbers.
        AtomicInteger counter = new AtomicInteger(3);

        // List of tasks for threads to do.
        ArrayList<FutureTask<PrimesInfo>> tasks = new ArrayList<FutureTask<PrimesInfo>>();
        for (int i = 0; i < NUM_THREADS; i++)
        {
            tasks.add(new FutureTask<>(new Callable<PrimesInfo>() {

                // Maintains thread-specific numPrimes and sumPrimes trackers.
                public PrimesInfo call()
                {
                    PrimesInfo infoP = new PrimesInfo();

                    int cur = counter.getAndAdd(2);
                    while (cur <= upLim)
                    {
                        if (isPrime(cur))
                        {
                            infoP.numPrimes++;
                            infoP.sumPrimes += cur;
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
        for (int i = 0; i < NUM_THREADS; i++)
        {
            try 
            {
                // Increment the total amount and sum found.
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
    }
}

// Stores information about a group of primes seen by a single thread.
class PrimesInfo
{
    public int numPrimes = 0;
    public long sumPrimes = 0;
}