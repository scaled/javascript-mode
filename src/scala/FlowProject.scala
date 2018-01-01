//
// Scaled JavaScript Project Support - JavaScript project support for Scaled project framework.
// http://github.com/scaled/javascript-project/blob/master/LICENSE

package scaled.project

import java.nio.file.{Files, Path}
import java.util.function.Consumer
import scaled._
import scaled.util.{Close, MoreFiles}
import scaled.pacman.{Config => PConfig}

object FlowProject {

  // val ConfigFile = ".flowconfig"
  val ProjectFile = ".flowproject"

  @Plugin(tag="project-finder")
  class FinderPlugin extends ProjectFinderPlugin("flow", true, classOf[FlowProject]) {
    def checkRoot (root :Path) :Int = if (exists(root, ProjectFile)) 1 else -1
  }

  // used to trigger flow-mode
  class Tag
}

class FlowProject (ps :ProjectSpace, r :Project.Root) extends AbstractFileProject(ps, r) {
  import FlowProject._

  class MetaData (config :PConfig) {
    val name = configS("name")
    val sourceDirs = configSL("sourceDir")
    val ignoreNames = configSL("ignoreName")
    val ignoreRegexes = configSL("ignoreRegex")
    config.finish()
    private def configS (name :String) = config.resolve(name, PConfig.StringP)
    private def configSL (name :String) = config.resolve(name, PConfig.StringListP)
  }

  // use our ignores when enumerating sources
  override def onSources (op :Consumer[Path]) :Unit =
    MoreFiles.onFilteredFiles(sourceDirs, d => !ignore(d), op)

  private[this] val meta = new Close.Ref[MetaData](toClose) {
    protected def create = new MetaData(new PConfig(Files.readAllLines(configFile)))
  }
  private def rootPath = root.path
  private def configFile = rootPath.resolve(ProjectFile)

  // reinit if the config file changes
  toClose += metaSvc.service[WatchService].watchFile(configFile, file => reinit())

  override protected def computeMeta (oldMeta :Project.Meta) = {
    val sb = FileProject.stockIgnores
    meta.get.ignoreNames.foreach { sb += FileProject.ignoreName(_) }
    meta.get.ignoreRegexes.foreach { sb += FileProject.ignoreRegex(_) }
    ignores() = sb

    addComponent(classOf[Compiler], new FlowCompiler(this) {
      // override def flowOpts = FlowProject.this.flowOpts
      // override protected def willCompile () = copyResources()
    })

    Future.success(oldMeta.copy(
      name = meta.get.name,
      sourceDirs = meta.get.sourceDirs.map(rootPath.resolve(_)).toSeq
    ))
  }

  override def addToBuffer (buffer :RBuffer) {
    super.addToBuffer(buffer)
    buffer.state[Tag]() = new Tag
  }
}
