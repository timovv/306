package team02.project.algorithm;

import team02.project.algorithm.solnspace.PartialSolution;
import team02.project.algorithm.solnspace.SolutionSpace;
import team02.project.algorithm.solnspace.ao.AOSolutionSpace;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class WorkStealingParallelBBAlgorithm implements SchedulingAlgorithm {

    private static final int UPDATE_BOUND_INTERVAL = 10000;
    private final int coreCount;

    private List<Worker> allWorkers = new ArrayList<>();
    private Random random = new Random();

    public WorkStealingParallelBBAlgorithm(int coreCount) {
        this.coreCount = coreCount;
    }

    @Override
    public Schedule calculateOptimal(SchedulingContext ctx) {
        SolutionSpace solutionSpace = new AOSolutionSpace();

        // use a simple version of A* to expand until we have enough nodes
        PriorityQueue<PartialSolution> queue = new PriorityQueue<>(Comparator.comparing(PartialSolution::getEstimatedFinishTime));
        queue.add(solutionSpace.getRoot(ctx));

        while(queue.size() < coreCount) {
            PartialSolution solution = queue.poll();
            if(solution == null) {
                // couldn't find a solution. shouldn't happen if solution space is set up properly
                throw new RuntimeException("Could not find solution?");
            }

            Collection<PartialSolution> children = solution.expand();
            for(PartialSolution child : children) {
                if(child.isComplete()) {
                    // we're actually done here
                    return child.makeComplete();
                }

                queue.offer(child);
            }
        }

        for(int i = 0; i < coreCount - 1; ++i) {
            allWorkers.add(new Worker(queue.poll()));
        }

        allWorkers.add(new Worker(queue));

        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < coreCount - 1; ++i) {
            Thread thread = new Thread(allWorkers.get(i));
            threads.add(thread);
            thread.start();
        }

        allWorkers.get(coreCount - 1).run();

        for(Thread thread : threads) {
            try {
                thread.join();
            } catch(InterruptedException e) {
                // lol
            }
        }

        Schedule best = null;
        int bestFinishTime = Integer.MAX_VALUE;
        // find the best
        for(Worker worker : allWorkers) {
            if(worker.localUpperBound <= bestFinishTime && worker.currentBestSchedule != null) {
                best = worker.currentBestSchedule;
                bestFinishTime = worker.localUpperBound;
            }
        }

        return best;
    }

    private class Worker implements Runnable {

        private final Deque<PartialSolution> localQueue;

        public Worker(PartialSolution... roots) {
            this.localQueue = new ConcurrentLinkedDeque<>(Arrays.asList(roots));
        }

        public Worker(Collection<PartialSolution> roots) {
            this.localQueue = new ConcurrentLinkedDeque<>(roots);
        }

        private volatile int localUpperBound = Integer.MAX_VALUE; // volatile so that multithread access is OK
        private Schedule currentBestSchedule = null;
        private int updateCount = random.nextInt(UPDATE_BOUND_INTERVAL); // randomise this so that different threads update at different times

        @Override
        public void run() {
            processAll();

            // our queue is empty, let's steal some work
            PartialSolution stolen;
            while((stolen = stealWorkItem()) != null) {
                localQueue.offerLast(stolen);
                processAll();
            }

            // all queues empty so time to exit
        }

        private void processAll() {
            while(true) {
                PartialSolution next = localQueue.pollFirst();
                if(next == null) {
                    break;
                }

                if(updateCount >= UPDATE_BOUND_INTERVAL) {
                    // fetch bounds from the other threads
                    for(Worker worker : allWorkers) {
                        // one of the workers will be this but this condition won't hold for that.
                        if(worker.localUpperBound < this.localUpperBound) {
                            // update the bound
                            // it's ok if the worker changed their bound since the check above since the bound
                            // only decreases over time
                            this.localUpperBound = worker.localUpperBound;
                        }
                    }

                    // reset update count
                    updateCount = 0;
                }

                process(next);

                ++updateCount;
            }
        }

        public PartialSolution stealTask() {
            return localQueue.pollLast();
        }

        private void process(PartialSolution soln) {
            if(soln.isComplete()) {
                // try update the bound, if it really is better
                Schedule schedule = soln.makeComplete();
                int finishTime = schedule.getFinishTime();
                if(finishTime < localUpperBound) { // update upper bound
                    localUpperBound = finishTime;
                    currentBestSchedule = schedule;
                }
            } else {
                // expand and find children
                for(PartialSolution partialSolution : soln.expand()) {
                    if(partialSolution.getEstimatedFinishTime() < localUpperBound) {
                        localQueue.offerLast(partialSolution);
                    }
                }
            }
        }
    }

    private PartialSolution stealWorkItem() {
        // try and steal from a random queue
        int i = random.nextInt(allWorkers.size());
        for(int j = 0; j < allWorkers.size(); ++j) {
            PartialSolution stolen = allWorkers.get((i + j) % allWorkers.size()).stealTask();
            if(stolen != null) {
                return stolen;
            }
        }

        return null;
    }
}
