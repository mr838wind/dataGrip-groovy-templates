import com.intellij.database.model.DasTable
import com.intellij.database.model.DasColumn
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil


/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */

//=============== [s] vars ===============
INPUT = [:]  //empty map

//table 이름 t_ 제거
INPUT.REMOVE_TABLE_PREFIX = true

//사용자별 template 위치
//INPUT.TEMPLATE_BASE = "C:/Users/mr838/.DataGrip2018.3/config/extensions/com.intellij.database/schema/template"
INPUT.TEMPLATE_BASE = "/Users/wind/Library/Preferences/DataGrip2019.3/extensions/com.intellij.database/schema/template"

//==
INPUT.TEMPLATE_DTO = INPUT.TEMPLATE_BASE + "/wind-gen-dto.template"
INPUT.TEMPLATE_MYBATIS = INPUT.TEMPLATE_BASE + "/wind-gen-mybatis.template"
INPUT.TEMPLATE_DAO = INPUT.TEMPLATE_BASE + "/wind-gen-dao.template"
INPUT.TEMPLATE_SERVICE = INPUT.TEMPLATE_BASE + "/wind-gen-service.template"
INPUT.TEMPLATE_CONTROLLER = INPUT.TEMPLATE_BASE + "/wind-gen-controller.template"

//==
INPUT.packageNameBase = "com.test"
INPUT.packageNameDTO = INPUT.packageNameBase + ".dto"
INPUT.packageNameDAO = INPUT.packageNameBase + ".dao"
INPUT.packageNameService = INPUT.packageNameBase + ".service"
INPUT.packageNameController = INPUT.packageNameBase + ".controller"

//=============== [e] vars ===============


//=============== [s] main ===============
allTables = new HashMap<>()
allFields = new HashMap<>()

typeMapping = [
        (~/(?i)int/)                      : "Integer",
        (~/(?i)long/)                     : "Long",
        (~/(?i)number/)                   : "String",
        (~/(?i)float|double|decimal|real/): "Double",
        (~/(?i)datetime|timestamp/)       : "java.sql.Timestamp",
        (~/(?i)date/)                     : "java.sql.Date",
        (~/(?i)time/)                     : "java.sql.Time",
        (~/(?i)/)                         : "String"
]

//== template binding map
def getDefaultBinding(className, fields, table) {
    //== template binding:
    def binding = INPUT.clone()

    binding.className = className
    binding.classNameLower = Case.LOWER.apply(className)
    binding.fields = fields
    binding.table = table
    binding.sqlNs = "sql-${className}"
    binding.fullDTO = "${INPUT.packageNameDTO}.${className}DTO"
    return binding
}

FILES.chooseDirectoryAndSave("Choose entity directory", "Choose where to store generated files") { dir ->
        //== fetch all tables
        SELECTION.filter { it instanceof DasTable && it.getKind() == ObjectKind.TABLE }.each {
            fetchAllDbInfo(it, dir)
        }
        //== dto
        allTables.each { className, table ->
            new File(dir, className + "DTO.java").withPrintWriter { out ->
                def fields = allFields[className];
                _generateItem(out, className, fields, table, "$INPUT.TEMPLATE_DTO" )
            }
        }
        //== mybatis
        allTables.each { className, table ->
            new File(dir, "sql-" + className + ".xml").withPrintWriter { out ->
                def fields = allFields[className];
                _generateItem(out, className, fields, table, "$INPUT.TEMPLATE_MYBATIS" )
            }
        }
        //== dao
        allTables.each { className, table ->
            new File(dir, className + "DAO.java").withPrintWriter { out ->
              def fields = allFields[className];
              _generateItem(out, className, fields, table, "$INPUT.TEMPLATE_DAO" )
            }
        }
        //== service
        allTables.each { className, table ->
            new File(dir, className + "Service.java").withPrintWriter { out ->
              def fields = allFields[className];
              _generateItem(out, className, fields, table, "$INPUT.TEMPLATE_SERVICE" )
            }
        }
        //== controller
        allTables.each { className, table ->
            new File(dir, className + "Controller.java").withPrintWriter { out ->
                def fields = allFields[className];
                _generateItem(out, className, fields, table, "$INPUT.TEMPLATE_CONTROLLER" )
            }
        }
}

//=============== [e] main ===============

def fetchAllDbInfo(table, dir) {
    className = javaName(table.getName(), true)
    def fields = calcFields(table, className)
    allTables.put(className, table)
    allFields.put(className, fields)
}

def _generateItem(out, className, fields, table, templatePath) {

    def binding = getDefaultBinding(className, fields, table)

    //== template:
    def f = new File(templatePath)
    def engine = new groovy.text.GStringTemplateEngine()
    def template = engine.createTemplate(f).make(binding)
    //
    out.println template.toString()
}


// fields
def calcFields(table, javaName) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        def comm = [
                pk : false,
                dbName : col.getName(),
                name : columnName(col.getName()),
                type : typeStr,
                annos: ["@Column(name = \"" + col.getName() + "\" )"],
                comment: col.getComment()
        ]
        if (table.getColumnAttrs(col).contains(DasColumn.Attribute.PRIMARY_KEY)) {
            comm.pk = true
            comm.annos += ["@Id"]
            comm.annos += ["@GeneratedValue(strategy = GenerationType.IDENTITY)"]
        }
        fields += [comm]
    }
}


def javaName(str, capitalize) {
    def s = ''
    if(INPUT.REMOVE_TABLE_PREFIX) {
        s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .subList(1, 2) // remove prefix like t_
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    } else {
        s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    }
    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

def columnName(str) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    s.length() == 1? s : Case.LOWER.apply(s[0]) + s[1..-1]
}


def generateComment(out, comment = null) {
    out.println "/**"
    out.println " * " + comment
    out.println " *"
    out.println " * <p>Date: " + new java.util.Date().toString() + "</p>"
    out.println " */"
}

def toLowerCaseFirstOne(s){
    if(Character.isLowerCase(s.charAt(0)))
        return s;
    else
        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
}

static String genSerialID()
{
    return "    private static final long serialVersionUID =  " + Math.abs(new Random().nextLong())+"L;"
}
