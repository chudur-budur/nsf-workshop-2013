#! /bin/tcsh

# EXAMPLE:
# ./doit master master-file slave-file output-dir pop-size num-tests

set master=$1
set masterfile=$2
set slavefile=$3
set outputdir=$4
set popsize=$5
set numtests=$6

set port=5004

# pre-cleanup
mapb killall java

# distribute the slaves
distribute 128 java ec.eval.Slave -file $slavefile \
	       -p eval.slave.name=`hostname -s` \
	       -p eval.master.host=$master \
               -p eval.master.port=$port \
               -p eval.slave.one-shot=false \

# just in case
ssh $master mkdir $outputdir


# run the master 10 times
foreach i (1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50)
	ssh $master java ec.Evolve -file $masterfile \
	-p generations=64 \
        -p eval.mater.port=$port \
	-p pop.subpop.0.size=$popsize \
	-p eval.num-tests=$numtests \
	-p stat.file=${outputdir}/${numtests}.${i}.stat \
	-p stat.do-per-generation-description=true \
	-p stat.num-children=1 \
	-p stat.child.0=ec.simple.SimpleShortStatistics \
	-p stat.child.0.file=${outputdir}/${numtests}.${i}.shortstat
end

# cleanup
mapb killall java

