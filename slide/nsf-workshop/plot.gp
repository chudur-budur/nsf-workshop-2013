#!/usr/bin/gnuplot
#
# Plotting the data of file data.dat with points and non-continuous lines
#
# AUTHOR: Hagen Wierstorf
# VERSION: gnuplot 4.6 patchlevel 0

reset

# x11
# set terminal x11

# wxt
# set terminal wxt size 350,262 enhanced font 'Verdana,10' persist

# png
# set terminal pngcairo size 350,262 enhanced font 'Verdana,10'
# set output 'non-continuous_lines.png'

# eps
# set term postscript enhanced color
# set output "front.eps"

# color definitions
set border linewidth 1.5
# line color '#0060ad' -- navy
# line color '#228b22' -- lightgreen
# line color '#32cd32' -- lime green
# see more at: http://www.uni-hamburg.de/Wiss/FB/15/Sustainability/schneider/gnuplot/colors.htm
set style line 1 linecolor rgb '#0060ad' linetype 1 linewidth 2 pointtype 7 pointinterval -2 pointsize 2.5
set pointintervalbox 3

# set the red cricle
set object 1 circle at 0.75,5.46252e+09 size screen 0.025 fillcolor rgb "red"

# unset key

# set ytics 1
# set tics scale 0.75

# set xrange [0:5]
# set yrange [0:4]

set xlabel "Average Satisfaction of The Populace"
set ylabel "Amount of Money Skimmed by The Government"

plot 'front-1.stat' with linespoints ls 1 title "Pareto-Optimal Front"
