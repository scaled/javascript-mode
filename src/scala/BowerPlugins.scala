//
// Scaled JavaScript Mode - a Scaled major mode for editing JavaScript code
// http://github.com/scaled/javascript-mode/blob/master/LICENSE

package scaled.project

import com.eclipsesource.json._
import java.nio.file.Files
import scaled._

/** Plugins to extract project metadata from `bower.json` files. */
object BowerPlugins {

  val BowerFile = "bower.json"

  @Plugin(tag="project-root")
  class BowerRootPlugin extends RootPlugin.File(BowerFile)

  @Plugin(tag="project-resolver")
  class BowerResolverPlugin extends ResolverPlugin {

    override def metaFiles (root :Project.Root) = Seq(root.path.resolve(BowerFile))

    def addComponents (project :Project) {
      val rootPath = project.root.path
      val bowerFile = rootPath.resolve(BowerFile)
      val config = Json.parse(Files.newBufferedReader(bowerFile)).asObject

      val projName = Option(config.get("name")).map(_.asString).
        getOrElse(rootPath.getFileName.toString)

      val sb = Ignorer.stockIgnores
      Option(config.get("ignore")).map(_.asArray).foreach { ignores =>
        // TODO: handle glob ignores properly
        ignores.map(_.asString).foreach { sb += Ignorer.ignoreName(_) }
      }
      project.addComponent(classOf[Filer], new DirectoryFiler(project, sb))

      // TODO: bower doesn't define source directories, so we hack some stuff
      val sourceDirs = Seq("src", "test").map(rootPath.resolve(_))
      project.addComponent(classOf[Sources], new Sources(sourceDirs))

      val oldMeta = project.metaV()
      project.metaV() = oldMeta.copy(name = projName)
    }
  }
}
