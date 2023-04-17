def call(){
    pipeline{
        agent any
        stages{
            stage("Start"){
                steps{
                    println "Staring Stage"
                }
            }
            stage("End"){
                steps{
                    println "Ending Stage"
                }
            }
        }
    }
}
