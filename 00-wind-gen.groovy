package com.wind

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

//= 사용자별 template 위치
//INPUT.TEMPLATE_BASE = "C:/Users/mr838/AppData/Roaming/JetBrains/DataGrip2020.3/extensions/com.intellij.database/schema/template"
INPUT.TEMPLATE_BASE = "/Users/wind/Library/Preferences/DataGrip2019.3/extensions/com.intellij.database/schema/template"
INPUT.CONFIG = INPUT.TEMPLATE_BASE + '/../00-wind-gen.config'

//== read from prop
Properties prop = loadProp(INPUT.CONFIG)

//= table 이름 t_ 제거
INPUT.REMOVE_TABLE_PREFIX = "true".equals(prop.REMOVE_TABLE_PREFIX_STRING)

//= 업무 이름 *******: (bizNameDynamicYn: table명 두번째 필드로 task 이름 계산)
INPUT.bizNameDynamicYn = "true".equals(prop.bizNameDynamicYnString)
INPUT.bizName = prop.bizName

//= package base name:
INPUT.packageNameBase = prop.packageNameBase



//==
INPUT.ITEMS = [:]
INPUT.ITEMS.Info = [
    filePrefix : '',
    fileSuffix : '-info.md',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-00-Info.template",
    packageName : '', //calc by sub
    subPackageName : "info",
    subPath: '00-info',
]
INPUT.ITEMS.DTO = [
    filePrefix : '',
    fileSuffix : 'DTO.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-10-dto.template",
    packageName : '', //calc by sub
    subPackageName : "dto",
    subPath: 'dto',
]
INPUT.ITEMS.SEARCH_CRITERIA = [
    filePrefix : '',
    fileSuffix : 'SearchCriteria.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-12-SearchCriteria.template",
    packageName : '', //calc by sub
    subPackageName : "dto",
    subPath: 'dto',
]
INPUT.ITEMS.MYBATIS = [
    filePrefix : 'sql-',
    fileSuffix : 'Mapper.xml',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-20-mybatis.template",
    packageName : '', //calc by sub
    subPackageName : "",
    subPath: 'mybatis',
]

INPUT.ITEMS.MAPPER = [
    filePrefix : '',
    fileSuffix : 'Mapper.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-30-dao-mapper.template",
    packageName : '', //calc by sub
    subPackageName : "dao",
    subPath: 'dao',
]
INPUT.ITEMS.SERVICE = [
    filePrefix : '',
    fileSuffix : 'Service.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-40-service.template",
    packageName : '', //calc by sub
    subPackageName : "service",
    subPath: 'service',
]
INPUT.ITEMS.CONTROLLER = [
    filePrefix : '',
    fileSuffix : 'Controller.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-50-controller.template",
    packageName : '', //calc by sub
    subPackageName : "controller",
    subPath: 'controller',
]
INPUT.ITEMS.ServiceTest = [
    filePrefix : '',
    fileSuffix : 'ServiceTest.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-60-ServiceTest.template",
    packageName : '', //calc by sub
    subPackageName : "service",
    subPath: 'test_service',
]
INPUT.ITEMS.ControllerTest = [
    filePrefix : 'Test',
    fileSuffix : 'Controller.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-62-ControllerTest.template",
    packageName : '', //calc by sub
    subPackageName : "mvc",
    subPath: 'test_controller',
]

