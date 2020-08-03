import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil
/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */

/** 사용자별 schema 위치 */
CUSTOM_DIR_SCHEMA = "/Users/wind/Library/Preferences/DataGrip2019.3/extensions/com.intellij.database/schema"

TEMPLATE_FILE = "$CUSTOM_DIR_SCHEMA/template/custom-vo.template"

packageName = "com.sample;"
typeMapping = [
  (~/(?i)int/)                      : "long",
  (~/(?i)float|double|decimal|real/): "double",
  (~/(?i)datetime|timestamp/)       : "Timestamp",
  (~/(?i)date/)                     : "Date",
  (~/(?i)time/)                     : "Time",
  (~/(?i)/)                         : "String"
]
FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
  SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
  def className = javaName(table.getName(), true)
  def fields = calcFields(table)
  new File(dir, className + ".java").withPrintWriter { out -> generate(out, className, fields) }
}

def generate(out, className, fields) {

    //== template binding:
    def binding = [:];
    binding.packageName = packageName;
    binding.className = className;
    binding.fields = fields;

    //== template:
    def f = new File("$TEMPLATE_FILE")
    def engine = new groovy.text.GStringTemplateEngine()
    def template = engine.createTemplate(f).make(binding)
    //
    out.println template.toString()


    //////////////
    //  out.println "package $packageName"
    //  out.println ""
    //   out.println ""
    //   out.println "public class $className {"
    //   out.println ""
    //   fields.each() {
    //     out.println " /**"
    //     out.println "  * ${it.comment}"
    //     out.println "  */"
    //     if (it.annos != "") out.println "  ${it.annos}"
    //     out.println "  private ${it.type} ${it.name};"
    //     out.println ""
    //   }
    //////////////
    //  out.println ""
    //  fields.each() {
    //    out.println ""
    //    out.println "  public ${it.type} get${it.name.capitalize()}() {"
    //    out.println "    return ${it.name};"
    //    out.println "  }"
    //    out.println ""
    //    out.println "  public void set${it.name.capitalize()}(${it.type} ${it.name}) {"
    //    out.println "    this.${it.name} = ${it.name};"
    //    out.println "  }"
    //    out.println ""
    //  }
    //  out.println "}"
    //////////////

}

def calcFields(table) {
  DasUtil.getColumns(table).reduce([]) { fields, col ->
    def spec = Case.LOWER.apply(col.getDataType().getSpecification())
    def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
    fields += [[
                 name : javaName(col.getName(), false),
                 comment : javaName(col.getComment(), false),
                 type : typeStr,
                 annos: ""]]
  }
}
def javaName(str, capitalize) {
    if(str == null)
	{
		str = "%no+comment%";
	}
  def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
    .collect { Case.LOWER.apply(it).capitalize() }
    .join("")
    .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
  capitalize || s.length() == 1? s : Case.LOWER.apply(s[0]) + s[1..-1]
}