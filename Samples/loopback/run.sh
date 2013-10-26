start() {

	if [ $(env | grep XDG_CURRENT_DESKTOP | wc -l) -eq 0 ]
	then
		echo GUI not recognized
		#$@ 2>&1 | awk '{printf "[TOP] %s\n",$0}; fflush(stdout)' &
		$@ &
	else
		echo GUI ok
		gnome-terminal -x "$@"
	fi
}


echo Starting clock: http://localhost:3030/
start java -jar ../ClockWebServer.jar


sleep 5


echo Starting Inference Module Sample using Jena: http://localhost:40000/snorql
start java -jar ../InferenceModule.jar explo-jena.config.xml



echo Starting Inference Module Sample using Pellet: http://localhost:50000/snorql
start java -jar ../InferenceModule.jar explo-pellet.config.xml
