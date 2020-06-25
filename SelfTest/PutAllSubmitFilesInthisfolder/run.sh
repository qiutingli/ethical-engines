#!/bin/sh

#
# COMP90041 Semester 1, 2015
# Project A Submission Script
# Jianzhong Qi
#
# Modified from:
#
# Submission Script for 520 the Unisys project(projB)
# Semester 2, 2005
# Saeed Araban
#

cwd=`pwd`
name=`basename $cwd`
visible=./vis/$name.OUT
invisible=./invis/$name.OUT

axinvis=8
count=0
testcount=1

entryfile=EthicalEngine

limiter="rlimit -t 20 -f 20k -c 0"

#jdk="/usr/java1.6/bin/javac"
#jre="/usr/java1.6/bin/java"

jdk="javac"
jre="java"


if [ ! -d vis ] ; then
        mkdir vis
else
        rm -f vis/*
fi
#
if [ ! -d invis ] ; then
        mkdir invis
else
        rm -f invis/*
fi

#echo 'a' > vis/ye.txt
#echo 'b' > invis/a.txt


PATH=/usr/java1.6/bin:$PATH; export PATH

# CREATE DIRECTORIES
#

if [ ! -d ethicalengine ] ; then
	mkdir ethicalengine
else
	rm -f ethicalengine/*
fi

file="ethicalengine.txt"
while IFS= read -r line
do
  mv $line ethicalengine/
done < "$file"

echo "Feedback:" > vis/result.rtx

#mv Animal.java ethicalengine/
#mv Person.java ethicalengine/
#mv Character.java ethicalengine/
#mv Scenario.java ethicalengine/
#mv ScenarioGenerator.java ethicalengine/
$jdk ethicalengine/*.java >> vis/result.rtx

#mv ../data/Test* ./



# Remove pre-existing result.rtxd classes
rm -f *.class

# check that entry file exists

if [ ! -f $entryfile.java ] ; then
        echo "Oops, wrong file names!!!" >> vis/result.rtx
        echo "You must submit files named: \"$entryfile.java\ " >> vis/result.rtx
        exit 0

else
    	echo "Your programs (\"$entryfile.java\" ) have been submitted successfully. " >> vis/result.rtx
        echo "Compiling your programs with \"$jdk\"..." >> vis/result.rtx
        javac  *.java  >> vis/result.rtx 2>&1
fi

if [ -f $entryfile.class ]; then
        echo "Your programs have been compiled successfully :-)" >> vis/result.rtx
        echo "Now, it's your responsibility to test your programs for correctness!" >> vis/result.rtx
        echo "Here are some public test cases, and the results:" >> vis/result.rtx
        count=1;
        for f in ../data/in? ; do
                file=`basename $f`
                parm=`head -1 $f`
                java EthicalEngine -i -c ../data/config$count <$f > invis/out$count 2>&1
                echo "============================================" >> vis/result.rtx
                echo "Test for input file: public Test$count" >>vis/result.rtx
                echo "Expected results:                                                       Your results:" >> vis/result.rtx
                if sdiff -w 140 ../data/out$count invis/out$count \
                        >>vis/result.rtx ; then
                        echo "Your results seem to be CORRECT :-)." >> vis/result.rtx
                else
                    	echo "Oops, your results seem to be INCORRECT :-(" >> vis/result.rtx
                fi
                count=`expr "$count" + 1`
        done

        count=1;

else
    	echo "Fix these errors and resubmit your programs!" >> vis/result.rtx
fi
