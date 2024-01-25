# Paralle Primes
Finds all the primes between 1 and 10^8 and prints some data about them:

## How to Run
Make sure you have a Java compiler installed.

- Make sure you have navigated to the directory storing the file Primes.java.
- To compile: type "javac Primes.java" and press enter.
- To run: type "java Primes" and press enter. Then, enter the upper limit to check with no spaces 
or extraneous characters and press enter.

Output format: \<time taken in nanoseconds\> \<number of primes found\> \<sum of primes found\>
               \<top ten primes found in ascending order\>

## Summary of Approach
Each thread uses the same isPrime() method concurrently. An atomic counter variable is used for 
threads to obtain values to test. A treeset holds the top ten primes found in order.

### Process of a single thread:
- Obtain a number to test from the atomic counter variable, then increment the variable's value. 
This operation is a critical section: No two or more threads can use this variable at the same time.
- Call the isPrime() method to determine the number's primality. 
- If the number is prime: 
    - increment a local object storing information about the number of primes found and their sum, 
    - and add the value to a shared tree set if it is one of the ten maximum primes found so far. 
    Note: this is a critical section since the set is shared by all threads.
- Repeat the steps above until the number obtained is larger than the upper limit.
- Return the object containing this thread's number of and sum of the primes it found.

### After all Threads Complete:
- Each thread's individual numPrimes tracker can be added together to obtain the total number of 
primes found. The same can be done to find the total sum of all the primes.
- The tree set stores the top ten largest primes found.

## Correctness and Efficiency

### Correctness: 
Correct results output accurate values for the amount of primes found and their sum.
Correct execution results in no unexpected behavior when interacting with the shared counter 
or the shared set storing the top ten maximum primes.

This is achieved by using an AtomicInteger object as the shared counter and a synchronized 
method to update the set. This prevents multiple threads from accessing the shared resources at 
the same time, which can cause unpredictable behavior (such as numbers being skipped in the 
counter).

### Efficiency:
The program should run fairly quickly for large inputs and should utilize concurrency 
whenever possible. Sequential operations should be minimized.

Locking mechanisms are only necessary when threads want to read from and/or write to shared 
resources.
Shared resources in this implementation are the counter and the set storing the maximum primes.
All computations and actions that don't involve these shared resources, like determining a number's 
primality and updating the threads own local tracker variables, are handled concurrently.

## Experimental Evaluation

### Versus Sequential Implementation:
I implemented a sequential approach using the same isPrime() method to compare against my 
concurrent approach. I found that my concurrent approach took, on average, about 17% of the time 
my sequential approach took.

### Testing with Different Upper Limits:
I tested my algorithm using some smaller and larger upper limits to ensure correctness for 
different inputs.
