import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("pulse.android.library")
                apply("pulse.android.hilt")
            }
            dependencies {
                add("implementation", project(":core:core-ui"))
                add("implementation", project(":core:core-model"))
                add("implementation", project(":core:core-common"))
            }
        }
    }
}