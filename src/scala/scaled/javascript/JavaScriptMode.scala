//
// Scaled JavaScript Mode - a Scaled major mode for editing JavaScript code
// http://github.com/scaled/scala-mode/blob/master/LICENSE

package scaled.javascript

import scaled._
import scaled.code.{CodeConfig, Commenter}
import scaled.grammar.{Grammar, GrammarConfig, GrammarCodeMode}
import scaled.util.Paragrapher

object JavaScriptConfig extends Config.Defs {
  import CodeConfig._
  import GrammarConfig._

  // map TextMate grammar scopes to Scaled style definitions
  val effacers = List(
    effacer("comment.line", commentStyle),
    effacer("comment.block", docStyle),
    effacer("constant", constantStyle),
    effacer("invalid", invalidStyle),
    effacer("keyword", keywordStyle),
    effacer("string", stringStyle),

    effacer("entity.name.package", moduleStyle),
    effacer("entity.name.class", typeStyle),
    effacer("entity.name.type", typeStyle),
    effacer("entity.other.inherited-class", typeStyle),
    effacer("entity.name.function", functionStyle),
    effacer("entity.name.val-declaration", variableStyle),

    effacer("support.type", typeStyle),

    // effacer("meta.definition.method.javascript", functionStyle),
    effacer("meta.method.javascript", functionStyle),

    effacer("storage.modifier.import", moduleStyle),
    effacer("storage.modifier", keywordStyle),
    effacer("storage.type.annotation", preprocessorStyle),
    effacer("storage.type.def", keywordStyle),
    effacer("storage.type", keywordStyle),

    // effacer("variable.import", typeStyle),
    // effacer("variable.language", constantStyle),
    effacer("variable", variableStyle)
  )

  // map TextMate grammar scopes to Scaled syntax definitions
  val syntaxers = List(
    syntaxer("comment.line", Syntax.LineComment),
    syntaxer("comment.block", Syntax.DocComment),
    syntaxer("constant", Syntax.OtherLiteral),
    syntaxer("string.quoted.double", Syntax.StringLiteral)
  )

  val grammars = resource(Seq("HTML.ndf", "JavaDoc.ndf", "JavaScript.ndf"))(Grammar.parseNDFs)
}

@Major(name="javascript",
       tags=Array("code", "project", "javascript"),
       pats=Array(".*\\.js", ".*\\.json"),
       ints=Array("javascript"),
       desc="A major editing mode for the JavaScript language.")
class JavaScriptMode (env :Env) extends GrammarCodeMode(env) {
  import CodeConfig._
  import scaled.util.Chars._

  override def configDefs = JavaScriptConfig :: super.configDefs

  override def grammars = JavaScriptConfig.grammars.get
  override def effacers = JavaScriptConfig.effacers
  override def syntaxers = JavaScriptConfig.syntaxers

  override protected def createIndenter = new JavaScriptIndenter(config)

  override val commenter = new Commenter() {
    override def linePrefix  = "//"
    override def blockOpen = "/*"
    override def blockPrefix = "*"
    override def blockClose = "*/"
    override def docOpen   = "/**"
  }

  // TODO: more things!
}
