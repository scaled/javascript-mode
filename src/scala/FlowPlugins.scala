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
    override def metaFiles (root :Project.Root) = Seq(root.path.resolve(ConfigFile))
    override def addComponents (project :Project) {
      project.addComponent(classOf[Compiler], new FlowCompiler(project))
    }
  }
}

