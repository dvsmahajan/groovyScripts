import org.jenkinsci.plugins.pipeline.modeldefinition.Utils
import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.transform.Field

def call(Closure body) {
    def config = [:] // Initialize an empty map

    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent any
        stages {
            stage("Start") {
                steps {
                    echo "Starting Stage"
                }
            }
            stage("Running") {
                steps {
                    echo "$config"
                }
            }
            stage("Pause") {
                steps {
                    echo "$config"
                }
            }
            stage("End") {
                steps {
                    echo "$config.name"
                    // config.name = "Done"
                    echo "$config.name"
                }
            }
        }
    }
}
