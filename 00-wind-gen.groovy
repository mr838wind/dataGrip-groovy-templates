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

//=============== [s] inputs ===============
INPUT = [:]  //empty map

//= table 이름 t_ 제거
INPUT.REMOVE_TABLE_PREFIX = true

//= 사용자별 template 위치
//INPUT.TEMPLATE_BASE = "C:/Users/mr838/.DataGrip2018.3/config/extensions/com.intellij.database/schema/template"
INPUT.TEMPLATE_BASE = "/Users/wind/Library/Preferences/DataGrip2019.3/extensions/com.intellij.database/schema/template"

//= package base name:
INPUT.packageNameBase = "com.shinsegae.villiv"

//==
INPUT.ITEMS = [:]
INPUT.ITEMS.DTO = [
    filePrefix : '',
    fileSuffix : 'DTO.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-dto.template",
    packageName : INPUT.packageNameBase + ".dto",
]
INPUT.ITEMS.SEARCH_CRITERIA = [
    filePrefix : '',
    fileSuffix : 'SearchCriteria.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-SearchCriteria.template",
    packageName : INPUT.packageNameBase + ".dto",
]
INPUT.ITEMS.MYBATIS = [
    filePrefix : 'sql-',
    fileSuffix : 'Mapper.xml',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-mybatis.template",
    packageName : '',
]
INPUT.ITEMS.DAO = [
    filePrefix : '',
    fileSuffix : 'DAO.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-dao.template",
    packageName : INPUT.packageNameBase + ".dao",
]
INPUT.ITEMS.MAPPER = [
    filePrefix : '',
    fileSuffix : 'Mapper.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-mapper.template",
    packageName : INPUT.packageNameBase + ".dao",
]
INPUT.ITEMS.SERVICE = [
    filePrefix : '',
    fileSuffix : 'Service.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-service.template",
    packageName : INPUT.packageNameBase + ".service",
]
INPUT.ITEMS.CONTROLLER = [
    filePrefix : '',
    fileSuffix : 'Controller.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-controller.template",
    packageName : INPUT.packageNameBase + ".controller",
]
//=============== [e] inputs ===============


//=============== [s] main ===============
allTables = new HashMap<>()
allFields = new HashMap<>()
allPkInfos = new HashMap<>()

typeMapping = [
        (~/(?i)int/)                      : "Integer",
        (~/(?i)long/)                     : "Long",
        (~/(?i)number/)                   : "Integer",  //""String",
        (~/(?i)float|double|decimal|real/): "Double",
        (~/(?i)datetime|timestamp/)       : "java.util.Date", //"java.sql.Timestamp",
        (~/(?i)date/)                     : "java.util.Date", //"java.sql.Date",
        (~/(?i)time/)                     : "java.util.Date", //"java.sql.Time",
        (~/(?i)/)                         : "String"
]

INSERT_FIELD_LIST = ["IPT_ID", "IPT_DTM", "IPT_IP"]
UPDATE_FIELD_LIST = ["UPD_ID", "UPD_DTM", "UPD_IP"]


//== template binding map
def getDefaultBinding(className, fields, table) {
    //== template binding:
    def binding = INPUT.clone()

    binding.className = className
    binding.classNameLower = Case.LOWER.apply(className)
    binding.fields = fields
    binding.table = table
    binding.tableComment = table.getComment()
    binding.sqlNs = "sql-${className}"
    binding.fullDTO = "${INPUT.ITEMS.DTO.packageName}.${className}DTO"
    binding.fullSearchCriteria = "${INPUT.ITEMS.DTO.packageName}.${className}SearchCriteria"
    binding.pkInfo = allPkInfos[className]
    return binding
}

FILES.chooseDirectoryAndSave("Choose entity directory", "Choose where to store generated files") { dir ->
        //== fetch all tables
        SELECTION.filter { it instanceof DasTable && it.getKind() == ObjectKind.TABLE }.each {
            fetchAllDbInfo(it, dir)
        }

        //== generate for each
        INPUT.ITEMS.each {  entry ->
            def item = entry.value
            allTables.each { className, table ->
                def newDir = new File(dir, "${className}")
                if(!newDir.exists()) {
                    newDir.mkdirs();
                }
                new File(newDir, "${item.filePrefix}" + className + "${item.fileSuffix}").withPrintWriter { out ->
                    def fields = allFields[className];
                    _generateItem(out, className, fields, table, "${item.template}" )
                }
            }
        }
}

//=============== [e] main ===============

def fetchAllDbInfo(table, dir) {
    className = javaName(table.getName(), true)
    def fields = calcFields(table, className)
    allTables.put(className, table)
    allFields.put(className, fields)
    allPkInfos.put(className, getPkInfo(fields) )
}

// [not used]
def getPkInfo(fields) {
    def pkDbName = ''
    def pkName = ''
    fields.each { it ->
        if(it.isPk) {
            pkDbName = it.dbName
            pkName = it.name
        }
    }
    return [name:pkName, dbName:pkDbName]
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

// dtoTypeStr:  날짜는 String으로
def getDtoTypeStr(typeStr) {
    if(typeStr == "java.util.Date") {
        return "String"
    } else {
        return typeStr
    }
}


// fields
def calcFields(table, javaName) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        def dtoTypeStr = getDtoTypeStr(typeStr)     //dto 에 사용할 타입
        def isDateType = (typeStr == "java.util.Date" ? true : false)   //날짜 타입 yn
        def isInsertField = INSERT_FIELD_LIST.contains(col.getName())   // insert column
        def isUpdateField = UPDATE_FIELD_LIST.contains(col.getName())   // update column

        def comm = [
                isPk : false,
                dbName : col.getName(),
                name : columnName(col.getName()),
                type : typeStr,
                dtoTypeStr : dtoTypeStr,
                isDateType : isDateType,
                isInsertField : isInsertField,
                isUpdateField : isUpdateField,
                annos: ["@Column(name = \"" + col.getName() + "\" )"],
                comment: col.getComment()
        ]
        if (table.getColumnAttrs(col).contains(DasColumn.Attribute.PRIMARY_KEY)) {
            comm.isPk = true
            comm.annos += ["@Id"]
            comm.annos += ["@GeneratedValue(strategy = GenerationType.IDENTITY)"]
        }
        fields += [comm]
    }
}


def javaName(str, capitalize) {
    def s = ''

    def tmp = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
        .collect { Case.LOWER.apply(it).capitalize() }
    if(INPUT.REMOVE_TABLE_PREFIX) {
        //remove first prefix like t_
        tmp = tmp.subList(1,tmp.size())
    }
    s = tmp.join("")
        .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")

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
