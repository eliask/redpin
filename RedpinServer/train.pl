#!/usr/bin/perl -w

`./libsvm-2.9/svm-scale -l -1 -u 1 -s range1 train.1 > train.1.scale`;
`./libsvm-2.9/svm-train -c 32 -t 0 train.1.scale > train.1.scale.model`;
