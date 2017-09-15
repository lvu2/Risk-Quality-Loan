Without pruning my accuracy for the prune_test.txt is .66258.
After implementing various pruning methods, my new accuracy is 0.71779.
The various pruning techiniques I used were DFS, BFS, recursion, and
random generator to figure out the order of nodes I should check
whether pruning should be done.  I determined whether a branch should
be pruned or not by changing a node terminality and label and seeing if accuracy
would increase from taking such action or not.  Result for bestPrune(stack implementation)
is .71779.  Result for pruneQueue (queue implementation) gave accuracy
of .67485.  Result for pruneStack (naive stack implementation) is also .67485.
Random implementation varies depending the number of iterations.  For the
majority of the time, is gave me a worse result compared to no pruning.
For tie breakers, I went with the majority label for entire train set.
I tried using a non-majority label and noticed that it gave me a worse
result. Iteration depth of 3 gave me the best results for my bestPrune method
