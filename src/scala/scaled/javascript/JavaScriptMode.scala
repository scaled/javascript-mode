//
// Scaled JavaScript Mode - a Scaled major mode for editing JavaScript code
// http://github.com/scaled/scala-mode/blob/master/LICENSE

package scaled.javascript

import scaled._
import scaled.code.{CodeConfig, Commenter}
import scaled.grammar._
import scaled.util.Paragrapher

@Plugin(tag="textmate-grammar")
class JavaScriptGrammarPlugin extends GrammarPlugin {
  import CodeConfig._

  override def grammars = Map("source.javascript" -> "JavaScript.ndf")

  override def effacers = List(
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

    // effacer("meta.definition.method.javascript", functionStyle),
    effacer("meta.method.javascript", functionStyle),

    effacer("storage.modifier.import", moduleStyle),
    effacer("storage.modifier", keywordStyle),
    effacer("storage.type.annotation", preprocessorStyle),
    effacer("storage.type.other", typeStyle),
    effacer("storage.type.def", keywordStyle),
    effacer("storage.type", keywordStyle),
    effacer("support.type", typeStyle),

    // effacer("variable.import", typeStyle),
    // effacer("variable.language", constantStyle),
    effacer("variable", variableStyle)
  )

  override def syntaxers = List(
    syntaxer("comment.line", Syntax.LineComment),
    syntaxer("comment.block", Syntax.DocComment),
    syntaxer("constant", Syntax.OtherLiteral),
    syntaxer("string", Syntax.StringLiteral)
  )
}

@Major(name="javascript",
       tags=Array("code", "project", "javascript"),
       pats=Array(".*\\.js", ".*\\.json"),
       ints=Array("javascript"),
       desc="A major editing mode for the JavaScript language.")
class JavaScriptMode (env :Env) extends GrammarCodeMode(env) {
  import CodeConfig._
  import scaled.util.Chars._

  override def langScope = "source.javascript"

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
