//
// Scaled JavaScript Project Support - JavaScript project support for Scaled project framework.
// http://github.com/scaled/javascript-project/blob/master/LICENSE

package scaled.project

import java.nio.file.{Files, Path, Paths}
import scaled._
import scaled.util.BufferBuilder

object Flow {

  val NpmConfig = "package.json"
  val FlowConfig = ".flowconfig"

  def flowBin (project :Project) = project.component[FlowBin].map(_.flow) || Paths.get("flow")

  /** A component added to a project to indicate that we found a `flow` binary for it. */
  case class FlowBin (flow :Path) extends Project.Component {
    override def describeSelf (bb :BufferBuilder) :Unit = {
      bb.addSection("Flow")
      bb.addKeyValue("Binary", s" $flow")
    }
  }

  @Plugin(tag="project-root")
  class NpmRootPlugin extends RootPlugin.File(NpmConfig)

  @Plugin(tag="project-resolver")
  class FlowResolverPlugin extends ResolverPlugin {
    override def metaFiles (root :Project.Root) = Seq(root.path.resolve(NpmConfig))

    override def addComponents (project :Project) :Unit = {
      // if they have a .flowconfig use flow for type checking
      if (Files.exists(project.root.path.resolve(FlowConfig))) {
        project.addComponent(classOf[Compiler], new FlowCompiler(project))
      }

      // try to find a Flow binary for use by our extractor and flow-mode
      val flowPaths = Seq(
        project.root.path.resolve("node_modules").resolve(".bin").resolve("flow"),
        Paths.get("/usr/local/bin/flow")
      )
      flowPaths.find(Files.exists(_)) foreach { path =>
        println(s"Using flow at $path")
        project.addComponent(classOf[FlowBin], FlowBin(path))
      }
    }
  }
}
