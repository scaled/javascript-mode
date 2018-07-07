//
// Scaled JavaScript Mode - a Scaled major mode for editing JavaScript code
// http://github.com/scaled/javascript-mode/blob/master/LICENSE

package scaled.code

import scaled._
import scaled.grammar._

@Plugin(tag="textmate-grammar")
class GraphQLGrammarPlugin extends GrammarPlugin {
  import CodeConfig._

  override def grammars = Map("source.graphql" -> "GraphQL.ndf")

  override def effacers = List(
    effacer("comment.line", commentStyle),
    effacer("comment.block", docStyle),
    effacer("constant", constantStyle),
    effacer("invalid", invalidStyle),
    effacer("keyword", keywordStyle),
    effacer("string", stringStyle),
    effacer("support.type", typeStyle),
    effacer("variable", variableStyle)
  )

  override def syntaxers = List(
    syntaxer("comment.line", Syntax.LineComment),
    syntaxer("comment.block", Syntax.DocComment),
    syntaxer("constant", Syntax.OtherLiteral),
    syntaxer("string", Syntax.StringLiteral)
  )
}

@Major(name="graphql",
       tags=Array("code", "project", "graphql"),
       pats=Array(".*\\.graphql", ".*\\.gql"),
       ints=Array("graphql"),
       desc="A major editing mode for GraphQL schemas.")
class GraphQLMode (env :Env) extends GrammarCodeMode(env) {
  import CodeConfig._
  import scaled.util.Chars._

  override def langScope = "source.graphql"

  override protected def createIndenter = new BlockIndenter(config, Seq(
    new BlockIndenter.BlockCommentRule()
  ));

  override val commenter = new Commenter() {
    override def linePrefix  = "//"
    override def blockOpen = "/*"
    override def blockPrefix = "*"
    override def blockClose = "*/"
    override def docOpen   = "/**"
  }

  // TODO: more things!

}
