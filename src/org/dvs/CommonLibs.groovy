package org.dvs;


def execute(config){
    print(config)
    try {
        appName= config['name'];
        isDeployment = config['deployment'];
        isService = config['service'];
        isDocker = config['docker'];
        isDeploy = config['deploy'];
        repoUrl = config['repo'];
        jarName = config['jarName'];
        serverIp = "192.168.0.102"
        deployIp = "192.168.0.101"
        archiveType = config['archiveType']
        if(isDeploy){
            stage('Prepare'){
                sh " echo $WORKSPACE"
                sh "pwd; ls -a;"
                sshagent(['masterSSHID']){

                    sh """
                    echo "I am trying to connect the server"
                    ssh  -o StrictHostKeyChecking=no user@192.168.0.102 "rm -rf /home/user/app ; mkdir /home/user/app ; cd /home/user/app; pwd ; ls -a" 
                     ssh  -o StrictHostKeyChecking=no user@192.168.0.101 "echo 'I am inside 101' ;rm -rf /home/user/app ; mkdir /home/user/app ; cd /home/user/app; pwd ; ls -a" 
                    """
                }
            }
            stage('Clone Repo') {
                cloneRepo(appName,repoUrl);
            }
            stage('Build'){
                buildMavenProject(appName)
            }

            stage('Deploy'){
                print("Deploying the application in server")
                copyJarToServer(appName,jarName,archiveType)

                if(isDocker){
                    path = "$WORKSPACE/$appName/Dockerfile";
                    destinationPath = "user@$deployIp:/home/user/app/;"
                    copyFile(path, destinationPath);
                }

                if(isDeployment){
                    path = "$WORKSPACE/$appName/deployment.yaml";
                    destinationPath = "user@$serverIp:/home/user/app/;"
                    copyFile(path, destinationPath);
                }
                if(isService){
                    path = "$WORKSPACE/$appName/service.yaml";
                    destinationPath = "user@$serverIp:/home/user/app/;"
                    copyFile(path, destinationPath);
                }
            }
        }
        if(isDocker){
            stage("Docker Image"){
                sshagent(['masterSSHID']){
                    buildDockerImage(jarName,deployIp)
                }
            }

            if(isDeployment){
                stage("Deployment"){
                    sshagent(['masterSSHID']){
                        applyKubernates(serverIp, "deployment.yaml")
                    }
                }
            }

            if(isService){
                stage("Service"){
                    sshagent(['masterSSHID']){
                        applyKubernates(serverIp, "service.yaml")
                    }
                }
            }

        }

    }catch (err){
        print(err)
        throw err;
    }
}

def buildMavenProject(appName){
    sh "pwd; ls -a"
    sh "cd $WORKSPACE; cd $appName; mvn clean install"
}

def cloneRepo(appName,repoUrl){
    print("APP NAME :: $appName, REPO URL:: $repoUrl")
    script {
        sh "cd $WORKSPACE; rm -rf $appName; git clone $repoUrl ; cd $appName;"
    }
}

def copyJarToServer(appName,jarName,archiveType){
    sh """sshpass -p "user" scp -o PreferredAuthentications="password"  $WORKSPACE/$appName/target/$jarName-1.$archiveType user@192.168.0.101:/home/user/app/; """
}

def copyFile(path, destinationPath){
    sh """sshpass -p "user" scp -o PreferredAuthentications="password"  $path $destinationPath """
}

def buildDockerImage(jarName, deployIp){
    sh """
    echo "I am trying to connect the server"
    ssh  -o StrictHostKeyChecking=no user@$deployIp " cd /home/user/app; echo 'Trying to create docker image'; docker build -t $jarName . ; docker image tag $jarName  localhost:5000/$jarName; docker push localhost:5000/$jarName" 
    """
}

def applyKubernates(serverIp, file){
    sh """
        ssh  -o StrictHostKeyChecking=no user@$serverIp " cd /home/user/app; kubectl apply -f $file" 
    """
}
