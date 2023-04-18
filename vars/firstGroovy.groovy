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