//== angular: backoffice templates
INPUT.ITEMS.UiModel = [
    filePrefix : '',
    fileSuffix : '.model.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-70-UiModel.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiServiceEnum = [
    filePrefix : '',
    fileSuffix : '.enum.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-71-UiServiceEnum.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiServiceFile = [
    filePrefix : '',
    fileSuffix : '.service.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-72-UiServiceFile.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiServiceFile = [
    filePrefix : '',
    fileSuffix : '.service.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-72-UiServiceFile.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiPageTs = [
    filePrefix : '',
    fileSuffix : '.page.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-73-UiPageTs.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiPageHtml = [
    filePrefix : '',
    fileSuffix : '.page.html',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-74-UiPageHtml.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiPageScss = [
    filePrefix : '',
    fileSuffix : '.page.scss',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-75-UiPageScss.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

//=============== [e] inputs ===============


//=============== [s] main ===============
allTables = new HashMap<>()
allFields = new HashMap<>()

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

// ui project type: angular
uiTypeMapping = [
        (~/(?i)int/)                      : "number",
        (~/(?i)long/)                     : "number",
        (~/(?i)number/)                   : "number",
        (~/(?i)float|double|decimal|real/): "number",
        (~/(?i)datetime|timestamp/)       : "string", //"java.sql.Timestamp",
        (~/(?i)date/)                     : "string", //"java.sql.Date",
        (~/(?i)time/)                     : "string", //"java.sql.Time",
        (~/(?i)/)                         : "string"
]

INSERT_FIELD_LIST = ["IPT_ID", "IPT_DTM", "IPT_IP"]
UPDATE_FIELD_LIST = ["UPD_ID", "UPD_DTM", "UPD_IP"]


//== template binding map
def getDefaultBinding(className, fields, table) {
    calcPackageName()

    //== template binding:
    def binding = INPUT.clone()


    def nonPkFields = fields.findAll { !it.isPk }
    def pkFields = fields.findAll { it.isPk }
    def nonPkNoInsertFields = nonPkFields.findAll { !it.isInsertField }

    binding.className = className
    binding.classNameLower = javaName(table.getName(), false)
    //
    binding.fields = fields
    binding.pkFields = pkFields
    binding.nonPkFields = nonPkFields
    binding.nonPkNoInsertFields = nonPkNoInsertFields
    binding.pkFieldsFirst = pkFields.size() > 0 ? pkFields[0] : [:]
    //
    binding.table = table
    binding.tableComment = table.getComment()
    binding.sqlNs = "sql-${className}"
    binding.fullDTO = "${INPUT.ITEMS.DTO.packageName}.${className}DTO"
    binding.fullSearchCriteria = "${INPUT.ITEMS.DTO.packageName}.${className}SearchCriteria"

    return binding
}

//== bizName calculate: second word
def calcBizName(table) {
    def bizName = "biz"
    def str = table.getName()

    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }

    bizName = (s.size() == 1)? Case.LOWER.apply(s[0]) : Case.LOWER.apply(s[1])

    INPUT.bizName = bizName;
}

//== packageName calculate
def calcPackageName() {
    INPUT.ITEMS.each { key, val ->
        val.packageName = INPUT.packageNameBase + "." + INPUT.bizName + "." + val.subPackageName
    }
}

//== properties
def loadProp(filePath) {
    Properties prop = new Properties()
    File propertiesFile = new File(filePath)
    propertiesFile.withInputStream {
        prop.load(it)
    }

    // test
    assert prop.a == '1'

    return prop;
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

                //== 업무이름 계산: table명 두번째 필드로
                if(INPUT.bizNameDynamicYn) {
                    calcBizName(table)
                }

                //== package별로
                def baseDir = new File(dir, "${INPUT.bizName}")
                if(!baseDir.exists()) {
                    baseDir.mkdirs();
                }
                def newDir = new File(baseDir, "${item.subPath}")
                if(!newDir.exists()) {
                    newDir.mkdirs();
                }

                //== file name 처리
                def classNameLower = javaName(table.getName(), false)
                def fileClassName = className
                if(item.fileUseClassNameLower) {
                    fileClassName = classNameLower
                }
                def fileName = "${item.filePrefix}" + fileClassName + "${item.fileSuffix}"

                new File(newDir, "${fileName}").withPrintWriter { out ->
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
        def uiTypeStr = uiTypeMapping.find { p, t -> p.matcher(spec).find() }.value  // ui type: angular
        def dtoTypeStr = getDtoTypeStr(typeStr)     //dto 에 사용할 타입
        def isDateType = (typeStr == "java.util.Date" ? true : false)   //날짜 타입 yn
        def isInsertField = INSERT_FIELD_LIST.contains(col.getName())   // insert column
        def isUpdateField = UPDATE_FIELD_LIST.contains(col.getName())   // update column

        def comm = [
                isPk : false,
                dbName : col.getName(),
                name : columnName(col.getName()),
                type : typeStr,
                uiTypeStr : uiTypeStr,
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


//== like camel case
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

//== like camel case
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


//== not used : do not know how to bind method in template
class WindUtils {
    static toCamelCase( String text, boolean capitalized = false ) {
        text = text.replaceAll( "(_)([A-Za-z0-9])", { Object[] it -> it[2].toUpperCase() } )
        return capitalized ? capitalize(text) : text
    }

    static toSnakeCase( String text ) {
        return text.replaceAll( /([A-Z])/, /_$1/ ).toLowerCase().replaceAll( /^_/, '' )
    }

}
