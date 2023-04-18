import org.jenkinsci.plugins.pipeline.modeldefinition.Utils
import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.transform.Field

def call(body){
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    pipeline{
        agent any
        stages{
            stage("Start"){
                steps{
                    println "Staring Stage"
                    config.stageName = "Running Stage"
                }
            }
            stage("${config.stageName}"){
                steps{
                    println "$config.stageName"
                    config.stageName = "Pause"
                }
            }
            stage("${config.stageName}"){
                steps{
                    println "$config.stageName"
                    config.stageName = "End"
                }
            }
            stage("${config.stageName}"){
                steps{
                    println "$config.stageName"
                }
            }
        }
    }
}
