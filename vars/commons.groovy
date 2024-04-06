import org.jenkinsci.plugins.pipeline.modeldefinition.Utils
import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.transform.Field

def call(body) {
     def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def all_params = [:]
    node(){
        try {
                stage("Start"){
                        println "Staring Stage"

                }
                stage("Running"){
                        println "$config"

                }
                stage("Pause"){
                        println "$config"
//                    commonLib = new common-libs();
                        common-libs.pipelineJob(config)
                }
                stage("End"){
                        println "$config.name"
                        // config.name = "Done"
                        println "$config.name"
                }

        }catch (err){
            print("Exception occur "+err)
        }
    }
}