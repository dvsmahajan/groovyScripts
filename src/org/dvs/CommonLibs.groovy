package org.dvs;

def pipelineJob(config){
    echo "Hellp"
}

def execute(config){
    print(config)
    try {
//        stage("build"){
//            sh """
//                    pwd;
//                    ls -a;
//                    mvn clean install;
//                """
//        }
        stage('validate server'){
            sshagent(['masterSSHID']){

                sh """
                    echo "I am trying to connect the server"
                    ssh  -o StrictHostKeyChecking=no user@192.168.0.102 "pwd;ls -a;" 
                    """
            }
        }
    }catch (err){
        print(err)
        throw err;
    }
}