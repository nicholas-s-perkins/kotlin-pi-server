WORKING_DIR="$(pwd)"
PI_ADDRESS="192.168.50.236"


SSH_KEY="${HOME}/.ssh/pi_id_rsa"

JAR_PATH="${WORKING_DIR}/target/kotlin-pi-server.jar"
LIB_PATH="${WORKING_DIR}/target/libs"

PI_USER="pi"
PI_FOLDER="/home/pi/app"
PI_USER_ADR="${PI_USER}@${PI_ADDRESS}"


# kill the program - kills java, a bit hacky but works for now
ssh -i ${SSH_KEY}  ${PI_USER_ADR} "sudo pkill java"

## COPY LIB
rsync -r -e "ssh -i ${SSH_KEY}" ${LIB_PATH} ${PI_USER_ADR}:${PI_FOLDER}
## COPY JAR
rsync -e "ssh -i ${SSH_KEY}" ${JAR_PATH} ${PI_USER_ADR}:${PI_FOLDER}

# start the jar, put output somewhere?  Don't want to lock up terminal with this script
ssh -i ${SSH_KEY}  ${PI_USER_ADR} "sudo java -jar ${PI_FOLDER}/kotlin-pi-server.jar > /dev/null 2>&1 &"
