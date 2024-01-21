// todo author and problem statement comment

import java.lang.Math;

public class Primes
{
    // Upper limit.
    private double upLim;

    // Returns true if n is prime.
    // todo consider improvement.
    static boolean isPrime(double n)
    {
        // Base case.
        if (n == 2) return true;

        double sqrt = Math.sqrt(n);
        for (double i = 2; i <= sqrt; i++)
        {
            if (n % i == 0)
                return false;
        }

        return true;
    }

    // Main method that prints the data.
    public static void main(String [] args)
    {
        for (double i = 2; i < 20; i++)
        {
            if (isPrime(i))
                System.out.println(" " + i);
        }
    }
}