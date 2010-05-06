#!/usr/bin/perl -w

`./../libsvm-2.9/svm-scale -r range1 test.1 > test.1.scale`;
`./../libsvm-2.9/svm-predict test.1.scale train.1.scale.model out`;
