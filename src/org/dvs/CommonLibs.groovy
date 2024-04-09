package org.dvs;

def pipelineJob(config){
    echo "Hellp"
}

def execute(config){
    print(config)
    try {
        isDeployment = config['deployment'];
        isService = config['service'];
        isDocker = config['docker'];
        isDeploy = config['deploy'];

        if(isDeploy){
            stage('Prepare'){
                sh " echo $WORKSPACE"
                sh "pwd; ls -a;"
                sshagent(['masterSSHID']){

                    sh """
                    echo "I am trying to connect the server"
                    ssh  -o StrictHostKeyChecking=no user@192.168.0.102 "rm -rf /home/user/app ; mkdir /home/user/app ; cd /home/user/app; pwd ; ls -a" 
                    """
                }
            }
            stage('Clone Repo') {
                script {
                    sh 'cd $WORKSPACE; rm -rf micro-eureka; git clone https://github.com/dvsmahajan/micro-eureka.git ; cd micro-eureka;'
                }
            }
            stage('Build'){
                sh "pwd; ls -a"
                sh "cd $WORKSPACE; cd micro-eureka; mvn clean install"
            }

            stage('Deploy'){

                print("Deploying the application in server")

                sh """sshpass -p "user" scp -o PreferredAuthentications="password"  micro-eureka/target/eureka-1.war user@192.168.0.102:/home/user/app/; """

                if(isDocker){
                    sh """sshpass -p "user" scp -o PreferredAuthentications="password"  micro-eureka/Dockerfile user@192.168.0.102:/home/user/app/; """
                }

                print("Deploying the deployement yaml in server")

                if(isDeployment){
                    sh """sshpass -p "user" scp -o PreferredAuthentications="password" micro-eureka/deployment.yaml  user@192.168.0.102:/home/user/app/;"""
                }

                print("Deploying the Service yaml in server")

                if(isService){
                    sh """sshpass -p "user" scp -o PreferredAuthentications="password" micro-eureka/service.yaml   user@192.168.0.102:/home/user/app/;"""

                }
            }
        }
        if(isDocker){
            stage("Docker Image"){
                sshagent(['masterSSHID']){

                    sh """
                    echo "I am trying to connect the server"
                    ssh  -o StrictHostKeyChecking=no user@192.168.0.102 " cd /home/user/app; echo 'Trying to create docker image'; docker build -t eureka . " 
                    """
                }
            }

            stage("Image Status"){
                sshagent(['masterSSHID']){

                    sh """
                    echo "I am trying to connect the server"
                    ssh  -o StrictHostKeyChecking=no user@192.168.0.102 " docker images  " 
                    """
                }
            }
        }

    }catch (err){
        print(err)
        throw err;
    }
}