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
                sshagent(['masterSSHID']){

                    sh """
                    echo "I am trying to connect the server"
                    ssh  -o StrictHostKeyChecking=no user@192.168.0.102 "rm -rf /home/user/app ; mkdir /home/user/app ; cd /home/user/app; pwd ; ls -a" 
                    """
                }
            }
            stage('Build'){
                sh "mvn clean install"
            }

            stage('Deploy'){

                print("Deploying the application in server")

                sh """sshpass -p "user" scp -o PreferredAuthentications="password"  target/eureka-1.war user@192.168.0.102:/home/user/app/; """

                print("Deploying the deployement yaml in server")

                if(isDeployment){
                    sh """sshpass -p "user" scp -o PreferredAuthentications="password" doployment.yaml  user@192.168.0.102:/home/user/app/;"""
                }

                print("Deploying the Service yaml in server")

                if(isService){
                    sh """sshpass -p "user" scp -o PreferredAuthentications="password" service.yaml   user@192.168.0.102:/home/user/app/;"""

                }
            }
        }

    }catch (err){
        print(err)
        throw err;
    }
}