package org.dvs;

def pipelineJob(config){
    echo "Hellp"
}

def execute(config){
    print(config)
    try {
        stage("build"){
            sh """
                    pwd;
                    ls -a;
                """
        }
    }catch (err){
        printf(err)
    }
}