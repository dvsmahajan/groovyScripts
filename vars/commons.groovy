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
            def commonLibs = new org.dvs.CommonLibs();
            commonLibs.execute(config)

        }catch (err){
            print("Exception occur "+err)
        }
    }
}