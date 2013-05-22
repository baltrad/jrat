#!/bin/bash
site="http://www.spaceweather.gc.ca/data-donnee/sol_flux/sx-5-flux-eng.php"
site_filename="site.tmp"

echo "Attempting to fetch data from "$site"..."
wget --no-parent -O "$site_filename" "$site" 
if [ -s "$site_filename" ]
then
	echo "Data fetched successfully."
else
	echo "Fetching data FAILED."
	exit
fi

echo -n "Extracting table..."
site_table=`grep -F "<tr><th scope='row'" $site_filename`
#table=`grep -F "<tr><th scope='row' headers='fluxdate'" table.tmp`
if [ "$site_table" != "" ]
then
	echo "OK: "
else
	echo "FAILED."
	exit
fi
#site_table=`eval cat "smalltest.tmp"` #for testing

fluxdate="fluxdate"
fluxtime="fluxtime"
fluxjulian="fluxjulian"
fluxcarrington="fluxcarrington"
fluxobs="fluxobs"
fluxadj="fluxadj"
fluxursi="fluxursi"
line_delimiter=";"

solar_flux_data_filename=`eval echo "DRAO_10.7cm_solar_flux_*.data"`
echo -n "Checking for previous DRAO solar flux data file..."
if [ -f "${solar_flux_data_filename}" ]
then
	tmp=$solar_flux_data_filename
	solar_flux_data_filename_base=${tmp%_*}
	solar_flux_data_filename_date=${tmp##*_}
	solar_flux_data_filename_date=${solar_flux_data_filename_date%.*}
	echo "FOUND file with date="$solar_flux_data_filename_date

	echo -n "Checking filename date with solar flux date..."

	solar_flux_table=`eval cat $solar_flux_data_filename`

	# ursi flux
	tmp=$solar_flux_table
	ursi_flux=${tmp##*${line_delimiter}}
#	echo "ursi_flux="$ursi_flux
	solar_flux_table=${solar_flux_table%${line_delimiter}*}

	# adj flux
	tmp=$solar_flux_table
	adj_flux=${tmp##*${line_delimiter}}
#	echo "adj_flux="$adj_flux
	solar_flux_table=${solar_flux_table%${line_delimiter}*}

	# obs flux
	tmp=$solar_flux_table
	obs_flux=${tmp##*${line_delimiter}}
#	echo "obs_flux="$obs_flux
	solar_flux_table=${solar_flux_table%${line_delimiter}*}

	# carrington number
	tmp=$solar_flux_table
	carrington_number=${tmp##*${line_delimiter}}
#	echo "carrington_number="$carrington_number
	solar_flux_table=${solar_flux_table%${line_delimiter}*}

	# julian day
	tmp=$solar_flux_table
	julian_day=${tmp##*${line_delimiter}}
#	echo "julian_day="$julian_day
	solar_flux_table=${solar_flux_table%${line_delimiter}*}
	
	# time
	tmp=$solar_flux_table
	time=${tmp##*${line_delimiter}}
#	echo "time="$time
	solar_flux_table=${solar_flux_table%${line_delimiter}*}

	# date
	tmp=$solar_flux_table
	tmp=${tmp##*${line_delimiter}}
	solar_flux_date=${tmp#*$'\n'}
#	echo "solar_flux_date="$solar_flux_date
	solar_flux_table=${solar_flux_table%${line_delimiter}*}

	solar_flux_date=${solar_flux_date//-/}
#	echo "solar_flux_date="$solar_flux_date

	solar_flux_data_filename_line_count=`cat $solar_flux_data_filename | wc -l`

	if [ "$solar_flux_data_filename_date" == "$solar_flux_date" ]
	then
		echo "OK: date="$solar_flux_data_filename_date" line count="$solar_flux_data_filename_line_count
	else
		echo "FAILED: filename date="$solar_flux_data_filename_date " solar_flux_date="$solar_flux_date
		exit
	fi
else
	echo "NO FILE."
	exit
fi


echo "Extracting new solar flux data:"
i=0
#echo -n '' >> $solar_flux_data_filename 
echo -n $'\n' >> $solar_flux_data_filename
insert_line_number=$(( $solar_flux_data_filename_line_count+1 ))
while :
	do

	# ursi flux
	tmp=$site_table
	tmp=${tmp##*${fluxursi}}
	tmp=${tmp#*">"}
	ursi_flux=${tmp%%"<"*}
#	echo "ursi_flux="$ursi_flux
	site_table=${site_table%${fluxursi}*}

	# adj flux
	tmp=$site_table
	tmp=${tmp##*${fluxadj}}
	tmp=${tmp#*">"}
	adj_flux=${tmp%%"<"*}
#	echo "adj_flux="$adj_flux
	site_table=${site_table%${fluxadj}*}

	# obs flux
	tmp=$site_table
	tmp=${tmp##*${fluxobs}}
	tmp=${tmp#*">"}
	obs_flux=${tmp%%"<"*}
#	echo "obs_flux="$obs_flux
	site_table=${site_table%${fluxobs}*}

	# carrington number
	tmp=$site_table
	tmp=${tmp##*${fluxcarrington}}
	tmp=${tmp#*">"}
	carrington_number=${tmp%%"<"*}
#	echo "carrington_number="$carrington_number
	site_table=${site_table%${fluxcarrington}*}

	# julian day
	tmp=$site_table
	tmp=${tmp##*${fluxjulian}}
	tmp=${tmp#*">"}
	julian_day=${tmp%%"<"*}
#	echo "julian_day="$julian_day
	site_table=${site_table%${fluxjulian}*}

	# time
	tmp=$site_table
	tmp=${tmp##*${fluxtime}}
	tmp=${tmp#*">"}
	time=${tmp%%"<"*}
#	echo "time="$time
	site_table=${site_table%${fluxtime}*}

	# date
	tmp=$site_table
	tmp=${tmp##*${fluxdate}}
	tmp=${tmp#*">"}
	date=${tmp%%"<"*}
#	echo "date="$date
	site_table=${site_table%${fluxdate}*}

	if [ "$solar_flux_date" != "${date//-/}" ] 
	then
		echo "New values found: date="$date" time="$time" --> appending to solar flux data file."
		line=""
		line+=${date}${line_delimiter}
		line+=${time}${line_delimiter}
		line+=${julian_day}${line_delimiter}
		line+=${carrington_number}${line_delimiter}
		line+=${obs_flux}${line_delimiter}
		line+=${adj_flux}${line_delimiter}
		line+=${ursi_flux}
	#	echo "line="$line

		sed -i "${insert_line_number} i ${line}" $solar_flux_data_filename

		if [ $i -eq "0" ]
		then
			new_filename_date=${date//-/}
		fi
		i=$(( $i+1 ))
	else
		break
	fi

done

echo -n "Removing site tmp file..."
rm $site_filename
echo "OK."

echo -n "Updating DRAO solar flux filename..."
grep -v '^$' $solar_flux_data_filename > $solar_flux_data_filename_base"_"$new_filename_date".data"
rm $solar_flux_data_filename
echo "OK."	

echo "Number of lines added: "$i
echo "DONE."
