//
// Scaled JavaScript Project Support - JavaScript project support for Scaled project framework.
// http://github.com/scaled/javascript-project/blob/master/LICENSE

package scaled.project

import java.nio.file.{Files, Path}
import scaled._

object FlowPlugins {

  val ConfigFile = ".flowconfig"

  @Plugin(tag="project-root")
  class FlowRootPlugin extends RootPlugin.File(ConfigFile)

  @Plugin(tag="project-resolver")
  class FlowResolverPlugin extends ResolverPlugin {
    override def addComponents (project :Project) {
      val configFile = project.root.path.resolve(ConfigFile)
      if (Files.exists(configFile)) {
        project.addComponent(classOf[Compiler], new FlowCompiler(project))
      }
    }
  }
}

