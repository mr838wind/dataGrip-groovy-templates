/*
 * Available context bindings:
 *   COLUMNS     List<DataColumn>
 *   ROWS        Iterable<DataRow>
 *   OUT         { append() }
 *   FORMATTER   { format(row, col); formatValue(Object, col) }
 *   TRANSPOSED  Boolean
 * plus ALL_COLUMNS, TABLE, DIALECT
 *
 * where:
 *   DataRow     { rowNumber(); first(); last(); data(): List<Object>; value(column): Object }
 *   DataColumn  { columnNumber(), name() }
 */

import com.intellij.database.util.Case
import static com.intellij.openapi.util.text.StringUtil.escapeStringCharacters as escapeStr

NEWLINE = System.getProperty("line.separator")
INDENT = "  "

def printJSON(level, col, o) {
  switch (o) {
    case null: OUT.append("null"); break
    case Number:
      if (col != null) OUT.append(FORMATTER.formatValue(o, col))
      else OUT.append("$o")
      break
    case Boolean: OUT.append("$o"); break
    case String: OUT.append("\"${escapeStr(o)}\""); break
    case Tuple: printJSON(level, o[0], o[1]); break
    case Map:
      OUT.append("{")
      o.entrySet().eachWithIndex { entry, i ->
        OUT.append("${i > 0 ? "," : ""}$NEWLINE${INDENT * (level + 1)}")
        OUT.append("\"${escapeStr(entry.getKey().toString())}\"")
        OUT.append(": ")
        printJSON(level + 1, null, entry.getValue())
      }
      OUT.append("$NEWLINE${INDENT * level}}")
      break
    case Object[]:
    case Iterable:
      OUT.append("[")
      def plain = true
      o.eachWithIndex { item, i ->
        plain = item == null || item instanceof Number || item instanceof Boolean || item instanceof String
        if (plain) OUT.append(i > 0 ? ", " : "")
        else OUT.append("${i > 0 ? "," : ""}$NEWLINE${INDENT * (level + 1)}")
        printJSON(level + 1, null, item)
      }
      if (plain) OUT.append("]") else OUT.append("$NEWLINE${INDENT * level}]")
      break
    default:
      if (col != null) printJSON(level, null, FORMATTER.formatValue(o, col))
      else OUT.append("$o")
      break
  }
}



//== like camel case
def javaName(str, capitalize) {
    def s = ''

    def tmp = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
        .collect { Case.LOWER.apply(it).capitalize() }
    s = tmp.join("")
        .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")

    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}


printJSON(0, null, ROWS.transform { row ->
  def map = new LinkedHashMap<String, String>()
  COLUMNS.each { col ->
    def val = row.value(col)
    // java camel case instead of column name
    def fieldName = javaName(col.name(), false)
    map.put(fieldName, new Tuple(col, val))
    //map.put(col.name(), new Tuple(col, val))
  }
  map
})





