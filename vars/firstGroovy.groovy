import org.jenkinsci.plugins.pipeline.modeldefinition.Utils
import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.transform.Field

def call(body){
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def all_params = [:]
    pipeline{
        agent any
        stages{
            stage("Start"){
                steps{
                    println "Staring Stage"
                    all_params['stageName'] = "Running Stage"
                }
            }
            stage("Running"){
                steps{
                    println "$config"
                    all_params['stageName'] = "Pause"
                }
            }
            stage("Pause"){
                steps{
                    println "$config"
                    all_params['stageName'] = "End"
                }
            }
            stage("End"){
                steps{
                    println "$config"
                }
            }
        }
    }
}
