@echo off

echo Starting clock: http://localhost:3030/
start java -jar ClockWebServer.jar

pause

echo Starting Inference Module Sample using Jena: http://localhost:40000/snorql
start java -jar InferenceModule.jar explo-jena.config.xml

echo Starting Inference Module Sample using Pellet: http://localhost:50000/snorql
start java -jar InferenceModule.jar explo-pellet.config.xml

pause
