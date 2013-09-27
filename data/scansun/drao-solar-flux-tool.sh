#!/bin/bash
green="\e[1;32m"
red="\e[1;31m"
nc="\e[0m"
on_success="DONE"
on_fail="FAIL"

spinstr='/-\|'
let maxcol=$(tput cols)-8

function spin(){
#	$1 - current counter value
#	$2 - max counter value
#	$3 - counter delta

	binnum=$(( $1/$3 ))
	spincounter=$(( $binnum%${#spinstr} ))	
	progress=$(( 100*$1/$2 ))

	let col2=6
	let col1=${maxcol}-${#msg}-${#col2}

	echo -ne
	printf "\r%s%${col1}s %${col2}s" "${msg}" "${spinstr:spincounter:1}" "[${progress}%]"
}

function startspin(){
#	$1 - msg displayed when spinning
	msg="$1"
}

function endspin(){
#	$1 - process exit status
	echo -ne "\r${msg}"
	printf "%$(( ${maxcol}-${#msg} ))s" 
	if [ $1 -eq 0 ]
	then
		echo -e "[${green}${on_success}${nc}]"
	else
		echo -e "[${red}${on_fail}${nc}]"i
	fi
}

draofluxtablename="draofluxtable.txt"
outfilename="DRAO-solar-flux-table.data"

#echo "Searching for previous files..."
if [ -s "$draofluxtablename" ]
then
	echo -n "FOUND file $draofluxtablename -> removing... "

	rm $draofluxtablename
	[ $? -eq 0 ] && echo -e "${green}${on_success}${nc}" || echo -e "${red}${on_fail}${nc}"
fi

if [ -s "$outfilename" ]
then
	echo -n "FOUND file $outfilename -> removing... "
	rm $outfilename
	[ $? -eq 0 ] && echo -e "${green}${on_success}${nc}" || echo -e "${red}${on_fail}${nc}"
fi
#echo "All files cleared."

ftp="ftp.geolab.nrcan.gc.ca/data/solar_flux/daily_flux_values/fluxtable.txt"

echo "Attempting to fetch file: ${ftp}..."
wget -O "$draofluxtablename" ftp://"$ftp"
if [ "$?" -eq 0 ] && [ -s "$draofluxtablename" ] 
then
	echo -e "Data fetched ${green}SUCCESSFULLY${nc}."
else
	echo "Fetching data ${red}FAILED${nc}."
	exit
fi

delimiter=";"

startspin "Creating file $outfilename ..."
linecount=$(cat $draofluxtablename | wc -l)
i=0
while read line
do
	linearray=( $line )
	if [ ${linearray[0]} -eq ${linearray[0]} ] 2>/dev/null
	then
		#data
		outline[0]="${linearray[0]:0:4}-${linearray[0]:4:2}-${linearray[0]:6:2}" #fluxdate
		outline[1]="${linearray[1]:0:2}:${linearray[1]:2:2}:${linearray[1]:4:2}" #fluxtime
		outline[2]="${linearray[2]}" #fluxjulian
		outline[3]="${linearray[3]}" #fluxcarrington
		outline[4]="${linearray[4]}" #fluxobsflux
		outline[5]="${linearray[5]}" #fluxadjflux
		outline[6]="${linearray[6]}" #fluxursi
		for e in ${outline[@]}
		do
			echo -n "${e};" >> $outfilename
		done
		echo >> $outfilename
	else
		#not data
		echo "#"${line} | sed "s/ /$delimiter/g" >> $outfilename
	fi
	(( i++ ))
	spin "$i" "$linecount" "250"
done < $draofluxtablename
endspin $?

startspin "Removing tmp files..."
if [ -s $draofluxtablename ]
then
	rm $draofluxtablename
#	[ $? -eq 0 ] && echo -e "${green}${on_success}${nc}" || echo -e "${red}${on_fail}${nc}"
fi
endspin $?
